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
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;locale&gt;, the locale setting tag in
 * JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class LocaleSupport extends TagSupport {

    //*********************************************************************
    // Package-scoped constants

    static final String LOCALE = "javax.servlet.jsp.jstl.i18n.locale";

    
    //*********************************************************************
    // Private constants

    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';


    //*********************************************************************
    // Protected state

    protected String value;                      // 'value' attribute
    protected String variant;                    // 'variant' attribute


    //*********************************************************************
    // Private state

    private int scope;                           // 'scope' attribute


    //*********************************************************************
    // Constructor and initialization

    public LocaleSupport() {
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
	Locale locale = parseLocale(value, variant);
	pageContext.setAttribute(LOCALE, locale, scope);
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
     * Parses the given locale string into its language and (optionally)
     * country components, and returns the corresponding
     * <tt>java.util.Locale</tt> object.
     *
     * @param locale the locale string 
     * @param variant the variant
     *
     * @return the corresponding <tt>java.util.Locale</tt> object
     *
     * @throws IllegalArgumentException if the given locale does not have a
     * language component or has an empty country component
     */
    public static Locale parseLocale(String locale, String variant) {

	Locale ret = null;
	String language = locale;
	String country = null;
	int index = -1;

	if (((index = locale.indexOf(HYPHEN)) > -1) ||
	    ((index = locale.indexOf(UNDERSCORE)) > -1)) {
	    language = locale.substring(0, index);
	    country = locale.substring(index+1);
	}

	if ((language == null) || (language.length() == 0))
	    throw new IllegalArgumentException(
		Resources.getMessage("LOCALE_NO_LANGUAGE"));

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
	} else
	    throw new IllegalArgumentException(
		Resources.getMessage("LOCALE_EMPTY_COUNTRY"));

	return ret;
    }


    //*********************************************************************
    // Package-scoped utility methods

    /*
     * Stores the given locale in the response object of the given page
     * context, and stores the locale's associated charset in the
     * javax.servlet.jsp.jstl.i18n.request.charset session attribute, which
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
     * Determines the formatting locale to use with the given formatting
     * action in the given page.
     *
     * <p> The formatting locale is determined as follows:
     *
     * <ul>
     * <li> If the <tt>javax.servlet.jsp.jstl.i18n.locale</tt> scoped
     * attribute or context configuration parameter exists, use its locale.
     *
     * <li> If the formatting action is enclosed within a <bundle> action, use
     * the locale of the parent bundle.
     *
     * <li> If the <tt>javax.servlet.jsp.jstl.i18n.basename</tt> scoped
     * attribute exists, retrieve the default base name from it and use the
     * best matching locale for the resource bundle with this base name.
     *
     * <li> Compare the client's preferred locales (in order of preference)
     * against the available formatting locales given in the
     * <tt>avail</tt> parameter, and use the best matching locale.
     *
     * <li> If no match is found, use the runtime's default locale.
     * </ul>
     *
     * @param pageContext the page containing the formatting action
     * @param fromTag the formatting action
     * @param format <tt>true</tt> if the formatting action is of type
     * <formatXXX> (as opposed to <parseXXX>), and <tt>false</tt> otherwise
     * @param avail the array of available locales
     *
     * @return the formatting locale to use
     */
    static Locale getFormattingLocale(PageContext pageContext,
				      Tag fromTag,
				      boolean format,
				      Locale[] avail) {
	Locale ret = getLocale(pageContext, LOCALE);
	if (ret == null) {
	    Tag t = findAncestorWithClass(fromTag, BundleSupport.class);
	    if (t != null) {
		// use locale from parent <bundle> tag
		BundleSupport parent = (BundleSupport) t;
		ret = parent.getBundle().getLocale();
	    } else {
		ResourceBundle bundle = BundleSupport.getDefaultBundle(
                    pageContext, BundleSupport.DEFAULT_BASENAME);
		if (bundle != null) {
		    // use locale from bundle with default base name
		    ret = bundle.getLocale();
		} else {
		    // get best matching formatting locale
		    ret = getBestMatch(pageContext, avail);
		    if (ret == null) {
			// no match available, use runtime's default locale
			ret = Locale.getDefault();
		    }
		}
	    }
	}
	
	/*
	 * If this is a <formatXXX> (as opposed to a <parseXXX>) action,
	 * set the response locale
	 */
	if (format)
	    LocaleSupport.setResponseLocale(pageContext, ret);

	return ret;
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
	Locale ret = (Locale) pageContext.findAttribute(name);
	if (ret == null) {
	    String loc =
		pageContext.getServletContext().getInitParameter(name);
	    if (loc != null) {
		ret = parseLocale(loc, null);
	    }
	}

	return ret;
    }


    //*********************************************************************
    // Private utility methods
    
    /*
     * Compares the client's preferred locales (in order of preference) against
     * the available formatting locales, and returns the best matching locale.
     *
     * <p> The best matching locale is a client's preferred locale that matches
     * both the language and country components of an available formatting
     * locale. This is considered an exact match. An exact match may exist only
     * if the client's preferred locale specifies a country.
     *
     * <p> If no exact match exists, the first client locale that matches 
     * (just) the language component of an available locale is chosen.
     *
     * <p> If still no match is found, <tt>null</tt> is returned.
     *
     * @param pageContext the page in which the best matching formatting
     * locale needs to be determined
     * @param avail the available formatting locales
     *
     * @return the best matching formatting locale, or <tt>null</tt> if no
     * match was found
     */
    private static Locale getBestMatch(PageContext pageContext,
				       Locale[] avail) {
	Locale ret = null;

	boolean foundExactMatch = false;
	for (Enumeration enum = pageContext.getRequest().getLocales();
	     enum.hasMoreElements() && !foundExactMatch; ) {
	    Locale pref = (Locale) enum.nextElement();
	    for (int i=0; i<avail.length; i++) {
		if (pref.getLanguage().equals(avail[i].getLanguage())) {
		    if (pref.getCountry().length() > 0
			&& pref.getCountry().equals(avail[i].getCountry())) {
			// exact match
			ret = avail[i];
			foundExactMatch = true;	
			break;
		    } else {
			if (ret == null) {
			    ret = avail[i];
			}
		    }
		}
	    } // for
	} // for

	return ret;
    }
}
