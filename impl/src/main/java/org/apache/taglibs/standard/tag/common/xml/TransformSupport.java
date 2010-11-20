/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.taglibs.standard.tag.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>Support for tag handlers for &lt;transform&gt;, the XML transformation
 * tag.</p>
 *
 * @author Shawn Bayern
 */
public abstract class TransformSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected Object xml;               // attribute
    protected boolean xmlSpecified;     // true if xml attribute was specified
    protected String xmlSystemId;       // attribute
    protected Object xslt;              // attribute
    protected String xsltSystemId;      // attribute
    protected Result result;            // attribute

    //*********************************************************************
    // Private state

    private String var;                 // 'var' attribute
    private int scope;                  // processed 'scope' attr
    private Transformer t;              // actual Transformer
    private TransformerFactory tf;      // reusable factory
    private DocumentBuilder db;         // reusable factory


    //*********************************************************************
    // Constructor and initialization

    public TransformSupport() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            db = dbf.newDocumentBuilder();
            tf = TransformerFactory.newInstance();
        } catch (ParserConfigurationException e) {
            throw (AssertionError) new AssertionError("Unable to create DocumentBuilder").initCause(e);
        }

        init();
    }

    private void init() {
        xml = xslt = null;
        xmlSpecified = false;
        xmlSystemId = xsltSystemId = null;
        var = null;
        result = null;
        tf.setURIResolver(null);
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    @Override
    public int doStartTag() throws JspException {
        // set up transformer in the start tag so that nested <param> tags can set parameters directly
        t = getTransformer(xslt, xsltSystemId);
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        Source source = xmlSpecified ? getSourceFromXmlAttribute() : getDocumentFromBodyContent();

        try {
            //************************************
            // Conduct the transformation

            // we can assume at most one of 'var' or 'result' is specified
            if (result != null)
            // we can write directly to the Result
            {
                t.transform(source, result);
            } else if (var != null) {
                // we need a Document
                Document d = db.newDocument();
                Result doc = new DOMResult(d);
                t.transform(source, doc);
                pageContext.setAttribute(var, d, scope);
            } else {
                Result page = new StreamResult(new SafeWriter(pageContext.getOut()));
                t.transform(source, page);
            }
            return EVAL_PAGE;
        } catch (TransformerException ex) {
            throw new JspException(ex);
        }
    }

    // Releases any resources we may have (or inherit)

    @Override
    public void release() {
        super.release();
        init();
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        tf.setURIResolver(pageContext == null ? null : new JstlUriResolver(pageContext));
    }


    //*********************************************************************
    // Public methods for subtags

    /**
     * Sets (adds) a transformation parameter on our transformer.
     */
    public void addParameter(String name, Object value) {
        t.setParameter(name, value);
    }


    //*********************************************************************
    // Utility methods

    /**
     * Wraps systemId with a "jstl:" prefix to prevent the parser from
     * thinking that the URI is truly relative and resolving it against
     * the current directory in the filesystem.
     */
    private static String wrapSystemId(String systemId) {
        if (systemId == null) {
            return "jstl:";
        } else if (ImportSupport.isAbsoluteUrl(systemId)) {
            return systemId;
        } else {
            return ("jstl:" + systemId);
        }
    }

    /**
     * Create a Transformer from the xslt attribute.
     *
     * @param xslt     the xslt attribute
     * @param systemId the systemId for the transform
     * @return an XSLT transformer
     * @throws JspException if there was a problem creating the transformer
     */
    Transformer getTransformer(Object xslt, String systemId) throws JspException {
        if (xslt == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_NULL"));
        }
        Source source;
        if (xslt instanceof Source) {
            source = (Source) xslt;
        } else {
            if (xslt instanceof String) {
                String s = (String) xslt;
                s = s.trim();
                if (s.length() == 0) {
                    throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_EMPTY"));
                }
                xslt = new StringReader(s);
            }
            if (xslt instanceof Reader) {
                source = getSource((Reader) xslt, systemId);
            } else {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_UNSUPPORTED_TYPE", xslt.getClass()));
            }
        }
        try {
            return tf.newTransformer(source);
        } catch (TransformerConfigurationException e) {
            throw new JspTagException(e);
        }
    }

    /**
     * Return the Source for a document specified in the "doc" or "xml" attribute.
     *
     * @return the document Source
     * @throws JspTagException if there is a problem with the attribute
     */
    Source getSourceFromXmlAttribute() throws JspTagException {
        Object xml = this.xml;
        if (xml == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_XML_IS_NULL"));
        }

        // other JSTL XML tags may produce a list
        if (xml instanceof List) {
            List<?> list = (List<?>) xml;
            if (list.size() != 1) {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XML_LIST_SIZE"));
            }
            xml = list.get(0);
        }

        if (xml instanceof Source) {
            return (Source) xml;
        }
        if (xml instanceof String) {
            String s = (String) xml;
            s = s.trim();
            if (s.length() == 0) {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XML_IS_EMPTY"));
            }
            return getSource(new StringReader(s), xmlSystemId);
        }
        if (xml instanceof Reader) {
            return getSource((Reader) xml, xmlSystemId);
        }
        if (xml instanceof Node) {
            return new DOMSource((Node) xml, xmlSystemId);
        }
        throw new JspTagException(Resources.getMessage("TRANSFORM_XML_UNSUPPORTED_TYPE", xml.getClass()));
    }

    /**
     * Return the Source for a document specified as body content.
     *
     * @return the document Source
     * @throws JspTagException if there is a problem with the body content
     */
    Source getDocumentFromBodyContent() throws JspTagException {
        if (bodyContent == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_IS_NULL"));
        }
        String s = bodyContent.getString();
        if (s == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_CONTENT_IS_NULL"));
        }
        s = s.trim();
        if (s.length() == 0) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_IS_EMPTY"));
        }
        return getSource(new StringReader(s), xmlSystemId);
    }

    /**
     * Create a Source from a Reader
     *
     * @param reader   the Reader to read
     * @param systemId the systemId for the document
     * @return a SAX Source
     * @throws JspTagException if there is a problem creating the Source
     */
    Source getSource(Reader reader, String systemId) throws JspTagException {
        try {
            // explicitly go through SAX to maintain control
            // over how relative external entities resolve
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setEntityResolver(new ParseSupport.JstlEntityResolver(pageContext));
            InputSource s = new InputSource(reader);
            s.setSystemId(wrapSystemId(systemId));
            Source source = new SAXSource(xr, s);
            source.setSystemId(wrapSystemId(systemId));
            return source;
        } catch (SAXException e) {
            throw new JspTagException(e);
        }
    }


    //*********************************************************************
    // Tag attributes

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Private utility classes

    /**
     * A Writer based on a wrapped Writer but ignoring requests to
     * close() and flush() it.  (Someone must have wrapped the
     * toilet in my office similarly...)
     */
    private static class SafeWriter extends Writer {
        // TODO: shouldn't we be delegating all methods?
        private Writer w;

        public SafeWriter(Writer w) {
            this.w = w;
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            w.write(cbuf, off, len);
        }
    }

    //*********************************************************************
    // JSTL-specific URIResolver class

    /**
     * Lets us resolve relative external entities.
     */
    private static class JstlUriResolver implements URIResolver {
        private final PageContext ctx;

        public JstlUriResolver(PageContext ctx) {
            this.ctx = ctx;
        }

        public Source resolve(String href, String base)
                throws TransformerException {

            // pass if we don't have a systemId
            if (href == null) {
                return null;
            }

            // remove "jstl" marker from 'base'
            // NOTE: how 'base' is determined varies among different Xalan
            // xsltc implementations
            int index;
            if (base != null && (index = base.indexOf("jstl:")) != -1) {
                base = base.substring(index + 5);
            }

            // we're only concerned with relative URLs
            if (ImportSupport.isAbsoluteUrl(href)
                    || (base != null && ImportSupport.isAbsoluteUrl(base))) {
                return null;
            }

            // base is relative; remove everything after trailing '/'
            if (base == null || base.lastIndexOf("/") == -1) {
                base = "";
            } else {
                base = base.substring(0, base.lastIndexOf("/") + 1);
            }

            // concatenate to produce the real URL we're interested in
            String target = base + href;

            // for relative URLs, load and wrap the resource.
            // don't bother checking for 'null' since we specifically want
            // the parser to fail if the resource doesn't exist
            InputStream s;
            if (target.startsWith("/")) {
                s = ctx.getServletContext().getResourceAsStream(target);
                if (s == null) {
                    throw new TransformerException(
                            Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY",
                                    href));
                }
            } else {
                String pagePath =
                        ((HttpServletRequest) ctx.getRequest()).getServletPath();
                String basePath =
                        pagePath.substring(0, pagePath.lastIndexOf("/"));
                s = ctx.getServletContext().getResourceAsStream(
                        basePath + "/" + target);
                if (s == null) {
                    throw new TransformerException(
                            Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY",
                                    href));
                }
            }
            return new StreamSource(s);
        }
    }

}
