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

package org.apache.taglibs.standard.tag.el.fmt;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.fmt.FormatNumberSupport;

/**
 * <p>A handler for &lt;formatNumber&gt; that accepts attributes as Strings
 * and evaluates them as expressions at runtime.</p>
 *
 * @author Jan Luehe
 */

public class FormatNumberTag extends FormatNumberSupport {

    //*********************************************************************
    // 'Private' state (implementation details)

    private String value_;                       // stores EL-based property
    private String type_;                        // stores EL-based property
    private String pattern_;		         // stores EL-based property
    private String currencyCode_;   	         // stores EL-based property
    private String currencySymbol_;   	         // stores EL-based property
    private String groupingUsed_;   	         // stores EL-based property
    private String maxIntegerDigits_;   	 // stores EL-based property
    private String minIntegerDigits_;   	 // stores EL-based property
    private String maxFractionDigits_;   	 // stores EL-based property
    private String minFractionDigits_;   	 // stores EL-based property


    //*********************************************************************
    // Constructor

    /**
     * Constructs a new FormatNumberTag.  As with TagSupport, subclasses
     * should not provide other constructors and are expected to call
     * the superclass constructor
     */
    public FormatNumberTag() {
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
    public void setValue(String value_) {
        this.value_ = value_;
	this.valueSpecified = true;
    }

    // for EL-based attribute
    public void setType(String type_) {
        this.type_ = type_;
    }

    // for EL-based attribute
    public void setPattern(String pattern_) {
        this.pattern_ = pattern_;
    }

    // for EL-based attribute
    public void setCurrencyCode(String currencyCode_) {
        this.currencyCode_ = currencyCode_;
    }

    // for EL-based attribute
    public void setCurrencySymbol(String currencySymbol_) {
        this.currencySymbol_ = currencySymbol_;
    }

    // for EL-based attribute
    public void setGroupingUsed(String groupingUsed_) {
        this.groupingUsed_ = groupingUsed_;
	this.groupingUsedSpecified = true;
    }

    // for EL-based attribute
    public void setMaxIntegerDigits(String maxIntegerDigits_) {
        this.maxIntegerDigits_ = maxIntegerDigits_;
	this.maxIntegerDigitsSpecified = true;
    }

    // for EL-based attribute
    public void setMinIntegerDigits(String minIntegerDigits_) {
        this.minIntegerDigits_ = minIntegerDigits_;
	this.minIntegerDigitsSpecified = true;
    }

    // for EL-based attribute
    public void setMaxFractionDigits(String maxFractionDigits_) {
        this.maxFractionDigits_ = maxFractionDigits_;
	this.maxFractionDigitsSpecified = true;
    }

    // for EL-based attribute
    public void setMinFractionDigits(String minFractionDigits_) {
        this.minFractionDigits_ = minFractionDigits_;
	this.minFractionDigitsSpecified = true;
    }


    //*********************************************************************
    // Private (utility) methods

    // (re)initializes state (during release() or construction)
    private void init() {
        // null implies "no expression"
	value_ = type_ = pattern_ = null;
	currencyCode_ = currencySymbol_ = null;
	groupingUsed_ = null;
	maxIntegerDigits_ = minIntegerDigits_ = null;
	maxFractionDigits_ = minFractionDigits_ = null;
    }

    // Evaluates expressions as necessary
    private void evaluateExpressions() throws JspException {
	Object obj = null;

        /* 
         * Note: we don't check for type mismatches here; we assume
         * the expression evaluator will return the expected type
         * (by virtue of knowledge we give it about what that type is).
         * A ClassCastException here is truly unexpected, so we let it
         * propagate up.
         */

	// 'value' attribute
	if (value_ != null) {
	    value = ExpressionEvaluatorManager.evaluate(
	        "value", value_, Object.class, this, pageContext);
	}

	// 'type' attribute
	if (type_ != null) {
	    type = (String) ExpressionEvaluatorManager.evaluate(
	        "type", type_, String.class, this, pageContext);
	}

	// 'pattern' attribute
	if (pattern_ != null) {
	    pattern = (String) ExpressionEvaluatorManager.evaluate(
	        "pattern", pattern_, String.class, this, pageContext);
	}

	// 'currencyCode' attribute
	if (currencyCode_ != null) {
	    currencyCode = (String) ExpressionEvaluatorManager.evaluate(
	        "currencyCode", currencyCode_, String.class, this,
		pageContext);
	}

	// 'currencySymbol' attribute
	if (currencySymbol_ != null) {
	    currencySymbol = (String) ExpressionEvaluatorManager.evaluate(
	        "currencySymbol", currencySymbol_, String.class, this,
		pageContext);
	}

	// 'groupingUsed' attribute
	if (groupingUsed_ != null) {
	    obj = ExpressionEvaluatorManager.evaluate(
	        "groupingUsed", groupingUsed_, Boolean.class, this,
		pageContext);
	    if (obj != null) {
		isGroupingUsed = ((Boolean) obj).booleanValue();
	    }
	}

	// 'maxIntegerDigits' attribute
	if (maxIntegerDigits_ != null) {
	    obj = ExpressionEvaluatorManager.evaluate(
	        "maxIntegerDigits", maxIntegerDigits_, Integer.class, this,
		pageContext);
	    if (obj != null) {
		maxIntegerDigits = ((Integer) obj).intValue();
	    }
	}

	// 'minIntegerDigits' attribute	
	if (minIntegerDigits_ != null) {
	    obj = ExpressionEvaluatorManager.evaluate(
	        "minIntegerDigits", minIntegerDigits_, Integer.class, this,
		pageContext);
	    if (obj != null) {
		minIntegerDigits = ((Integer) obj).intValue();
	    }
	}

	// 'maxFractionDigits' attribute
	if (maxFractionDigits_ != null) {
	    obj = ExpressionEvaluatorManager.evaluate(
	        "maxFractionDigits", maxFractionDigits_, Integer.class, this,
		pageContext);
	    if (obj != null) {
		maxFractionDigits = ((Integer) obj).intValue();
	    }
	}

	// 'minFractionDigits' attribute
	if (minFractionDigits_ != null) {
	    obj = ExpressionEvaluatorManager.evaluate(
	        "minFractionDigits", minFractionDigits_, Integer.class, this,
		pageContext);
	    if (obj != null) {
		minFractionDigits = ((Integer) obj).intValue();
	    }
	}
    }
}

