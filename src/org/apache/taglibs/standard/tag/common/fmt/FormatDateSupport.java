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
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;formatDate&gt;, the date and time
 * formatting tag in JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class FormatDateSupport extends TagSupport {

    //*********************************************************************
    // Public constants

    public static final String DATE_STRING = "date";
    public static final String TIME_STRING = "time";
    public static final String DATETIME_STRING = "both";

    public static final String DEFAULT_STYLE = "default";
    public static final String SHORT_STYLE = "short";
    public static final String MEDIUM_STYLE = "medium";
    public static final String LONG_STYLE = "long";
    public static final String FULL_STYLE = "full";


    //*********************************************************************
    // Package-scoped constants

    static final int DATE_TYPE = 0;
    static final int TIME_TYPE = 1;
    static final int DATETIME_TYPE = 2;
    

    //*********************************************************************
    // Protected state

    protected Object value;                      // 'value' attribute
    protected String pattern;                    // 'pattern' attribute
    protected TimeZone timeZone;                 // 'timeZone' attribute
    protected Locale parseLocale;                // 'parseLocale' attribute


    //*********************************************************************
    // Private state

    private int type;                            // 'type' attribute
    private int dateStyle;                       // 'dateStyle' attribute
    private int timeStyle;                       // 'timeStyle' attribute
    private String var;                          // 'var' attribute
    private int scope;                           // 'scope' attribute


    //*********************************************************************
    // Constructor and initialization

    public FormatDateSupport() {
	super();
	init();
    }

    private void init() {
	pattern = var = null;
	value = null;
	timeZone = null;
	type = DATE_TYPE;
	dateStyle = timeStyle = DateFormat.DEFAULT;
	scope = PageContext.PAGE_SCOPE;
	parseLocale = null;
    }


   //*********************************************************************
    // Tag attributes known at translation time

    public void setType(String type) {
	if (TIME_STRING.equalsIgnoreCase(type))
	    this.type = TIME_TYPE;
	else if (DATETIME_STRING.equalsIgnoreCase(type))
	    this.type = DATETIME_TYPE;
    }

    public void setDateStyle(String dateStyle) {
        this.dateStyle = getStyle(dateStyle);
    }

    public void setTimeStyle(String timeStyle) {
        this.timeStyle = getStyle(timeStyle);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Tag logic

    /*
     * Formats the given date and time.
     */
    public int doEndTag() throws JspException {

	/*
	 * If no date or time is given, the current date and time are used.
	 * If the date and/or time is given as a string literal, it is first
	 * parsed into an instance of java.util.Date according to the default
	 * pattern of the locale given via the 'parseLocale' attribute. If this
	 * attribute is missing, the default ("en") locale is used.
	 */
	if (value == null) {
	    value = new Date();
	} else if (value instanceof String) {
	    DateFormat parser = null;
	    if (parseLocale != null)
		parser = DateFormat.getDateInstance(DateFormat.DEFAULT,
						    parseLocale);
	    else
		parser = DateFormat.getDateInstance(
                    DateFormat.DEFAULT,
		    FormatNumberSupport.EN_LOCALE);
	    try {
		value = parser.parse((String) value);
	    } catch (ParseException pe) {
		throw new JspTagException(pe.getMessage());
	    }
	}

	// Create date formatter using page's locale
	DateFormat formatter = null;
	Locale locale = LocaleSupport.getFormattingLocale(
            pageContext,
	    this,
	    DateFormat.getAvailableLocales());
	switch (type) {
	case DATE_TYPE:
	    formatter = DateFormat.getDateInstance(dateStyle, locale);
	    break;
	case TIME_TYPE:
	    formatter = DateFormat.getTimeInstance(timeStyle, locale);
	    break;
	case DATETIME_TYPE:
	    formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle,
						       locale);
	    break;
	} // switch

	// Apply pattern, if present
	if (pattern != null) {
	    try {
		((SimpleDateFormat) formatter).applyPattern(pattern);
	    } catch (ClassCastException cce) {
		formatter = new SimpleDateFormat(pattern, locale);
	    }
	}

	// Set time zone
	if (timeZone == null)
	    timeZone = TimeZoneSupport.getTimeZone(pageContext, this);
	if (timeZone != null)
	    formatter.setTimeZone(timeZone);

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
    // Package-scoped utility methods

    static int getStyle(String style) {
	int ret = DateFormat.DEFAULT;

	if (SHORT_STYLE.equalsIgnoreCase(style))
	    ret = DateFormat.SHORT;
	else if (MEDIUM_STYLE.equalsIgnoreCase(style))
	    ret = DateFormat.MEDIUM;
	else if (LONG_STYLE.equalsIgnoreCase(style))
	    ret = DateFormat.LONG;
	else if (FULL_STYLE.equalsIgnoreCase(style))
	    ret = DateFormat.FULL;

	return ret;
    }
}
