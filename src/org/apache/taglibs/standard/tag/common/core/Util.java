/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

import java.text.DateFormat;
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

	if (REQUEST.equalsIgnoreCase(scope))
	    ret = PageContext.REQUEST_SCOPE;
	else if (SESSION.equalsIgnoreCase(scope))
	    ret = PageContext.SESSION_SCOPE;
	else if (APPLICATION.equalsIgnoreCase(scope))
	    ret = PageContext.APPLICATION_SCOPE;

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
     * Searches the named attribute in the given page after appending the
     * various scope names to its name.
     *
     * First, the attribute is searched in page scope, with the suffix
     * ".page" appended to its name.
     * If not found, the attribute is searched in request scope, with the 
     * suffix ".request" appended to its name.
     * If not found, the attribute is searched in session scope, with the
     * suffix ".session" appended to its name.
     * If not found. the attribute is searched in application scope, with the
     * suffix ".application" appended to its name.
     *
     * @param pageContext The page in which to search the attribute
     * @param name The attribute name
     *
     * @return The value associated with the named attribute, or null.
     */
    public static Object findAttribute(PageContext pageContext, String name) {
	Object ret = pageContext.getAttribute(name + ".page",
					      PageContext.PAGE_SCOPE);
	if (ret == null) {
	    ret = pageContext.getAttribute(name + ".request",
					   PageContext.REQUEST_SCOPE);
	    if (ret == null) {
		ret = pageContext.getAttribute(name + ".session",
					       PageContext.SESSION_SCOPE);
		if (ret == null) {
		    ret = pageContext.getAttribute(name + ".application",
						   PageContext.APPLICATION_SCOPE);
		}
	    }
	}

	return ret;
    }
}
