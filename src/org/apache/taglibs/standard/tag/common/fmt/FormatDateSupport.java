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

public abstract class FormatDateSupport extends BodyTagSupport {

    //*********************************************************************
    // Private constants

    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DATETIME = "both";


    //*********************************************************************
    // Protected state

    protected Object value;                      // 'value' attribute
    protected String type;                       // 'type' attribute
    protected String pattern;                    // 'pattern' attribute
    protected Object timeZone;                   // 'timeZone' attribute
    protected String dateStyle;                  // 'dateStyle' attribute
    protected String timeStyle;                  // 'timeStyle' attribute


    //*********************************************************************
    // Private state

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
	type = DATE;
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

    /*
     * Formats the given date and time.
     */
    public int doEndTag() throws JspException {

	/*
	 * If 'value' is null or equal to "", format the current date/time.
	 * Note that this is the only action in which we need to check for an
	 * empty body if 'value' is missing. For all other actions, the
	 * TLV would have reported this as an error. However, with this action,
	 * it is legal for 'value' to be missing and the body to be empty, in
	 * which case the current date/time is formatted. 
	 */
	if (value == null) {
            String bcs = null;
	    if ((getBodyContent() == null)
		    || ((bcs = getBodyContent().getString()) == null)
		    || (value = bcs.trim()).equals("")) {
		value = new Date();
	    }
	}

	/*
	 * If the date and/or time is given as a string literal, it is first
	 * parsed into an instance of java.util.Date according to the default
	 * pattern of the "en" locale.
	 */
	if (value instanceof String) {
	    DateFormat parser
		= DateFormat.getDateInstance(DateFormat.DEFAULT,
					     Locale.ENGLISH);
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
	    DateFormat.getAvailableLocales());
	DateFormat formatter = createFormatter(locale);

	// Apply pattern, if present
	if (pattern != null) {
	    try {
		((SimpleDateFormat) formatter).applyPattern(pattern);
	    } catch (ClassCastException cce) {
		formatter = new SimpleDateFormat(pattern, locale);
	    }
	}

	// Set time zone
	TimeZone tz = null;
	if (timeZone != null) {
	    if (timeZone instanceof String) {
		tz = TimeZone.getTimeZone((String) timeZone);
	    } else if (timeZone instanceof TimeZone) {
		tz = (TimeZone) timeZone;
	    } else {
		throw new JspTagException(
                    Resources.getMessage("FORMAT_DATE_BAD_TIMEZONE"));
	    }
	} else {
	    tz = TimeZoneSupport.getTimeZone(pageContext, this);
	}
	if (tz != null) {
	    formatter.setTimeZone(tz);
	}

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

    private DateFormat createFormatter(Locale loc) throws JspException {
	DateFormat formatter = null;

	if (DATE.equalsIgnoreCase(type)) {
	    formatter = DateFormat.getDateInstance(
	        Util.getStyle(dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"),
		loc);
	} else if (TIME.equalsIgnoreCase(type)) {
	    formatter = DateFormat.getTimeInstance(
	        Util.getStyle(timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"),
		loc);
	} else if (DATETIME.equalsIgnoreCase(type)) {
	    formatter = DateFormat.getDateTimeInstance(
	        Util.getStyle(dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"),
		Util.getStyle(timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"),
		loc);
	} else {
	    throw new JspException(
                    Resources.getMessage("FORMAT_DATE_INVALID_TYPE", 
					 type));
	}

	return formatter;
    }
}
