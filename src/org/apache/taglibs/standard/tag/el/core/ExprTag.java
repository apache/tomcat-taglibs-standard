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

package org.apache.taglibs.standard.tag.el.core;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.jstl.core.ExpressionException;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.tag.common.core.*;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>A handler for the &lt;expr&gt; tag, which simply evalutes and
 * prints the result of the expression it's passed.  If the expression
 * fails to complete evaluation successfully because of a
 * javax.servlet.jsp.jstl.core.ExpressionException, or if the result is
 * null, we print the value of the 'default' attribute's expression or
 * our body (which two are mutually exclusive, although this constraint
 * is enforced outside this handler, in our TagLibraryValidator).</p>
 *
 * @author Shawn Bayern
 */
public class ExprTag extends BodyTagSupport {

    /*
     * (One almost wishes XML and JSP could support "anonymous tags,"
     * given the amount of trouble we had naming this one!)  :-)  - sb
     */

    //*********************************************************************
    // Internal state

    private String value;                       // tag attribute
    private String def;				// tag attribute
    private boolean needBody;			// non-space body needed?

    //*********************************************************************
    // Construction and initialization

    /**
     * Constructs a new handler.  As with TagSupport, subclasses should
     * not provide other constructors and are expected to call the
     * superclass constructor.
     */
    public ExprTag() {
        super();
        init();
    }

    // resets local state
    private void init() {
        value = def = null;
	needBody = false;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
        super.release();
        init();
    }


    //*********************************************************************
    // Tag logic

    // evaluates 'value' and determines if the body should be evaluted
    public int doStartTag() throws JspException {
      try {
	Object result;

	// try to evaluate 'value'
	try {
            result = ExpressionEvaluatorManager.evaluate(
                "value", value, Object.class, this, pageContext);
	} catch (ExpressionException ex) {
	    result = null;	// expression-specific error; no result
	}

	// print value if available; otherwise, try 'default'
	if (result != null) {
	    pageContext.getOut().print(result);
	    return SKIP_BODY;
	} else {
	    // if we don't have a 'default' attribute, just go to the body
	    if (def == null) {
		needBody = true;
		return EVAL_BODY_BUFFERED;
	    }

	    // if we do have 'default', try to evaluate it
	    result = ExpressionEvaluatorManager.evaluate(
		"default", def, Object.class, this, pageContext);
	    if (result != null) {
		// good 'default'
		pageContext.getOut().print(result);
		return SKIP_BODY;
	    } else {
		// bad 'default'
		throw new NullAttributeException("expr", "default");
	    }
	}
      } catch (IOException ex) {
	throw new JspException(ex.getMessage(), ex);
      }
    }

    // prints the body if necessary; reports errors
    public int doEndTag() throws JspException {
      try {
	if (!needBody)
	    return EVAL_PAGE;		// nothing more to do

	// trim and print out the body
	if (bodyContent != null && bodyContent.getString() != null)
	    pageContext.getOut().print(bodyContent.getString().trim());
	return EVAL_PAGE;
      } catch (IOException ex) {
	throw new JspException(ex.getMessage(), ex);
      }
    }


    //*********************************************************************
    // Accessor methods

    // for tag attribute
    public void setValue(String value) {
        this.value = value;
    }

    // for tag attribute
    public void setDefault(String def) {
        this.def = def;
    }
}
