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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;param&gt;, the message argument
 * subtag in JSTL 1.0 which supplies an argument for parametric replacement
 * to its parent &lt;message&gt; tag.
 *
 * @see MessageSupport
 * @author Jan Luehe
 */

public abstract class ParamSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected Object value;                          // 'value' attribute


    //*********************************************************************
    // Constructor and initialization

    public ParamSupport() {
	super();
	init();
    }

    private void init() {
	value = null;
    }


    //*********************************************************************
    // Tag logic

    // Supply our value to our parent <fmt:message> tag
    public int doEndTag() throws JspException {
	Tag t = findAncestorWithClass(this, MessageSupport.class);
	if (t == null) {
	    throw new JspTagException(Resources.getMessage(
                            "PARAM_OUTSIDE_MESSAGE"));
	}
	MessageSupport parent = (MessageSupport) t;

	/*
	 * Get argument from 'value' attribute or body, as appropriate, and
	 * add it to enclosing <fmt:message> tag, even if it is null or equal
	 * to "".
	 */
	if (value == null) {
	    // TLV has already ensured that we have non-empty body in this case
	    value = getBodyContent().getString().trim();
	}
	parent.addParam(value);

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }
}
