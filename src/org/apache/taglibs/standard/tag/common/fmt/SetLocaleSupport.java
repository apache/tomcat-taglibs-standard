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
import javax.servlet.ServletResponse;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;setLocale&gt;, the locale setting tag in
 * JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class SetLocaleSupport extends TagSupport {

    
    //*********************************************************************
    // Private constants

    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';


    //*********************************************************************
    // Protected state

    protected Object value;                      // 'value' attribute
    protected String variant;                    // 'variant' attribute


    //*********************************************************************
    // Private state

    private int scope;                           // 'scope' attribute


    //*********************************************************************
    // Constructor and initialization

    public SetLocaleSupport() {
	super();
	init();
    }

    private void init() {
	value = variant = null;
	scope = PageContext.PAGE_SCOPE;
    }


   //*********************************************************************
    // Tag attributes known at translation time

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Tag logic

    public int doEndTag() throws JspException {
	Locale locale = null;

	if (value == null) {
	    locale = Locale.getDefault();
	} else if (value instanceof String) {
	    if (((String) value).trim().equals("")) {
		locale = Locale.getDefault();
	    } else {
		locale = parseLocale((String) value, variant);
	    }
	} else {
	    locale = (Locale) value;
	}

	Config.set(pageContext, Config.FMT_LOCALE, locale, scope);
	setResponseLocale(pageContext, locale);

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Public utility methods

    /**
     * See parseLocale(String, String) for details.
     */
    public static Locale parseLocale(String locale) {
	return parseLocale(locale, null);
    }

    /**
     * Parses the given locale string into its language and (optionally)
     * country components, and returns the corresponding
     * <tt>java.util.Locale</tt> object.
     *
     * If the given locale string is null or empty, the runtime's default
     * locale is returned.
     *
     * @param locale the locale string to parse
     * @param variant the variant
     *
     * @return <tt>java.util.Locale</tt> object corresponding to the given
     * locale string, or the runtime's default locale if the locale string is
     * null or empty
     *
     * @throws IllegalArgumentException if the given locale does not have a
     * language component or has an empty country component
     */
    public static Locale parseLocale(String locale, String variant) {

	Locale ret = null;
	String language = locale;
	String country = null;
	int index = -1;

	if (((index = locale.indexOf(HYPHEN)) > -1)
	        || ((index = locale.indexOf(UNDERSCORE)) > -1)) {
	    language = locale.substring(0, index);
	    country = locale.substring(index+1);
	}

	if ((language == null) || (language.length() == 0)) {
	    throw new IllegalArgumentException(
		Resources.getMessage("LOCALE_NO_LANGUAGE"));
	}

	if (country == null) {
	    if (variant != null)
		ret = new Locale(language, "", variant);
	    else
		ret = new Locale(language, "");
	} else if (country.length() > 0) {
	    if (variant != null)
		ret = new Locale(language, country, variant);
	    else
		ret = new Locale(language, country);
	} else {
	    throw new IllegalArgumentException(
		Resources.getMessage("LOCALE_EMPTY_COUNTRY"));
	}

	return ret;
    }


    //*********************************************************************
    // Package-scoped utility methods

    /*
     * Stores the given locale in the response object of the given page
     * context, and stores the locale's associated charset in the
     * javax.servlet.jsp.jstl.fmt.request.charset session attribute, which
     * may be used by the <requestEncoding> action in a page invoked by a
     * form included in the response to set the request charset to the same as
     * the response charset (this makes it possible for the container to
     * decode the form parameter values properly, since browsers typically
     * encode form field values using the response's charset).
     *
     * @param pageContext the page context whose response object is assigned
     * the given locale
     * @param locale the response locale
     */
    static void setResponseLocale(PageContext pageContext, Locale locale) {
	// set response locale
	ServletResponse response = pageContext.getResponse();
	response.setLocale(locale);
	
	// get response character encoding and store it in session attribute
	pageContext.setAttribute(RequestEncodingSupport.REQUEST_CHAR_SET,
				 response.getCharacterEncoding(),
				 PageContext.SESSION_SCOPE);
    }
 
    /*
     * Returns the formatting locale to use with the given formatting action
     * in the given page.
     *
     * @param pc The page context containing the formatting action
     * @param fromTag The formatting action
     * @param format <tt>true</tt> if the formatting action is of type
     * <formatXXX> (as opposed to <parseXXX>), and <tt>false</tt> otherwise
     * (if set to <tt>true</tt>, the formatting locale that is returned by
     * this method is used to set the response locale).
     *
     * @param avail the array of available locales
     *
     * @return the formatting locale to use
     */
    static Locale getFormattingLocale(PageContext pc,
				      Tag fromTag,
				      boolean format,
				      Locale[] avail) {
	LocalizationContext locCtxt = null;
	
	// Get formatting locale from enclosing <fmt:bundle>
	Tag parent = findAncestorWithClass(fromTag, BundleSupport.class);
	if (parent != null) {
	    // use locale from parent <fmt:bundle> tag
	    if ((locCtxt = ((BundleSupport) parent).getLocalizationContext())
		                                                != null) {
		ResourceBundle bundle = locCtxt.getResourceBundle();
		if (bundle != null) {
		    if (format) {
			setResponseLocale(pc, bundle.getLocale());
		    }
		    return bundle.getLocale();
		}
	    }
	}

	// Get formatting locale from default I18N localization context
	if ((locCtxt = BundleSupport.getLocalizationContext(pc)) != null) {
	    ResourceBundle bundle = locCtxt.getResourceBundle();
	    if (bundle != null) {
		if (format) {
		    setResponseLocale(pc, bundle.getLocale());
		}
		return bundle.getLocale();
	    }
	}

	/*
	 * Establish formatting locale by comparing the preferred locales
	 * (in order of preference) against the available formatting
	 * locales, and determining the best matching locale.
	 */
	Locale match = null;
	Locale pref = getLocale(pc, Config.FMT_LOCALE);
	if (pref != null) {
	    // Preferred locale is application-based
	    match = findFormattingMatch(pref, avail);
	} else {
	    // Preferred locales are browser-based 
	    match = findFormattingMatch(pc, avail);
	}
	if (match == null) {
	    //Use fallback locale.
	    pref = getLocale(pc, Config.FMT_FALLBACKLOCALE);
	    if (pref != null) {
		match = findFormattingMatch(pref, avail);
	    }
	}
 	if (format && (match != null)) {
	    setResponseLocale(pc, match);
	}

	return match;
    }

    /*
     * Returns the locale specified by the named scoped attribute or context
     * configuration parameter.
     *
     * <p> The named scoped attribute is searched in the page, request,
     * session (if valid), and application scope(s) (in this order). If no such
     * attribute exists in any of the scopes, the locale is taken from the
     * named context configuration parameter.
     *
     * @param pageContext the page in which to search for the named scoped
     * attribute or context configuration parameter
     * @param name the name of the scoped attribute or context configuration
     * parameter
     *
     * @return the locale specified by the named scoped attribute or context
     * configuration parameter, or <tt>null</tt> if no scoped attribute or
     * configuration parameter with the given name exists
     */
    static Locale getLocale(PageContext pageContext, String name) {
	Locale loc = null;

	Object obj = Config.find(pageContext, name);
	if (obj != null) {
	    if (obj instanceof Locale) {
		loc = (Locale) obj;
	    } else {
		loc = parseLocale((String) obj);
	    }
	}

	return loc;
    }


    //*********************************************************************
    // Private utility methods

    /*
     * Determines the client's preferred locales from the request, and compares
     * each of the locales (in order of preference) against the available
     * locales in order to determine the best matching locale.
     *
     * @param pageContext Page containing the formatting action
     * @param avail Available formatting locales
     *
     * @return Best matching locale, or <tt>null</tt> if no match was found
     */
    private static Locale findFormattingMatch(PageContext pageContext,
					      Locale[] avail) {
	Locale match = null;

	for (Enumeration enum = pageContext.getRequest().getLocales();
	     enum.hasMoreElements(); ) {
	    /*
	     * If client request doesn't provide an Accept-Language header,
	     * the returned locale Enumeration contains the runtime's default
	     * locale, so it always contains at least one element.
	     */
	    match = findFormattingMatch((Locale) enum.nextElement(), avail);
	    if (match != null) {
		break;
	    }
	}
	
	return match;
    }

    /*
     * Returns the best match between the given preferred locale and the
     * given available locales.
     *
     * The best match is given as the first available locale that exactly
     * matches the given preferred locale ("exact match"). If no exact match
     * exists, the best match is given as the first available locale that 
     * matches the preferred locale's language component and does not have any
     * country component ("language match").
     *
     * @param pref the preferred locale
     * @param avail the available formatting locales
     *
     * @return Available locale that best matches the given preferred locale,
     * or <tt>null</tt> if no match exists
     */
    private static Locale findFormattingMatch(Locale pref, Locale[] avail) {
	Locale match = null;

	for (int i=0; i<avail.length; i++) {
	    if (pref.equals(avail[i])) {
		// Exact match
		match = avail[i];
		break;
	    } else {
		if (pref.getLanguage().equals(avail[i].getLanguage())
		        && ("".equals(avail[i].getCountry()))) {
		    // Language match
		    if (match == null) {
			match = avail[i];
		    }
		}
	    }
	}

	return match;
    }
}
