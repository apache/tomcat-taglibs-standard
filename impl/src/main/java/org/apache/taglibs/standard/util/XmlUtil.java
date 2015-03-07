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
package org.apache.taglibs.standard.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
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
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Utilities for working with JAXP and SAX.
 */
public class XmlUtil {
    /* Cache factory classes when this class is initialized (since Java1.5 factories are required
     * to be thread safe).
     *
     * As JavaEE 5 requires JSTL to be provided by the container we use our ClassLoader to locate
     * the implementations rather than the application's. As we don't know the actual implementation
     * class in use we can't use the newInstance() variant that allows the ClassLoader to be
     * specified so we use the no-arg form and coerce the TCCL (which may be restricted by the
     * AccessController).
     */
    private static final DocumentBuilderFactory PARSER_FACTORY;
    private static final SAXTransformerFactory TRANSFORMER_FACTORY;
    private static final SAXParserFactory SAXPARSER_FACTORY;
    static {
        try {
            PARSER_FACTORY = runWithOurClassLoader(new Callable<DocumentBuilderFactory>() {
                public DocumentBuilderFactory call() throws ParserConfigurationException {
                    return DocumentBuilderFactory.newInstance();
                }
            }, ParserConfigurationException.class);
            PARSER_FACTORY.setNamespaceAware(true);
            PARSER_FACTORY.setValidating(false);
            PARSER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            TRANSFORMER_FACTORY = runWithOurClassLoader(new Callable<SAXTransformerFactory>() {
                public SAXTransformerFactory call() throws TransformerConfigurationException {
                    TransformerFactory tf = TransformerFactory.newInstance();
                    if (!(tf instanceof SAXTransformerFactory)) {
                        throw new TransformerConfigurationException("TransformerFactory does not support SAX");
                    }
                    return (SAXTransformerFactory) tf;
                }
            }, TransformerConfigurationException.class);
            TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            SAXPARSER_FACTORY = runWithOurClassLoader(new Callable<SAXParserFactory>() {
                public SAXParserFactory call() {
                    return SAXParserFactory.newInstance();
                }
            }, RuntimeException.class);
            SAXPARSER_FACTORY.setNamespaceAware(true);
            SAXPARSER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        } catch (SAXNotRecognizedException e) {
            throw new ExceptionInInitializerError(e);
        } catch (SAXNotSupportedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String SP_ALLOWED_PROTOCOLS = "org.apache.taglibs.standard.xml.accessExternalEntity";
    private static final String ALLOWED_PROTOCOLS = initAllowedProtocols();

    private static String initAllowedProtocols() {
        if (System.getSecurityManager() == null) {
            return System.getProperty(SP_ALLOWED_PROTOCOLS, "all");
        } else {
            final String defaultProtocols = "";
            try {
                return AccessController.doPrivileged(new PrivilegedAction<String>() {
                    public String run() {
                        return System.getProperty(SP_ALLOWED_PROTOCOLS, defaultProtocols);
                    }
                });
            } catch (AccessControlException e) {
                // Fall back to the default i.e. none.
                return defaultProtocols;
            }
        }
    }

    static void checkProtocol(String allowedProtocols, String uri) {
        if ("all".equalsIgnoreCase(allowedProtocols)) {
            return;
        }
        String protocol = UrlUtil.getScheme(uri);
        for (String allowed : allowedProtocols.split(",")) {
            if (allowed.trim().equalsIgnoreCase(protocol)) {
                return;
            }
        }
        throw new AccessControlException("Access to external URI not allowed: " + uri);
    }

    /**
     * Create a new empty document.
     *
     * @return a new empty document
     */
    public static Document newEmptyDocument() {
        return newDocumentBuilder().newDocument();
    }

    /**
     * Create a new DocumentBuilder configured for namespaces but not validating.
     *
     * @return a new, configured DocumentBuilder
     */
    public static DocumentBuilder newDocumentBuilder() {
        try {
            return PARSER_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw (Error) new AssertionError().initCause(e);
        }
    }

    /**
     * Create a new TransformerHandler.
     * @return a new TransformerHandler
     */
    public static TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        return TRANSFORMER_FACTORY.newTransformerHandler();
    }

    /**
     * Create a new Transformer from an XSLT.
     * @param source the source of the XSLT.
     * @return a new Transformer
     * @throws TransformerConfigurationException if there was a problem creating the Transformer from the XSLT
     */
    public static Transformer newTransformer(Source source) throws TransformerConfigurationException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer(source);
        // Although newTansformer() is not allowed to return null, Xalan does.
        // Trap that here by throwing the expected TransformerConfigurationException.
        if (transformer == null) {
            throw new TransformerConfigurationException("newTransformer returned null. XSLT may be invalid.");
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
    public static InputSource newInputSource(Reader reader, String systemId) {
        InputSource source = new InputSource(reader);
        source.setSystemId(wrapSystemId(systemId));
        return source;
    }

    /**
     * Create an XMLReader that resolves entities using JSTL semantics.
     * @param entityResolver for resolving using JSTL semantics
     * @return a new XMLReader
     * @throws ParserConfigurationException if there was a configuration problem creating the reader
     * @throws SAXException if there was a problem creating the reader
     */
    public static XMLReader newXMLReader(JstlEntityResolver entityResolver)
            throws ParserConfigurationException, SAXException {
        XMLReader xmlReader = SAXPARSER_FACTORY.newSAXParser().getXMLReader();
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
     * @throws ParserConfigurationException if there was a configuration problem creating the source
     * @throws SAXException if there was a problem creating the source
     */
    public static SAXSource newSAXSource(Reader reader, String systemId, JstlEntityResolver entityResolver)
            throws ParserConfigurationException, SAXException {
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
     * JSTL-specific implementation of EntityResolver, used by parsers.
     */
    public static class JstlEntityResolver implements EntityResolver {
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
                checkProtocol(ALLOWED_PROTOCOLS, systemId);
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
     * JSTL-specific implementation of URIResolver, used by transformers.
     */
    public static class JstlUriResolver implements URIResolver {
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
            if (UrlUtil.isAbsoluteUrl(href)) {
                checkProtocol(ALLOWED_PROTOCOLS, href);
                return null;
            }
            if (base != null && UrlUtil.isAbsoluteUrl(base)) {
                checkProtocol(ALLOWED_PROTOCOLS, base);
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

    /**
     * Performs an action using this Class's ClassLoader as the Thread context ClassLoader.
     *
     * @param action the action to perform
     * @param allowed an Exception that might be thrown by the action
     * @param <T> the type of the result
     * @param <E> the type of the allowed Exception
     * @return the result of the action
     * @throws E if the action threw the allowed Exception
     */
    private static <T, E extends Exception> T runWithOurClassLoader(final Callable<T> action, Class<E> allowed) throws E {
        PrivilegedExceptionAction<T> actionWithClassloader = new PrivilegedExceptionAction<T>() {
            public T run() throws Exception {
                ClassLoader original = Thread.currentThread().getContextClassLoader();
                ClassLoader ours = XmlUtil.class.getClassLoader();
                // Don't override the TCCL if it is not needed.
                if (original == ours) {
                    return action.call();
                } else {
                    try {
                        Thread.currentThread().setContextClassLoader(ours);
                        return action.call();
                    } finally {
                        Thread.currentThread().setContextClassLoader(original);
                    }
                }
            }
        };
        try {
            return AccessController.doPrivileged(actionWithClassloader);
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (allowed.isInstance(cause)) {
                throw allowed.cast(cause);
            } else {
                throw (Error) new AssertionError().initCause(cause);
            }
        }
    }
}
