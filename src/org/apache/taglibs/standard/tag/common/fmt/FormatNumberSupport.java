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
import java.io.IOException;
import java.lang.reflect.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;formatNumber&gt;, the number
 * formatting tag in JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class FormatNumberSupport extends BodyTagSupport {

    //*********************************************************************
    // Private constants

    private static final Class[] GET_INSTANCE_PARAM_TYPES =
	new Class[] { String.class };
    private static final String NUMBER = "number";    
    private static final String CURRENCY = "currency";
    private static final String PERCENT = "percent";


    //*********************************************************************
    // Protected state

    protected Object value;                    // 'value' attribute
    protected String type;                     // 'type' attribute
    protected String pattern;                  // 'pattern' attribute
    protected String currencyCode;             // 'currencyCode' attribute
    protected String currencySymbol;           // 'currencySymbol' attribute
    protected boolean isGroupingUsed;          // 'groupingUsed' attribute
    protected boolean groupingUsedSpecified;
    protected int maxIntegerDigits;            // 'maxIntegerDigits' attribute
    protected boolean maxIntegerDigitsSpecified;
    protected int minIntegerDigits;            // 'minIntegerDigits' attribute
    protected boolean minIntegerDigitsSpecified;
    protected int maxFractionDigits;           // 'maxFractionDigits' attribute
    protected boolean maxFractionDigitsSpecified;
    protected int minFractionDigits;           // 'minFractionDigits' attribute
    protected boolean minFractionDigitsSpecified;


    //*********************************************************************
    // Private state

    private String var;                        // 'var' attribute
    private int scope;                         // 'scope' attribute
    private static Class currencyClass;


    //*********************************************************************
    // Constructor and initialization

    static {
	try {
	    currencyClass = Class.forName("java.util.Currency");
	    // container's runtime is J2SE 1.4 or greater
	} catch (Exception cnfe) {
	}
    }

    public FormatNumberSupport() {
	super();
	init();
    }

    private void init() {
	value = type = null;
	pattern = var = currencyCode = currencySymbol = null;
	groupingUsedSpecified = false;
	maxIntegerDigitsSpecified = minIntegerDigitsSpecified = false;
	maxFractionDigitsSpecified = minFractionDigitsSpecified = false;
	scope = PageContext.PAGE_SCOPE;
    }


   //*********************************************************************
    // Tag attributes known at translation time

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Tag logic

    public int doEndTag() throws JspException {
	if (value == null) {
	    BodyContent bc = null;
	    String bcs = null;
	    if (((bc = getBodyContent()) != null)
		    && ((bcs = bc.getString()) != null)) {
		value = bcs.trim();
	    }
	    if ((value == null) || value.equals("")) {
		// do nothing
		return EVAL_PAGE;
	    }
	}

	/*
	 * If 'value' is a String, it is first parsed into an instance of
	 * java.lang.Number according to the default pattern of the "en"
	 * locale.
	 */
	if (value instanceof String) {
	    NumberFormat parser
		= NumberFormat.getNumberInstance(Locale.ENGLISH);
	    try {
		value = parser.parse((String) value);
	    } catch (ParseException pe) {
		throw new JspTagException(pe.getMessage());
	    }
	}

	// Create formatter 
	Locale locale = LocaleSupport.getFormattingLocale(
            pageContext,
	    this,
	    true,
	    NumberFormat.getAvailableLocales());
	NumberFormat formatter = createFormatter(locale);

	// Configure the formatter
	configureFormatter(formatter);

	// Format given numeric value
	String formatted = formatter.format(value);
	if (var != null) {
	    pageContext.setAttribute(var, formatted, scope);	
	} else {
	    try {
		pageContext.getOut().print(formatted);
	    } catch (IOException ioe) {
		throw new JspTagException(ioe.getMessage());
	    }
	}

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Private utility methods

    private NumberFormat createFormatter(Locale loc) throws JspException {
	NumberFormat formatter = null;
	
	if ((type == null) || NUMBER.equalsIgnoreCase(type)) {
	    formatter = NumberFormat.getNumberInstance(loc);
	    if (pattern != null) {
		/*
		 * Let potential ClassCastException propagate up (will almost
		 * never happen)
		 */
		DecimalFormat df = (DecimalFormat) formatter;
		df.applyPattern(pattern);
	    }
	} else if (CURRENCY.equalsIgnoreCase(type)) {
	    formatter = NumberFormat.getCurrencyInstance(loc);
	    if ((currencyCode != null) || (currencySymbol != null)) {
		try {
		    setCurrency(formatter);
		} catch (Exception e) {
		    throw new JspTagException(e.getMessage());
		}
	    }
	} else if (PERCENT.equalsIgnoreCase(type)) {
	    formatter = NumberFormat.getPercentInstance(loc);
	} else {
	    throw new JspException(
	        Resources.getMessage("FORMAT_NUMBER_INVALID_TYPE", type));
	}
	
	return formatter;
    }

    private void configureFormatter(NumberFormat formatter) {
	if (groupingUsedSpecified)
	    formatter.setGroupingUsed(isGroupingUsed);
	if (maxIntegerDigitsSpecified)
	    formatter.setMaximumIntegerDigits(maxIntegerDigits);
	if (minIntegerDigitsSpecified)
	    formatter.setMinimumIntegerDigits(minIntegerDigits);
	if (maxFractionDigitsSpecified)
	    formatter.setMaximumFractionDigits(maxFractionDigits);
	if (minFractionDigitsSpecified)
	    formatter.setMinimumFractionDigits(minFractionDigits);
    }

    /*
     * Override the formatting locale's default currency symbol with the
     * specified currency code (specified via the "currencyCode" attribute) or
     * currency symbol (specified via the "currencySymbol" attribute).
     *
     * If both "currencyCode" and "currencySymbol" are present,
     * "currencyCode" takes precedence over "currencySymbol" if the
     * java.util.Currency class is defined in the container's runtime (that
     * is, if the container's runtime is J2SE 1.4 or greater), and
     * "currencySymbol" takes precendence over "currencyCode" otherwise.
     *
     * If only "currencyCode" is given, it is used as a currency symbol if
     * java.util.Currency is not defined.
     *
     * Example:
     *
     * JDK    "currencyCode" "currencySymbol" Currency symbol being displayed
     * -----------------------------------------------------------------------
     * all         ---            ---         Locale's default currency symbol
     *
     * <1.4        EUR            ---         EUR
     * >=1.4       EUR            ---         Locale's currency symbol for Euro
     *
     * all         ---           \u20AC       \u20AC
     * 
     * <1.4        EUR           \u20AC       \u20AC
     * >=1.4       EUR           \u20AC       Locale's currency symbol for Euro
     */
    private void setCurrency(NumberFormat currencyFormat) throws Exception {
	String code = null;
	String symbol = null;

	if ((currencyCode != null) && (currencySymbol != null)) {
	    if (currencyClass != null)
		code = currencyCode;
	    else
		symbol = currencySymbol;
	} else if (currencyCode == null) {
	    symbol = currencySymbol;
	} else {
	    if (currencyClass != null)
		code = currencyCode;
	    else
		symbol = currencyCode;
	}

	if (code != null) {
	    Object[] methodArgs = new Object[1];

	    /*
	     * java.util.Currency.getInstance()
	     */
	    Method m = currencyClass.getMethod("getInstance",
					       GET_INSTANCE_PARAM_TYPES);
	    methodArgs[0] = code;
	    Object currency = m.invoke(null, methodArgs);

	    /*
	     * java.text.NumberFormat.setCurrency()
	     */
	    Class[] paramTypes = new Class[1];
	    paramTypes[0] = currencyClass;
	    Class numberFormatClass = Class.forName("java.text.NumberFormat");
	    m = numberFormatClass.getMethod("setCurrency", paramTypes);
	    methodArgs[0] = currency;
	    m.invoke(currencyFormat, methodArgs);
	} else {
	    /*
	     * Let potential ClassCastException propagate up (will almost
	     * never happen)
	     */
	    DecimalFormat df = (DecimalFormat) currencyFormat;
	    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
	    dfs.setCurrencySymbol(symbol);
	    df.setDecimalFormatSymbols(dfs);
	}
    }
}
