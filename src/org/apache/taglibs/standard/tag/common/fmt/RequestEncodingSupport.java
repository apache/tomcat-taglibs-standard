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

package org.apache.taglibs.standard.tag.common.fmt;

import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Support for tag handlers for &lt;requestEncoding&gt;, the tag for setting
 * the request character encoding in JSTL 1.0.
 *
 * @author Jan Luehe
 * @author Pierre Delisle
 */

public abstract class RequestEncodingSupport extends TagSupport {

    //*********************************************************************
    // Package-scoped constants

    static final String REQUEST_CHAR_SET =
	"javax.servlet.jsp.jstl.fmt.request.charset";


    //*********************************************************************
    // Private constants

    private static final String DEFAULT_ENCODING = "ISO-8859-1";


    //*********************************************************************
    // Tag attributes

    protected String value;             // 'value' attribute
    

    //*********************************************************************
    // Derived information
    
    protected String charEncoding;   // derived from 'value' attribute  
    

    //*********************************************************************
    // Constructor and initialization

    public RequestEncodingSupport() {
	super();
	init();
    }

    private void init() {
	value = null;
    }


    //*********************************************************************
    // Tag logic

    public int doEndTag() throws JspException {
        charEncoding = value;
	if ((charEncoding == null)
	        && (pageContext.getRequest().getCharacterEncoding() == null)) { 
            // Use charset from session-scoped attribute
	    charEncoding = (String)
		pageContext.getAttribute(REQUEST_CHAR_SET,
					 PageContext.SESSION_SCOPE);
	    if (charEncoding == null) {
		// Use default encoding
		charEncoding = DEFAULT_ENCODING;
	    }
	}

	/*
	 * If char encoding was already set in the request, we don't need to 
	 * set it again.
	 */
	if (charEncoding != null) {
	    try {
		pageContext.getRequest().setCharacterEncoding(charEncoding);
	    } catch (UnsupportedEncodingException uee) {
		throw new JspTagException(uee.toString(), uee);
	    }
	}

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }
}
