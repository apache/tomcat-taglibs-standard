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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XObjectFactory;

/**
 */
public class JSTLVariableStack extends VariableStack {

    private static enum Scope {
        PARAM,
        HEADER,
        COOKIE,
        INITPARAM,
        PAGE,
        REQUEST,
        SESSION,
        APPLICATION
    }

    // Prefixes for JSTL implicit variables
    private static final String PARAM_PREFIX = "param";
    private static final String HEADER_PREFIX = "header";
    private static final String COOKIE_PREFIX = "cookie";
    private static final String INITPARAM_PREFIX = "initParam";
    private static final String PAGE_PREFIX = "pageScope";
    private static final String REQUEST_PREFIX = "requestScope";
    private static final String SESSION_PREFIX = "sessionScope";
    private static final String APP_PREFIX = "applicationScope";

    // map prefixes to scopes
    private static final Map<String, Scope> SCOPES;
    static {
        SCOPES = new HashMap<String, Scope>(8);
        SCOPES.put(PARAM_PREFIX, Scope.PARAM);
        SCOPES.put(HEADER_PREFIX, Scope.HEADER);
        SCOPES.put(COOKIE_PREFIX, Scope.COOKIE);
        SCOPES.put(INITPARAM_PREFIX, Scope.INITPARAM);
        SCOPES.put(PAGE_PREFIX, Scope.PAGE);
        SCOPES.put(REQUEST_PREFIX, Scope.REQUEST);
        SCOPES.put(SESSION_PREFIX, Scope.SESSION);
        SCOPES.put(APP_PREFIX, Scope.APPLICATION);
    }

    private final PageContext pageContext;

    public JSTLVariableStack(PageContext pageContext) {
        super(2);
        this.pageContext = pageContext;
    }

    @Override
    public XObject getVariableOrParam(XPathContext xctxt, QName qname) throws TransformerException {
        String prefix = qname.getNamespaceURI();
        String name = qname.getLocalPart();
        Object value = getValue(prefix, name);
        if (value == null) {
            StringBuilder var = new StringBuilder();
            var.append('$');
            if (prefix != null) {
                var.append(prefix);
                var.append(':');
            }
            var.append(name);
            throw new TransformerException(Resources.getMessage("XPATH_UNABLE_TO_RESOLVE_VARIABLE", var.toString()));
        }
        return XObjectFactory.create(value, xctxt);
    }

    private Object getValue(String prefix, String name) {
        if (prefix == null) {
            return pageContext.findAttribute(name);
        }
        Scope scope = SCOPES.get(prefix);
        switch (scope) {
            case PARAM:
                return pageContext.getRequest().getParameter(name);
            case HEADER:
                return ((HttpServletRequest) pageContext.getRequest()).getHeader(name);
            case COOKIE:
                Cookie[] cookies = ((HttpServletRequest) pageContext.getRequest()).getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals(name)) {
                            return cookie.getValue();
                        }
                    }
                }
                return null;
            case INITPARAM:
                return pageContext.getServletContext().getInitParameter(name);
            case PAGE:
                return pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
            case REQUEST:
                return pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
            case SESSION:
                return pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
            case APPLICATION:
                return pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
            default:
                throw new AssertionError();
        }
    }
}
