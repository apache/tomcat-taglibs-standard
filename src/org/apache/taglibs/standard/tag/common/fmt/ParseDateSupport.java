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

import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;parseDate&gt;, the date and time
 * parsing tag in JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class ParseDateSupport extends BodyTagSupport {

    //*********************************************************************
    // Private constants

    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DATETIME = "both";


    //*********************************************************************
    // Protected state

    protected String value;                      // 'value' attribute
    protected String type;                       // 'type' attribute
    protected String pattern;                    // 'pattern' attribute
    protected Object timeZone;                   // 'timeZone' attribute
    protected Locale parseLocale;                // 'parseLocale' attribute
    protected String dateStyle;                  // 'dateStyle' attribute
    protected String timeStyle;                  // 'timeStyle' attribute


    //*********************************************************************
    // Private state

    private String var;                          // 'var' attribute
    private int scope;                           // 'scope' attribute


    //*********************************************************************
    // Constructor and initialization

    public ParseDateSupport() {
	super();
	init();
    }

    private void init() {
	type = dateStyle = timeStyle = null;
	value = pattern = var = null;
	timeZone = null;
	scope = PageContext.PAGE_SCOPE;
	parseLocale = null;
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
	}
	if ((value == null) || value.equals("")) {
	    if (var != null) {
		pageContext.removeAttribute(var, scope);
	    }
	    return EVAL_PAGE;
	}

	/*
	 * Set up parsing locale: Use locale specified via the 'parseLocale'
	 * attribute (if present), or else determine page's locale.
	 */
	Locale locale = parseLocale;
	if (locale == null)
	    locale = SetLocaleSupport.getFormattingLocale(
                pageContext,
	        this,
		false,
	        DateFormat.getAvailableLocales());
	if (locale == null) {
	    throw new JspException(
                    Resources.getMessage("PARSE_DATE_NO_PARSE_LOCALE"));
	}

	// Create parser
	DateFormat parser = createParser(locale);

	// Apply pattern, if present
	if (pattern != null) {
	    try {
		((SimpleDateFormat) parser).applyPattern(pattern);
	    } catch (ClassCastException cce) {
		parser = new SimpleDateFormat(pattern, locale);
	    }
	}

	// Set time zone
	TimeZone tz = null;
	if ((timeZone instanceof String) && ((String) timeZone).equals("")) {
	    timeZone = null;
	}
	if (timeZone != null) {
	    if (timeZone instanceof String) {
		tz = TimeZone.getTimeZone((String) timeZone);
	    } else if (timeZone instanceof TimeZone) {
		tz = (TimeZone) timeZone;
	    } else {
		throw new JspException(
                    Resources.getMessage("PARSE_DATE_BAD_TIMEZONE"));
	    }
	} else {
	    tz = TimeZoneSupport.getTimeZone(pageContext, this);
	}
	if (tz != null) {
	    parser.setTimeZone(tz);
	}

	// Parse date
	Date parsed = null;
	try {
	    parsed = parser.parse(value);
	} catch (ParseException pe) {
	    throw new JspException(
	            Resources.getMessage("PARSE_DATE_PARSE_ERROR", value),
		    pe);
	}

	if (var != null) {
	    pageContext.setAttribute(var, parsed, scope);	
	} else {
	    try {
		pageContext.getOut().print(parsed);
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

    private DateFormat createParser(Locale loc) throws JspException {
	DateFormat parser = null;

	if ((type == null) || DATE.equalsIgnoreCase(type)) {
	    parser = DateFormat.getDateInstance(
	        Util.getStyle(dateStyle, "PARSE_DATE_INVALID_DATE_STYLE"),
		loc);
	} else if (TIME.equalsIgnoreCase(type)) {
	    parser = DateFormat.getTimeInstance(
	        Util.getStyle(timeStyle, "PARSE_DATE_INVALID_TIME_STYLE"),
		loc);
	} else if (DATETIME.equalsIgnoreCase(type)) {
	    parser = DateFormat.getDateTimeInstance(
	        Util.getStyle(dateStyle, "PARSE_DATE_INVALID_DATE_STYLE"),
		Util.getStyle(timeStyle, "PARSE_DATE_INVALID_TIME_STYLE"),
		loc);
	} else {
	    throw new JspException(
                    Resources.getMessage("PARSE_DATE_INVALID_TYPE", type));
	}

	parser.setLenient(false);

	return parser;
    }
}
