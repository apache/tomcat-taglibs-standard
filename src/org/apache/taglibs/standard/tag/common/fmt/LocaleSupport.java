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

    static final String LOCALE = "javax.servlet.jsp.jstl.fmt.locale";
    static final String FALLBACK_LOCALE =
	"javax.servlet.jsp.jstl.fmt.fallbackLocale";

    
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
     * Determines the formatting locale to use with the given formatting
     * action in the given page.
     *
     * <p> The formatting locale is determined as follows:
     *
     * <ul>
     * <li> If the <tt>javax.servlet.jsp.jstl.fmt.locale</tt> scoped
     * attribute or context configuration parameter exists, use its locale.
     *
     * <li> If the formatting action is enclosed within a <bundle> action, use
     * the locale of the parent bundle.
     *
     * <li> If the <tt>javax.servlet.jsp.jstl.fmt.basename</tt> scoped
     * attribute exists, retrieve the default base name from it and use the
     * best matching locale for the resource bundle with this base name.
     *
     * <li> Compare the client's preferred locales (in order of preference)
     * against the available formatting locales given in the
     * <tt>avail</tt> parameter, and use the best matching locale.
     *
     * <li> If no match is found, use the fallback locale given by the
     * <tt>javax.servlet.jsp.jstl.fmt.fallbackLocale</tt> scoped attribute
     * or config parameter (if present).
     *
     * <li> If no match is found, use the runtime's default locale.
     * </ul>
     *
     * @param pageContext the page containing the formatting action
     * @param fromTag the formatting action
     * @param format <tt>true</tt> if the formatting action is of type
     * <formatXXX> (as opposed to <parseXXX>), and <tt>false</tt> otherwise
     * (if set to <tt>true</tt>, the formatting locale that is returned by
     * this method is used to set the response locale).
     *
     * @param avail the array of available locales
     *
     * @return the formatting locale to use
     */
    static Locale getFormattingLocale(PageContext pageContext,
				      Tag fromTag,
				      boolean format,
				      Locale[] avail) {
	Locale[] pref = null;

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
		    pref = getRequestLocales(pageContext);
		    ret = getBestMatch(pref, avail);
		    if (ret == null) {
			/*
			 * No match available. Use fallback locale (if defined
			 * and available).
			 */
			ret = getLocale(pageContext, FALLBACK_LOCALE);
			if (ret != null) {
			    pref = new Locale[1];
			    pref[0] = ret;
			}
			if ((ret == null)
			    || ((ret = getBestMatch(pref, avail)) == null)) {
			    /*
			     * No fallback locale defined, or specified
			     * fallback locale not among the available locales.
			     * Use runtime's default locale.
			     */
			    ret = Locale.getDefault();
			}
		    }
		}
	    }
	}
	
	if (format) {
	    LocaleSupport.setResponseLocale(pageContext, ret);
	}

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
     * Returns the best matching formatting locale.
     *
     * Each of the client's preferred locales (in order of preference) is
     * compared against the available formatting locales, and the best matching
     * locale is determined as the first available locale which either:
     *
     * - exactly matches a preferred locale
     *   (using java.util.Locale.equals()), or
     *
     * - does not have a country component and matches (just) the language 
     *   component of a preferred locale.
     *
     * @param pref the preferred locales
     * @param avail the available formatting locales
     *
     * @return the best matching formatting locale, or <tt>null</tt> if no
     * match was found
     */
    private static Locale getBestMatch(Locale[] pref, Locale[] avail) {
	Locale ret = null;

	boolean matchFound = false;
	for (int i=0; (i<pref.length) && !matchFound; i++) {
	    for (int j=0; j<avail.length; j++) {
		if (pref[i].equals(avail[j])) {
		    // Exact match
		    ret = avail[j];
		    matchFound = true;
		    break;
		} else {
		    if (pref[i].getLanguage().equals(avail[j].getLanguage())
			&& (avail[j].getCountry() == null)) {
			// Language match
			ret = avail[j];
			matchFound = true;
			break;
		    }
		}
	    }
	}

	return ret;
    }

    /*
     * Returns the preferred locales from the request as an array.
     *
     * @param pageContext the page in which the preferred request locales need
     * to be determined
     *
     * @return array of preferred request locales
     */
    private static Locale[] getRequestLocales(PageContext pageContext) {
	Vector vec = new Vector();
	for (Enumeration enum = pageContext.getRequest().getLocales();
	     enum.hasMoreElements(); ) {
	    vec.addElement((Locale) enum.nextElement());
	}
	
	/*
	 * The Enumeration returned by ServletRequest.getLocales() always
	 * contains at least one element: the default locale for the server.
	 */
	Locale[] ret = new Locale[vec.size()];
	for (int i=0; i<ret.length; i++) {
	    ret[i] = (Locale) vec.elementAt(i);
	}
	    
	return ret;
    }
}
