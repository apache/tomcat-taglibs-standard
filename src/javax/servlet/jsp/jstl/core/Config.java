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

package javax.servlet.jsp.jstl.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;

public class Config {

    /**
     * I18N/Formatting configuration
     */
    public static final String FMT_LOCALE
	= "javax.servlet.jsp.jstl.fmt.locale";
    public static final String FMT_FALLBACKLOCALE
	= "javax.servlet.jsp.jstl.fmt.fallbackLocale";
    public static final String FMT_TIMEZONE
	= "javax.servlet.jsp.jstl.fmt.timeZone";
    public static final String FMT_BUNDLE
	= "javax.servlet.jsp.jstl.fmt.bundle";

    /**
     * SQL configuration
     */
    public static final String SQL_DATASOURCE
	= "javax.servlet.jsp.jstl.sql.dataSource";
    public static final String SQL_DRIVER
	= "javax.servlet.jsp.jstl.sql.driver";
    public static final String SQL_URL
	= "javax.servlet.jsp.jstl.sql.url";
    public static final String SQL_USER
	= "javax.servlet.jsp.jstl.sql.user";
    public static final String SQL_PASSWORD
	= "javax.servlet.jsp.jstl.sql.password";
    public static final String SQL_MAXROWS
	= "javax.servlet.jsp.jstl.sql.maxRows";
	
    /*
     * Private constants
     */
    private static final String PAGE_SCOPE_SUFFIX = ".page";
    private static final String REQUEST_SCOPE_SUFFIX = ".request";
    private static final String SESSION_SCOPE_SUFFIX = ".session";
    private static final String APPLICATION_SCOPE_SUFFIX = ".application";

    /**
     * XXX
     * @param pc
     * @param name
     * @param scope
     *
     * @return
     */
    public static Object get(PageContext pc, String name, int scope) {
	switch (scope) {
	case PageContext.PAGE_SCOPE:
	    return pc.getAttribute(name + PAGE_SCOPE_SUFFIX, scope);
	case PageContext.REQUEST_SCOPE:
	    return pc.getAttribute(name + REQUEST_SCOPE_SUFFIX, scope);
	case PageContext.SESSION_SCOPE:
	    return pc.getAttribute(name + SESSION_SCOPE_SUFFIX, scope);
	case PageContext.APPLICATION_SCOPE:
	    return pc.getAttribute(name + APPLICATION_SCOPE_SUFFIX, scope);
	default:
	    throw new IllegalArgumentException("unknown scope");
	}
    }

    /**
     * XXX
     * @param request
     * @param name
     *
     * @return
     */
    public static Object get(ServletRequest request, String name) {
	return request.getAttribute(name + REQUEST_SCOPE_SUFFIX);
    }

    /**
     * XXX
     * @param session
     * @param name
     *
     * @return
     */
    public static Object get(HttpSession session, String name) {
	return session.getAttribute(name + SESSION_SCOPE_SUFFIX);
    }

    /**
     * XXX
     * @param context
     * @param name
     *
     * @return
     */
    public static Object get(ServletContext context, String name) {
	return context.getAttribute(name + APPLICATION_SCOPE_SUFFIX);
    }

    /**
     * XXX
     * @param pc
     * @param name
     * @param value
     * @param scope
     */
    public static void set(PageContext pc, String name, Object value,
			   int scope) {
	switch (scope) {
	case PageContext.PAGE_SCOPE:
	    pc.setAttribute(name + PAGE_SCOPE_SUFFIX, value, scope);
	    break;
	case PageContext.REQUEST_SCOPE:
	    pc.setAttribute(name + REQUEST_SCOPE_SUFFIX, value, scope);
	    break;
	case PageContext.SESSION_SCOPE:
	    pc.setAttribute(name + SESSION_SCOPE_SUFFIX, value, scope);
	    break;
	case PageContext.APPLICATION_SCOPE:
	    pc.setAttribute(name + APPLICATION_SCOPE_SUFFIX, value, scope);
	    break;
	default:
	    throw new IllegalArgumentException("unknown scope");
	}
    }

    /**
     * XXX
     * @param request
     * @param name
     * @paran value
     */
    public static void set(ServletRequest request, String name, Object value) {
	request.setAttribute(name + REQUEST_SCOPE_SUFFIX, value);
    }

    /**
     * XXX
     * @param session
     * @param name
     * @param value
     */
    public static void set(HttpSession session, String name, Object value) {
	session.setAttribute(name + SESSION_SCOPE_SUFFIX, value);
    }

    /**
     * XXX
     * @param context
     * @param name
     * @param value
     */
    public static void set(ServletContext context, String name, Object value) {
	context.setAttribute(name + APPLICATION_SCOPE_SUFFIX, value);
    }
 
    /**
     * XXX
     * @param pc
     * @param name
     * @param scope
     */
    public static void remove(PageContext pc, String name, int scope) {
	switch (scope) {
	case PageContext.PAGE_SCOPE:
	    pc.removeAttribute(name + PAGE_SCOPE_SUFFIX, scope);
	    break;
	case PageContext.REQUEST_SCOPE:
	    pc.removeAttribute(name + REQUEST_SCOPE_SUFFIX, scope);
	    break;
	case PageContext.SESSION_SCOPE:
	    pc.removeAttribute(name + SESSION_SCOPE_SUFFIX, scope);
	    break;
	case PageContext.APPLICATION_SCOPE:
	    pc.removeAttribute(name + APPLICATION_SCOPE_SUFFIX, scope);
	    break;
	default:
	    throw new IllegalArgumentException("unknown scope");
	}
    }

    /**
     * XXX
     * @param request
     * @param name
     */
    public static void remove(ServletRequest request, String name) {
	request.removeAttribute(name + REQUEST_SCOPE_SUFFIX);
    }

    /**
     * XXX
     * @param session
     * @param name
     */
    public static void remove(HttpSession session, String name) {
	session.removeAttribute(name + SESSION_SCOPE_SUFFIX);
    }

    /**
     * XXX
     * @param context
     * @param name
     */
    public static void remove(ServletContext context, String name) {
	context.removeAttribute(name + APPLICATION_SCOPE_SUFFIX);
    }
 
    /**
     * XXX
     * @param pc
     * @param name
     * 
     * @return
     */
    public static Object find(PageContext pc, String name) {
	Object ret = get(pc, name, PageContext.PAGE_SCOPE);
	if (ret == null) {
	    ret = get(pc, name, PageContext.REQUEST_SCOPE);
	    if (ret == null) {
		ret = get(pc, name, PageContext.SESSION_SCOPE);
		if (ret == null) {
		    ret = get(pc, name, PageContext.APPLICATION_SCOPE);
		    if (ret == null) {
			ret = pc.getServletContext().getInitParameter(name);
		    }
		}
	    }
	}

	return ret;
    }
}
