/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.FunctionContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.jaxen.XPathFunctionContext;
import org.jaxen.dom.DOMXPath;
import org.jaxen.dom.DocumentNavigator;
import org.saxpath.SAXPathException;
import org.w3c.dom.Node;

/**
 * <p>Support for tag handlers that evaluate XPath expressions.</p>
 *
 * @author Shawn Bayern
 */
// would ideally be a base class, but some of our user handlers already
// have their own parents
public class XPathUtil {

    //*********************************************************************
    // Constructor

    /**
     * Constructs a new XPathUtil object associated with the given
     * PageContext.
     */
    public XPathUtil(PageContext pc) {
	pageContext = pc;
    }

    //*********************************************************************
    // Support for JSTL variable resolution

    private static final String PAGE_NS_URL
	= "http://java.sun.com/jstl/xpath/page";
    private static final String REQUEST_NS_URL
	= "http://java.sun.com/jstl/xpath/request";
    private static final String SESSION_NS_URL
	= "http://java.sun.com/jstl/xpath/session";
    private static final String APP_NS_URL
	= "http://java.sun.com/jstl/xpath/app";
    private static final String PARAM_NS_URL
	= "http://java.sun.com/jstl/xpath/param";
    private static final String INITPARAM_NS_URL
	= "http://java.sun.com/jstl/xpath/initParam";
    private static final String COOKIE_NS_URL
	= "http://java.sun.com/jstl/xpath/cookie";
    private static final String HEADER_NS_URL
	= "http://java.sun.com/jstl/xpath/header";

    private static final String PAGE_P = "pageScope";
    private static final String REQUEST_P = "requestScope";
    private static final String SESSION_P = "sessionScope";
    private static final String APP_P = "applicationScope";
    private static final String PARAM_P = "param";
    private static final String INITPARAM_P = "initParam";
    private static final String COOKIE_P = "cookie";
    private static final String HEADER_P = "header";

    protected class JstlVariableContext implements VariableContext {
	// retrieves object using JSTL's custom variable-mapping rules
        public Object getVariableValue(String namespace, String prefix,
                String localName) throws UnresolvableException {
	    // I'd prefer to match on namespace, but this doesn't appear
            // to work in Jaxen
	    if (prefix == null || prefix.equals("")) {
		return notNull(
		    pageContext.findAttribute(localName),
		    prefix,
		    localName);
	    } else if (prefix.equals(PAGE_P)) {
		return notNull(
		    pageContext.getAttribute(localName,PageContext.PAGE_SCOPE),
		    prefix,
		    localName);
	    } else if (prefix.equals(REQUEST_P)) {
		return notNull(
		    pageContext.getAttribute(localName,
			PageContext.REQUEST_SCOPE),
		    prefix,
		    localName);
	    } else if (prefix.equals(SESSION_P)) {
		return notNull(
		    pageContext.getAttribute(localName,
		        PageContext.SESSION_SCOPE),
		    prefix,
		    localName);
	    } else if (prefix.equals(APP_P)) {
		return notNull(
		    pageContext.getAttribute(localName,
		        PageContext.APPLICATION_SCOPE),
		    prefix,
		    localName);
	    } else if (prefix.equals(PARAM_P)) {
		return notNull(
		    pageContext.getRequest().getParameter(localName),
		    prefix,
		    localName);
	    } else if (prefix.equals(INITPARAM_P)) {
		return notNull(
		    pageContext.getServletContext().
		      getInitParameter(localName),
		    prefix,
		    localName);
	    } else if (prefix.equals(HEADER_P)) {
		HttpServletRequest hsr =
		    (HttpServletRequest) pageContext.getRequest();
		return notNull(
		    hsr.getHeader(localName),
		    prefix,
		    localName);
	    } else if (prefix.equals(COOKIE_P)) {
		HttpServletRequest hsr =
		    (HttpServletRequest) pageContext.getRequest();
		Cookie[] c = hsr.getCookies();
		for (int i = 0; i < c.length; i++)
		    if (c[i].getName().equals(localName))
			return c[i].getValue();
		throw new UnresolvableException("$" + prefix + ":" + localName);
	    } else
		throw new UnresolvableException("$" + prefix + ":" + localName);
        }

	private Object notNull(Object o, String prefix, String localName)
	        throws UnresolvableException {
	    if (o == null)
		throw new UnresolvableException("$" + prefix + ":" + localName);
	    return o;
	}
    }

    //*********************************************************************
    // Support for XPath evaluation

    private PageContext pageContext;
    private static SimpleNamespaceContext nc;
    private static FunctionContext fc;
    private static DocumentNavigator dn;
    private static HashMap exprCache;

    /** Initialize globally useful data. */
    private synchronized static void staticInit() {
	if (nc == null) {
	    // register supported namespaces
            nc = new SimpleNamespaceContext();
            SimpleNamespaceContext nc = new SimpleNamespaceContext();
            nc.addNamespace("page", PAGE_NS_URL);
            nc.addNamespace("request", REQUEST_NS_URL);
            nc.addNamespace("session", SESSION_NS_URL);
            nc.addNamespace("application", APP_NS_URL);
            nc.addNamespace("param", PARAM_NS_URL);
            nc.addNamespace("initParam", INITPARAM_NS_URL);
            nc.addNamespace("header", HEADER_NS_URL);
            nc.addNamespace("cookie", COOKIE_NS_URL);

	    // set up the global FunctionContext
	    fc = XPathFunctionContext.getInstance();

	    // set up the global DocumentNavigator
	    dn = DocumentNavigator.getInstance();

            // create a HashMap to cache the expressions
            exprCache = new HashMap();
	}
    }

    /**
     * Returns a String given an XPath expression and a single context
     * Node.
     */
    public String valueOf(Node n, String xpath) throws SAXPathException {
	staticInit();
        XPath xp = new DOMXPath(xpath);
        // return xp.valueOf(getLocalContext(n));
        return xp.stringValueOf(getLocalContext(n));
    }

    /** Evaluates an XPath expression to a boolean value. */
    public boolean booleanValueOf(Node n, String xpath)
	    throws SAXPathException {
	staticInit();
	XPath xp = parse(xpath);
	return xp.booleanValueOf(getLocalContext(n));
    }

    /** Evalutes an XPath expression to a List of nodes. */
    public List selectNodes(Node n, String xpath) throws SAXPathException {
	staticInit();
	XPath xp = parse(xpath);
	return xp.selectNodes(getLocalContext(n));
    }

    /** Evaluates an XPath expression to a single node. */
    public Node selectSingleNode(Node n, String xpath)
	    throws SAXPathException {
	staticInit();
	XPath xp = parse(xpath);
	return (Node) xp.selectSingleNode(getLocalContext(n));
    }

    /** Returns a locally appropriate Jaxen context given a node. */
    private Context getLocalContext(Node n) {
	// set up instance-specific contexts
        VariableContext vc = new JstlVariableContext();
        ContextSupport cs = new ContextSupport(nc, fc, vc, dn);
        Context c = new Context(cs);
        List l = new ArrayList(1);
        l.add(n);
        c.setNodeSet(l);
	return c;
    }

    /**
     * Retrieves a parsed version of the textual XPath expression.
     * The parsed version is retrieved from our static cache if we've
     * seen it previously.
     */
    private XPath parse(String xpath) throws SAXPathException {
        XPath cached = (XPath) exprCache.get(xpath);
        if (cached == null) {
          cached = new DOMXPath(xpath);
          exprCache.put(xpath, cached);
	}
        return cached;
    }

    //*********************************************************************
    // Static support for context retrieval from parent <forEach> tag

    public static Node getContext(Tag t) throws JspTagException {
	ForEachTag xt =
	    (ForEachTag) TagSupport.findAncestorWithClass(
		t, ForEachTag.class);
	if (xt == null)
	    return null;
	else
	    return (xt.getContext());
    }

}
