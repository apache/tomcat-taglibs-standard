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

package org.apache.taglibs.standard.lang.javascript.adapter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class represents a JSP PageContext as a scope for the execution of 
 * EcmaScript expressions. It makes all context attributes, regardless of 
 * scope, available and in addition publishes a number of special objects.
 * 
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * @version $Revision$
 */
public class ScriptablePageContext
    extends ScriptableObject {
    
    // ---------------------------------------------------- Instance Variables
    
    /**
     * The actual PageContext we wrap around.
     */
    private PageContext pageContext = null;
    
    /**
     * Map of request cookies, populated by getCookieMap() on first invocation
     */
    private Map cookiesMap = null;
    
    /**
     * Map of request headers, populated by getHeaderMap() on first invocation
     */
    private Map headersMap = null;
    
    /**
     * Map of init parameters, populated by getInitParameterMap() on first 
     * invocation
     */
    private Map initParamsMap = null;
    
    /**
     * Map of request parameters, populated by getParameterMap() on first 
     * invocation
     */
    private Map paramsMap = null;
    
    // ---------------------------------------------------------- Constructors
    
    /**
     * Constructor.
     * 
     * @param pageContext   the JSP PageContext to wrap
     */
    public ScriptablePageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    // --------------------------------------- ScriptableObject Implementation
    
    /**
     * Implemented to provide a JavaScript class name.
     * 
     * @see org.mozilla.javascript.Scriptable#getClassName()
     */
    public String getClassName() {
        return "JspPageContext";
    }
    
    /**
     * Checks the requested name for special objects, which are:
     * <ul>
     *  <li>params: request parameters</li>
     *  <li>headers: request headers</li>
     *  <li>cookies: request cookies</li>
     *  <li>initParams: context initialization parameters</li>
     * </ul>
     * For each of these objects, a Map with the corresponding entries is 
     * returned.
     * 
     * <b>Note:</b> This should probably be replaced by a proper object model 
     * sometime in the future.
     * 
     * @see org.mozilla.javascript.Scriptable#get(String,Scriptable)
     */
    public Object get(String name, Scriptable start) {
        Object result = null;
        
        if (name.equals("params")) {
            result = getOrCreateParameterMap();
        } else if (name.equals("headers")) {
            result = getOrCreateHeaderMap();
        } else if (name.equals("cookies")) {
            result = getOrCreateCookieMap();
        } else if (name.equals("initParams")) {
            result = getOrCreateInitParameterMap();
        } else {
            result = pageContext.findAttribute(name);
        }
        if (result == null) {
            result = super.get(name, start);
        }
        
        return result;
    }
    
    // ------------------------------------------------------- Private Methods
    
    /**
     * Returns a Map containing the request cookies, creating it on first 
     * invocation.
     * 
     * @return a Map containing the request cookies
     */
    private Map getOrCreateCookieMap() {
        
        if (cookiesMap == null) {
            HttpServletRequest request = 
                (HttpServletRequest)pageContext.getRequest();
            cookiesMap = new HashMap();
            Cookie[] array = request.getCookies();
            if (array != null) {
                for (int i = 0; i < array.length; i++) {
                    String name = array[i].getName();
                    cookiesMap.put(name, array[i]);
                }
            }
        }
        
        return cookiesMap;
    }
    
    /**
     * Returns a Map containing the request headers, creating it on first 
     * invocation.
     * 
     * @return a Map containing the request headers
     */
    private Map getOrCreateHeaderMap() {
        
        if (headersMap == null) {
            HttpServletRequest request = 
                (HttpServletRequest)pageContext.getRequest();
            headersMap = new HashMap();
            Enumeration enum = request.getHeaderNames();
            if (enum != null) {
                while (enum.hasMoreElements()) {
                    String name = (String)enum.nextElement();
                    headersMap.put(name, request.getHeader(name));
                }
            }
        }
        
        return headersMap;
    }
    
    /**
     * Returns a Map containing the context initialization parameters, 
     * creating it on first invocation.
     * 
     * @return a Map containing the context initialization parameters
     */
    private Map getOrCreateInitParameterMap() {
        
        if (initParamsMap == null) {
            ServletContext context = pageContext.getServletContext();
            initParamsMap = new HashMap();
            Enumeration enum = context.getInitParameterNames();
            while (enum.hasMoreElements()) {
                String name = (String)enum.nextElement();
                initParamsMap.put(name, context.getInitParameter(name));
            }
        }
        
        return initParamsMap;
    }
    
    /**
     * Returns a Map containing the request parameters, creating it on first 
     * invocation.
     * 
     * @return a Map containing the request parameters
     */
    private Map getOrCreateParameterMap() {
        
        if (paramsMap == null) {
            paramsMap = pageContext.getRequest().getParameterMap();
        }
        
        return paramsMap;
    }
    
}
