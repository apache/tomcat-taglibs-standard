/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.taglibs.standard.tag.common.core;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.net.URLEncoder;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;url&gt;, the URL creation
 * and rewriting tag in JSTL 1.0.</p>
 *
 * @author Shawn Bayern
 */

public abstract class UrlSupport extends BodyTagSupport
    implements ParamParent {

    //*********************************************************************
    // Protected state

    protected String value;                      // 'value' attribute
    protected String context;			 // 'context' attribute

    //*********************************************************************
    // Private state

    private String var;                          // 'var' attribute
    private int scope;				 // processed 'scope' attr
    private ParamSupport.ParamManager params;	 // added parameters

    //*********************************************************************
    // Constructor and initialization

    public UrlSupport() {
	super();
	init();
    }

    private void init() {
	value = var = null;
	params = null;
	context = null;
	scope = PageContext.PAGE_SCOPE;
    }


   //*********************************************************************
    // Tag attributes known at translation time

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Collaboration with subtags

    // inherit Javadoc
    public void addParameter(String name, String value) {
	params.addParameter(name, value);
    }


    //*********************************************************************
    // Tag logic

    // resets any parameters that might be sent
    public int doStartTag() throws JspException {
	params = new ParamSupport.ParamManager();
	return EVAL_BODY_BUFFERED;
    }


    // gets the right value, encodes it, and prints or stores it
    public int doEndTag() throws JspException {
	String result;				// the eventual result

	// add (already encoded) parameters
	String baseUrl = resolveUrl(value, context, pageContext);
	result = params.aggregateParams(baseUrl);

	// if the URL is relative, rewrite it
	if (!ImportSupport.isAbsoluteUrl(result)) {
	    HttpServletResponse response =
                ((HttpServletResponse) pageContext.getResponse());
            result = response.encodeURL(result);
	}

	// store or print the output
	if (var != null)
	    pageContext.setAttribute(var, result, scope);
	else {
	    try {
	        pageContext.getOut().print(result);
	    } catch (java.io.IOException ex) {
		throw new JspTagException(ex.getMessage());
	    }
	}

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }

    //*********************************************************************
    // Utility methods

    public static String resolveUrl(
            String url, String context, PageContext pageContext)
	    throws JspException {
	// don't touch absolute URLs
	if (ImportSupport.isAbsoluteUrl(url))
	    return url;

	// normalize relative URLs against a context root
	HttpServletRequest request =
	    (HttpServletRequest) pageContext.getRequest();
	if (context == null) {
	    if (url.startsWith("/"))
		return (request.getContextPath() + url);
	    else
		return url;
	} else {
            if (!context.startsWith("/") || !url.startsWith("/"))
                throw new JspTagException(
                    Resources.getMessage("IMPORT_BAD_RELATIVE"));
            return (context + url);
        }
    }

}
