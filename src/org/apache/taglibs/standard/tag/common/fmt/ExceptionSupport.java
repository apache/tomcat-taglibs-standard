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

package org.apache.taglibs.standard.tag.common.fmt;

import java.util.*;
import java.text.*;
import java.io.*;
import javax.servlet.ServletContext;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.fmt.LocalizableException;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;exception&gt;, the exception formatting tag
 * in JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class ExceptionSupport extends TagSupport {

    //*********************************************************************
    // Private constants
    private static final String SERVLET_EXCEPTION =
	"javax.servlet.error.exception";

    
    //*********************************************************************
    // Protected state

    protected Exception value;                       // 'value' attribute
    protected ResourceBundle bundle;                 // 'bundle' attribute


    //*********************************************************************
    // Constructor and initialization

    public ExceptionSupport() {
	super();
	init();
    }

    private void init() {
	value = null;
	bundle = null;
    }


    //*********************************************************************
    // Tag logic

    public int doStartTag() throws JspException {
	Object[] messageArgs = null;

	if (value == null) {
	    /*
	     * Check javax.servlet.jsp.jspException request attribute
	     * defined in JSP 1.2
	     */
	    value = pageContext.getException();
	    if (value == null) {
		/*
		 * Check javax.servlet.error.exception request attribute
		 * defined in Servlet 2.3
		 */
		value = (Exception)
		    pageContext.getAttribute(SERVLET_EXCEPTION,
					     PageContext.REQUEST_SCOPE);
		if (value == null) {
		    throw new JspTagException(
		        Resources.getMessage("EXCEPTION_NOT_IN_ERROR_PAGE"));
		}
	    }
	}

	if (bundle == null) {
	    Tag t = findAncestorWithClass(this, BundleSupport.class);
	    if (t != null) {
		// use resource bundle from parent <bundle> tag
		BundleSupport parent = (BundleSupport) t;
		bundle = parent.getBundle();
	    } else {
		bundle = BundleSupport.getDefaultBundle(
                    pageContext, BundleSupport.DEFAULT_EXCEPTION_BASENAME);
	    }
	}

	String key = value.getClass().getName();
	if (value instanceof LocalizableException) {
	    LocalizableException le = (LocalizableException) value;
	    if (le.getMessageKey() != null)
		key = le.getMessageKey();
	    messageArgs = le.getMessageArgs();
	}

	String message = value.getLocalizedMessage();
	if (bundle != null) {
	    try {
		message = bundle.getString(key);
		if (messageArgs != null) {
		    MessageFormat formatter = new MessageFormat("");
		    formatter.setLocale(bundle.getLocale());
		    formatter.applyPattern(message);
		    message = formatter.format(messageArgs);
		}
	    } catch (MissingResourceException mre) {
	    }
	}

	try {
	    pageContext.getOut().print(message);
	} catch (IOException ioe) {
	    throw new JspTagException(ioe.getMessage());
	}

	return SKIP_BODY;
    }
	
    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }
}
