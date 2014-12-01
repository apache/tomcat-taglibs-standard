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
package javax.servlet.jsp.jstl.tlv;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.jsp.tagext.PageData;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Support class for working with the SAX Parser.
 */
class PageParser {

    private final SAXParserFactory parserFactory;

    PageParser(boolean namespaceAware) {
        parserFactory = AccessController.doPrivileged(new PrivilegedAction<SAXParserFactory>() {
            public SAXParserFactory run() {
                ClassLoader original = Thread.currentThread().getContextClassLoader();
                ClassLoader ours = PageParser.class.getClassLoader();
                try {
                    if (original != ours) {
                        Thread.currentThread().setContextClassLoader(ours);
                    }
                    return SAXParserFactory.newInstance();
                } finally {
                    if (original != ours) {
                        Thread.currentThread().setContextClassLoader(original);
                    }
                }
            }
        });
        try {
            parserFactory.setNamespaceAware(namespaceAware);
            parserFactory.setValidating(false);
            parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        } catch (SAXNotRecognizedException e) {
            throw new ExceptionInInitializerError(e);
        } catch (SAXNotSupportedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    void parse(PageData pageData, DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = parserFactory.newSAXParser();
        InputStream is = pageData.getInputStream();
        try {
            parser.parse(is, handler);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Suppress.
            }
        }
    }
}
