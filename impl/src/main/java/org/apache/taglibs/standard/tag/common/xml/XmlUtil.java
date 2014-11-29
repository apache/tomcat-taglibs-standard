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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.util.UrlUtil;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utilities for working with JAXP and SAX.
 */
public class XmlUtil {
    private static final DocumentBuilderFactory dbf;
    private static final SAXTransformerFactory stf;

    static {
        // from Java5 on DocumentBuilderFactory is thread safe and hence can be cached
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new AssertionError("Parser does not support secure processing");
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        if (!(tf instanceof SAXTransformerFactory)) {
            throw new AssertionError("TransformerFactory does not support SAX");
        }
        try {
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            throw new AssertionError("TransformerFactory does not support secure processing");
        }
        stf = (SAXTransformerFactory) tf;
    }


    /**
     * Create a new empty document.
     *
     * This method always allocates a new document as its root node might be
     * exposed to other tags and potentially be mutated.
     *
     * @return a new empty document
     */
    static Document newEmptyDocument() {
        return newDocumentBuilder().newDocument();
    }

    /**
     * Create a new DocumentBuilder configured for namespaces but not validating.
     *
     * @return a new, configured DocumentBuilder
     */
    static DocumentBuilder newDocumentBuilder() {
        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError();
        }
    }

    /**
     * Create a new TransformerHandler.
     * @return a new TransformerHandler
     */
    static TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        return stf.newTransformerHandler();
    }

    static Transformer newTransformer(Source source) throws TransformerConfigurationException {
        Transformer transformer = stf.newTransformer(source);
        // Although newTansformer() is not meant to, Xalan returns null if the XSLT is invalid
        // rather than throwing TransformerConfigurationException. Trap that here.
        if (transformer == null) {
            throw new TransformerConfigurationException("newTransformer returned null");
        }
        return transformer;
    }

    /**
     * Create an InputSource from a Reader.
     *
     * The systemId will be wrapped for use with JSTL's EntityResolver and UriResolver.
     *
     * @param reader the source of the XML
     * @param systemId the system id
     * @return a configured InputSource
     */
    static InputSource newInputSource(Reader reader, String systemId) {
        InputSource source = new InputSource(reader);
        source.setSystemId(wrapSystemId(systemId));
        return source;
    }

    /**
     * Create an XMLReader that resolves entities using JSTL semantics.
     * @param entityResolver for resolving using JSTL semamtics
     * @return a new XMLReader
     * @throws SAXException if there was a problem creating the reader
     */
    static XMLReader newXMLReader(JstlEntityResolver entityResolver) throws SAXException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setEntityResolver(entityResolver);
        return xmlReader;
    }

    /**
     * Create a SAXSource from a Reader. Any entities will be resolved using JSTL semantics.
     *
     * @param reader the source of the XML
     * @param systemId the system id
     * @param entityResolver for resolving using JSTL semamtics
     * @return a new SAXSource
     * @throws SAXException if there was a problem creating the source
     */
    static SAXSource newSAXSource(Reader reader, String systemId, JstlEntityResolver entityResolver)  throws SAXException {
        SAXSource source = new SAXSource(newXMLReader(entityResolver), new InputSource(reader));
        source.setSystemId(wrapSystemId(systemId));
        return source;
    }

    /**
     * Wraps systemId with a "jstl:" prefix to prevent the parser from
     * thinking that the URI is truly relative and resolving it against
     * the current directory in the filesystem.
     */
    private static String wrapSystemId(String systemId) {
        if (systemId == null) {
            return "jstl:";
        } else if (UrlUtil.isAbsoluteUrl(systemId)) {
            return systemId;
        } else {
            return ("jstl:" + systemId);
        }
    }

    /**
     * JSTL-specific implementation of EntityResolver.
     */
    static class JstlEntityResolver implements EntityResolver {
        private final PageContext ctx;

        public JstlEntityResolver(PageContext ctx) {
            this.ctx = ctx;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws FileNotFoundException {

            // pass if we don't have a systemId
            if (systemId == null) {
                return null;
            }

            // strip leading "jstl:" off URL if applicable
            if (systemId.startsWith("jstl:")) {
                systemId = systemId.substring(5);
            }

            // we're only concerned with relative URLs
            if (UrlUtil.isAbsoluteUrl(systemId)) {
                return null;
            }

            // for relative URLs, load and wrap the resource.
            // don't bother checking for 'null' since we specifically want
            // the parser to fail if the resource doesn't exist
            String path = systemId;
            if (!path.startsWith("/")) {
                String pagePath = ((HttpServletRequest) ctx.getRequest()).getServletPath();
                String basePath = pagePath.substring(0, pagePath.lastIndexOf("/"));
                path =  basePath + "/" + systemId;
            }

            InputStream s = ctx.getServletContext().getResourceAsStream(path);
            if (s == null) {
                throw new FileNotFoundException(Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY", systemId));
            }
            return new InputSource(s);
        }
    }

    /**
     * JSTL-specific implementation of URIResolver.
     */
    static class JstlUriResolver implements URIResolver {
        private final PageContext ctx;

        public JstlUriResolver(PageContext ctx) {
            this.ctx = ctx;
        }

        public Source resolve(String href, String base) throws TransformerException {

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
            if (UrlUtil.isAbsoluteUrl(href)
                    || (base != null && UrlUtil.isAbsoluteUrl(base))) {
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
            if (!target.startsWith("/")) {
                String pagePath = ((HttpServletRequest) ctx.getRequest()).getServletPath();
                String basePath = pagePath.substring(0, pagePath.lastIndexOf("/"));
                target = basePath + "/" + target;
            }
            InputStream s = ctx.getServletContext().getResourceAsStream(target);
            if (s == null) {
                throw new TransformerException(Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY", href));
            }
            return new StreamSource(s);
        }
    }
}
