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

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.util.UnclosableWriter;
import org.apache.taglibs.standard.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
    private XmlUtil.JstlEntityResolver entityResolver;
    private XmlUtil.JstlUriResolver uriResolver;


    //*********************************************************************
    // Constructor and initialization

    public TransformSupport() {
        init();
    }

    private void init() {
        xml = xslt = null;
        xmlSpecified = false;
        xmlSystemId = xsltSystemId = null;
        var = null;
        result = null;
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    @Override
    public int doStartTag() throws JspException {
        // set up transformer in the start tag so that nested <param> tags can set parameters directly
        if (xslt == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_NULL"));
        }

        Source source;
        try {
            if (xslt instanceof Source) {
                source = (Source) xslt;
            } else if (xslt instanceof String) {
                String s = (String) xslt;
                s = s.trim();
                if (s.length() == 0) {
                    throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_EMPTY"));
                }
                source = XmlUtil.newSAXSource(new StringReader(s), xsltSystemId, entityResolver);
            } else if (xslt instanceof Reader) {
                source = XmlUtil.newSAXSource((Reader) xslt, xsltSystemId, entityResolver);
            } else {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_UNSUPPORTED_TYPE", xslt.getClass()));
            }
        } catch (SAXException e) {
            throw new JspException(e);
        } catch (ParserConfigurationException e) {
            throw new JspException(e);
        }

        try {
            t = XmlUtil.newTransformer(source);
            t.setURIResolver(uriResolver);
        } catch (TransformerConfigurationException e) {
            throw new JspTagException(e);
        }
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {

        try {
            Source source = xmlSpecified ? getSourceFromXmlAttribute() : getSourceFromBodyContent();

            // Conduct the transformation
            if (var != null) {
                // Save the result to var.
                Document d = XmlUtil.newEmptyDocument();
                Result doc = new DOMResult(d);
                t.transform(source, doc);
                pageContext.setAttribute(var, d, scope);
            } else {
                // Write to out if result is not specified.
                Result out = result;
                if (out == null) {
                    out = new StreamResult(new UnclosableWriter(pageContext.getOut()));
                }
                t.transform(source, out);
            }
            return EVAL_PAGE;
        } catch (TransformerException ex) {
            throw new JspException(ex);
        } catch (SAXException e) {
            throw new JspException(e);
        } catch (ParserConfigurationException e) {
            throw new JspException(e);
        } finally {
            t = null;
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
        uriResolver = pageContext == null ? null : new XmlUtil.JstlUriResolver(pageContext);
        entityResolver = pageContext == null ? null : new XmlUtil.JstlEntityResolver(pageContext);
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
     * Return the Source for a document specified in the "doc" or "xml" attribute.
     *
     * @return the document Source
     * @throws JspTagException if there is a problem with the attribute
     */
    Source getSourceFromXmlAttribute() throws JspTagException, SAXException, ParserConfigurationException {
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
            return XmlUtil.newSAXSource(new StringReader(s), xmlSystemId, entityResolver);
        }
        if (xml instanceof Reader) {
            return XmlUtil.newSAXSource((Reader) xml, xmlSystemId, entityResolver);
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
    Source getSourceFromBodyContent() throws JspTagException, SAXException, ParserConfigurationException {
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
        return XmlUtil.newSAXSource(new StringReader(s), xmlSystemId, entityResolver);
    }


    //*********************************************************************
    // Tag attributes

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }
}
