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

package org.apache.taglibs.standard.tag.el.xml;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.apache.taglibs.standard.tag.common.xml.*;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>A handler for &lt;transform&gt; that accepts attributes as Strings
 * and evaluates them as expressions at runtime.</p>
 *
 * @author Shawn Bayern
 */
public class TransformTag extends TransformSupport {

    //*********************************************************************
    // 'Private' state (implementation details)

    private String xmlText_;                    // stores EL-based property
    private String xmlUrl_;                     // stores EL-based property
    private String xsltText_;			// stores EL-based property
    private String xsltUrl_;			// stores EL-based property
    private String result_;			// stores EL-based property


    //*********************************************************************
    // Constructor

    public TransformTag() {
        super();
        init();
    }


    //*********************************************************************
    // Tag logic

    // evaluates expression and chains to parent
    public int doStartTag() throws JspException {

        // evaluate any expressions we were passed, once per invocation
        evaluateExpressions();

	// chain to the parent implementation
	return super.doStartTag();
    }


    // Releases any resources we may have (or inherit)
    public void release() {
        super.release();
        init();
    }


    //*********************************************************************
    // Accessor methods

    // for EL-based attribute
    public void setXmlText(String xmlText_) {
        this.xmlText_ = xmlText_;
    }

    // for EL-based attribute
    public void setXmlUrl(String xmlUrl_) {
        this.xmlUrl_ = xmlUrl_;
    }

    // for EL-based attribute
    public void setXsltText(String xsltText_) {
        this.xsltText_ = xsltText_;
    }

    // for EL-based attribute
    public void setXsltUrl(String xsltUrl_) {
        this.xsltUrl_ = xsltUrl_;
    }

    /* Removed for RI 0.5 
     // for EL-based attribute
     public void setTransformer(String transformer_) {
         this.transformer_ = transformer_;
     }
    */

    // for EL-based attribute
    public void setResult(String result_) {
        this.result_ = result_;
    }


    //*********************************************************************
    // Private (utility) methods

    // (re)initializes state (during release() or construction)
    private void init() {
        // null implies "no expression"
	xmlText_ = xmlUrl = xsltText_ = xsltUrl_ = result_ = null;
    }

    /* Evaluates expressions as necessary */
    private void evaluateExpressions() throws JspException {
        /* 
         * Note: we don't check for type mismatches here; we assume
         * the expression evaluator will return the expected type
         * (by virtue of knowledge we give it about what that type is).
         * A ClassCastException here is truly unexpected, so we let it
         * propagate up.
         */

	xmlText = ExpressionUtil.evalNotNull(
	    "transform", "xmlText", xmlText_, Object.class, this, pageContext);
	xmlUrl = (String) ExpressionUtil.evalNotNull(
	    "transform", "xmlUrl", xmlUrl_, String.class, this, pageContext);
	xsltText= ExpressionUtil.evalNotNull(
	    "transform", "xsltText", xsltText_, Object.class, this,
	    pageContext);
	xsltUrl = (String) ExpressionUtil.evalNotNull(
	    "transform", "xsltUrl", xsltUrl_, String.class, this, pageContext);
//	transformer = (Transformer) ExpressionUtil.evalNotNull(
//	    "transform", "transformer", transformer_, Transformer.class,
//	     this, pageContext);
	result = (Result) ExpressionUtil.evalNotNull(
	    "transform", "result", result_, Result.class, this, pageContext);
    }
}
