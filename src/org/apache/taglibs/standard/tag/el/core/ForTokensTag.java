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

package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.ForTokensSupport;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;

/**
 * <p>A handler for &lt;forTokens&gt; that accepts attributes as Strings
 * and evaluates them as expressions at runtime.
 *
 * @author Shawn Bayern
 */
public class ForTokensTag
    extends ForTokensSupport
    implements LoopTag, IterationTag
{

    //*********************************************************************
    // 'Private' state (implementation details)

    private String begin_;                      // raw EL-based property
    private String end_;                        // raw EL-based property
    private String step_;                       // raw EL-based property
    private String items_;			// raw EL-based property
    private String delims_;			// raw EL-based property


    //*********************************************************************
    // Constructor

    public ForTokensTag() {
        super();
        init();
    }


    //*********************************************************************
    // Tag logic

    /* Begins iterating by processing the first item. */
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
    public void setBegin(String begin_) {
        this.begin_ = begin_;
        this.beginSpecified = true;
    }

    // for EL-based attribute
    public void setEnd(String end_) {
        this.end_ = end_;
        this.endSpecified = true;
    }

    // for EL-based attribute
    public void setStep(String step_) {
        this.step_ = step_;
        this.stepSpecified = true;
    }

    // for EL-based attribute
    public void setItems(String items_) {
        this.items_ = items_;
    }

    // for EL-based attribute
    public void setDelims(String delims_) {
	this.delims_ = delims_;
    }


    //*********************************************************************
    // Private (utility) methods

    // (re)initializes state (during release() or construction)
    private void init() {
        // defaults for interface with page author
        begin_ = null;          // (no expression)
        end_ = null;            // (no expression)
        step_ = null;           // (no expression)
	items_ = null;		// (no expression)
	delims_ = null;		// (no expression)
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

       if (begin_ != null) {
            Object r = ExpressionEvaluatorManager.evaluate(
                "begin", begin_, Integer.class, this, pageContext);
            if (r == null)
                throw new NullAttributeException("forTokens", "begin");
            begin = ((Integer) r).intValue();
            validateBegin();
        }

        if (end_ != null) {
            Object r = ExpressionEvaluatorManager.evaluate(
                "end", end_, Integer.class, this, pageContext);
            if (r == null)
                throw new NullAttributeException("forTokens", "end");
            end = ((Integer) r).intValue();
            validateEnd();
        }

        if (step_ != null) {
            Object r = ExpressionEvaluatorManager.evaluate(
                "step", step_, Integer.class, this, pageContext);
            if (r == null)
                throw new NullAttributeException("forTokens", "step");
            step = ((Integer) r).intValue();
            validateStep();
        }

        if (items_ != null) {
            items = (String) ExpressionEvaluatorManager.evaluate(
                "items", items_, String.class, this, pageContext);
	    // use the empty string to indicate "no iteration"
	    if (items == null)
		items = "";
	}

        if (delims_ != null) {
            delims = (String) ExpressionEvaluatorManager.evaluate(
                "delims", delims_, String.class, this, pageContext);
	    // use the empty string to cause monolithic tokenization
	    if (delims == null)
		delims = "";
	}
    }
}
