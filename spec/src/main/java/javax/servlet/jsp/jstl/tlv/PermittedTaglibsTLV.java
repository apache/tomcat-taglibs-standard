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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.TagLibraryValidator;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>A TagLibraryValidator class to allow a TLD to restrict what
 * taglibs (in addition to itself) may be imported on a page where it's
 * used.</p>
 * <p>This TLV supports the following initialization parameter:</p>
 * <ul>
 * <li><b>permittedTaglibs</b>: A whitespace-separated list of URIs corresponding
 * to tag libraries permitted to be imported on the page in addition to the tag
 * library that references PermittedTaglibsTLV (which is allowed implicitly).
 * </ul>
 * <p>This implementation only detects tag libraries declared on the {@code <jsp:root>} element,
 * including libraries in regular JSP files or JSP Documents with a specific {@code <jsp:root>}.
 * It does not detect libraries declared on other elements as supported by JSP 2.0.
 * </p>
 *
 * @author Shawn Bayern
 */
public class PermittedTaglibsTLV extends TagLibraryValidator {

    //*********************************************************************
    // Constants

    // parameter names
    private static final String PERMITTED_TAGLIBS_PARAM = "permittedTaglibs";

    // URI for "<jsp:root>" element
    private static final String JSP_ROOT_URI = "http://java.sun.com/JSP/Page";

    // local name of "<jsp:root>" element
    private static final String JSP_ROOT_NAME = "root";

    // QName for "<jsp:root>" element
    private static final String JSP_ROOT_QN = "jsp:root";

    private static final PageParser parser = new PageParser(false);

    private final Set<String> permittedTaglibs;        // what URIs are allowed?

    public PermittedTaglibsTLV() {
        permittedTaglibs = new HashSet<String>();
    }

    @Override
    public void setInitParameters(Map<String, Object> initParams) {
        super.setInitParameters(initParams);
        permittedTaglibs.clear();
        String uris = (String) initParams.get(PERMITTED_TAGLIBS_PARAM);
        if (uris != null) {
            StringTokenizer st = new StringTokenizer(uris);
            while (st.hasMoreTokens()) {
                permittedTaglibs.add(st.nextToken());
            }
        }
    }

    @Override
    public ValidationMessage[] validate(String prefix, String uri, PageData page) {
        try {
            PermittedTaglibsHandler h = new PermittedTaglibsHandler(prefix, uri);
            parser.parse(page, h);
            return h.getResult();
        } catch (SAXException ex) {
            return vmFromString(ex.toString());
        } catch (ParserConfigurationException ex) {
            return vmFromString(ex.toString());
        } catch (IOException ex) {
            return vmFromString(ex.toString());
        }
    }


    //*********************************************************************
    // Utility functions

    // constructs a ValidationMessage[] from a single String and no ID
    private ValidationMessage[] vmFromString(String message) {
        return new ValidationMessage[]{new ValidationMessage(null, message)};
    }

    /**
     * The handler that provides the base of our implementation.
     */
    private class PermittedTaglibsHandler extends DefaultHandler {
        private final String prefix;
        private final String uri;

        private boolean failed;

        public PermittedTaglibsHandler(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }

        // TODO: https://issues.apache.org/bugzilla/show_bug.cgi?id=57290 (JSP2.0 Documents)
        // If we had a way of determining if a namespace referred to a taglib as opposed to being
        // part of XML output we might be able to simplify this using startPrefixMapping events.
        @Override
        public void startElement(String ns, String ln, String qn, Attributes a) {
            // look at namespaces declared on the <jsp:root> element
            if (qn.equals(JSP_ROOT_QN) || (ns.equals(JSP_ROOT_URI) && ln.equals(JSP_ROOT_NAME))) {
                for (int i = 0; i < a.getLength(); i++) {
                    String name = a.getQName(i);

                    // ignore non-namespace attributes
                    if (!name.startsWith("xmlns:")) {
                        continue;
                    }

                    String value = a.getValue(i);
                    // ignore any declaration for our taglib or the JSP namespace
                    if (value.equals(uri) || value.equals(JSP_ROOT_URI)) {
                        continue;
                    }

                    // otherwise, ensure that 'value' is in 'permittedTaglibs' set
                    if (!permittedTaglibs.contains(value)) {
                        failed = true;
                    }
                }
            }
        }

        private ValidationMessage[] getResult() {
            if (failed) {
                return vmFromString(
                        "taglib " + prefix + " (" + uri + ") allows only the "
                                + "following taglibs to be imported: " + permittedTaglibs);
            } else {
                return null;
            }
        }
    }
}
