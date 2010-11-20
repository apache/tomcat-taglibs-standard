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

package org.apache.taglibs.standard.tag.common.core;

import java.text.DateFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Utilities in support of tag-handler classes.</p>
 *
 * @author Jan Luehe
 */
public class Util {

    private static final String REQUEST = "request";
    private static final String SESSION = "session";
    private static final String APPLICATION = "application";
    private static final String DEFAULT = "default";
    private static final String SHORT = "short";
    private static final String MEDIUM = "medium";
    private static final String LONG = "long";
    private static final String FULL = "full";

    /*
     * Converts the given string description of a scope to the corresponding
     * PageContext constant.
     *
     * The validity of the given scope has already been checked by the
     * appropriate TLV.
     *
     * @param scope String description of scope
     *
     * @return PageContext constant corresponding to given scope description
     */

    public static int getScope(String scope) {
        int ret = PageContext.PAGE_SCOPE; // default

        if (REQUEST.equalsIgnoreCase(scope)) {
            ret = PageContext.REQUEST_SCOPE;
        } else if (SESSION.equalsIgnoreCase(scope)) {
            ret = PageContext.SESSION_SCOPE;
        } else if (APPLICATION.equalsIgnoreCase(scope)) {
            ret = PageContext.APPLICATION_SCOPE;
        }

        return ret;
    }

    /*
     * Converts the given string description of a formatting style for
     * dates and times to the corresponding java.util.DateFormat constant.
     *
     * @param style String description of formatting style for dates and times
     * @param errCode Error code to throw if given style is invalid
     *
     * @return java.util.DateFormat constant corresponding to given style
     *
     * @throws JspException if the given style is invalid
     */

    public static int getStyle(String style, String errCode)
            throws JspException {
        int ret = DateFormat.DEFAULT;

        if (style != null) {
            if (DEFAULT.equalsIgnoreCase(style)) {
                ret = DateFormat.DEFAULT;
            } else if (SHORT.equalsIgnoreCase(style)) {
                ret = DateFormat.SHORT;
            } else if (MEDIUM.equalsIgnoreCase(style)) {
                ret = DateFormat.MEDIUM;
            } else if (LONG.equalsIgnoreCase(style)) {
                ret = DateFormat.LONG;
            } else if (FULL.equalsIgnoreCase(style)) {
                ret = DateFormat.FULL;
            } else {
                throw new JspException(Resources.getMessage(errCode, style));
            }
        }

        return ret;
    }


    /**
     * Get the value associated with a content-type attribute.
     * Syntax defined in RFC 2045, section 5.1.
     */
    public static String getContentTypeAttribute(String input, String name) {
        int begin;
        int end;
        int index = input.toUpperCase().indexOf(name.toUpperCase());
        if (index == -1) {
            return null;
        }
        index = index + name.length(); // positioned after the attribute name
        index = input.indexOf('=', index); // positioned at the '='
        if (index == -1) {
            return null;
        }
        index += 1; // positioned after the '='
        input = input.substring(index).trim();

        if (input.charAt(0) == '"') {
            // attribute value is a quoted string
            begin = 1;
            end = input.indexOf('"', begin);
            if (end == -1) {
                return null;
            }
        } else {
            begin = 0;
            end = input.indexOf(';');
            if (end == -1) {
                end = input.indexOf(' ');
            }
            if (end == -1) {
                end = input.length();
            }
        }
        return input.substring(begin, end).trim();
    }

    /**
     * HttpServletRequest.getLocales() returns the server's default locale
     * if the request did not specify a preferred language.
     * We do not want this behavior, because it prevents us from using
     * the fallback locale.
     * We therefore need to return an empty Enumeration if no preferred
     * locale has been specified. This way, the logic for the fallback
     * locale will be able to kick in.
     */
    public static Enumeration getRequestLocales(HttpServletRequest request) {
        Enumeration values = request.getHeaders("accept-language");
        if (values == null) {
            // No header for "accept-language". Simply return
            // a new empty enumeration.
            // System.out.println("Null accept-language");
            return new Vector().elements();
        } else if (values.hasMoreElements()) {
            // At least one "accept-language". Simply return
            // the enumeration returned by request.getLocales().
            // System.out.println("At least one accept-language");
            return request.getLocales();
        } else {
            // No header for "accept-language". Simply return
            // the empty enumeration.
            // System.out.println("No accept-language");
            return values;
        }
    }
}
