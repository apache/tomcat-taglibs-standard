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

package org.apache.taglibs.standard.extra.i18n;

import java.util.*;
import java.text.*;
import javax.servlet.jsp.*;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Exposes the resource bundle locale-determination logic through utility 
 * methods to any tag handlers that need to produce localized messages.
 *
 * <p> A resource bundle's locale is determined as follows:
 *
 * <ul>
 * <li> If the <tt>javax.servlet.jsp.jsptl.i18n.locale</tt> scoped 
 * attribute exists, use the locale stored as its value.
 *
 * <li> Otherwise, compare the client's preferred locales (in order of
 * preference) against the available locales for the resource bundle's base
 * name, and use the best matching locale.
 * The best matching locale is defined as the client's preferred locale that
 * matches both the language and country components of an available locale for
 * the base name in question. This is considered an exact match.
 * If no exact match exists, the first client locale that matches (just) the
 * language component of an available locale is used.
 *
 * <li> If no match is found, use the fallback locale given by the
 * <tt>javax.servlet.jsp.jsptl.i18n.fallbackLocale</tt> scoped attribute, if it
 * exists.
 *
 * <li> Otherwise, use the runtime's default locale.
 * </ul>
 *
 * @author Jan Luehe
 */

public class Locale {

    /** 
     * Retrieves the localized message corresponding to the given key. 
     * 
     * The given key is looked up in the resource bundle whose base 
     * name is retrieved from the
     * <tt>javax.servlet.jsp.jsptl.i18n.basename</tt> scoped attribute and
     * whose locale is determined according to the algorithm described in
     * the header of this class. 
     * 
     * If the <tt>javax.servlet.jsp.jsptl.i18n.basename</tt> attribute is not
     * found in any of the scopes, or no resource bundle with that base name
     * exists, or the given key is undefined in the resource bundle that was
     * loaded as a result of this call, the string
     * &quot;???&lt;key&gt;???&quot; is returned, where
     * &quot;&lt;key&gt;&quot; is replaced with the given <tt>key</tt>
     * argument.
     * 
     * @param pageContext the page in which the given key must be localized 
     * @param key the message key to be looked up 
     * 
     * @return the localized message corresponding to the given key 
     */ 
    public static String getLocalizedMessage(PageContext pageContext, 
                                             String key) {
	return getLocalizedMessage(pageContext, key, null, null);
    }

    /** 
     * Retrieves the localized message corresponding to the given key. 
     * 
     * The given key is looked up in the resource bundle with the given 
     * base name whose locale is determined according to the 
     * algorithm described in the header of this class.
     * 
     * If no resource bundle with the given base name exists, 
     * or the given key is undefined in the resource bundle that was loaded as 
     * a result of this call, the string
     * &quot;???&lt;key&gt;???&quot; is returned, where
     * &quot;&lt;key&gt;&quot; is replaced with the given <tt>key</tt>
     * argument.
     * 
     * @param pageContext the page in which the given key must be localized 
     * @param key the message key to be looked up 
     * @param basename the resource bundle base name 
     * 
     * @return the localized message corresponding to the given key 
     */ 
    public static String getLocalizedMessage(PageContext pageContext, 
                                             String key, 
                                             String basename) {
	return getLocalizedMessage(pageContext, key, null, basename);
    }

    /** 
     * Retrieves the localized message corresponding to the given key and 
     * performs parametric replacement using the arguments specified in the 
     * <tt>args</tt> parameter. 
     * 
     * The given key is looked up in the resource bundle whose base 
     * name is retrieved from the
     * <tt>javax.servlet.jsp.jsptl.i18n.basename</tt> scoped attribute and
     * whose locale is determined according to the algorithm described in the
     * header of this class.
     * 
     * Before being returned, the result of the lookup undergoes parametric 
     * replacement, using the arguments specified in the <tt>args</tt> 
     * parameter. 
     * 
     * If the <tt>javax.servlet.jsp.jsptl.i18n.basename</tt> attribute is not
     * found in any of the scopes, or no resource bundle with that base name
     * exists, or the given key is undefined in the resource bundle that was
     * loaded as a result of this call, the string
     * &quot;???&lt;key&gt;???&quot; is returned, where
     * &quot;&lt;key&gt;&quot; is replaced with the given <tt>key</tt>
     * argument.
     * 
     * @param pageContext the page in which the given key must be localized 
     * @param key the message key to be looked up 
     * @param args the arguments for parametric replacement 
     * 
     * @return the localized message corresponding to the given key 
     */ 
    public static String getLocalizedMessage(PageContext pageContext, 
                                             String key, 
                                             Object[] args) {
	return getLocalizedMessage(pageContext, key, args, null);
    }

    /** 
     * Retrieves the localized message corresponding to the given key. 
     * 
     * The given key is looked up in the resource bundle with the given 
     * base name whose locale is determined according to the 
     * algorithm described in the header of this class.
     * 
     * Before being returned, the result of the lookup undergoes parametric 
     * replacement, using the arguments specified in the <tt>args</tt> 
     * parameter. 
     * 
     * If no resource bundle with the given base name exists, 
     * or the given key is undefined in the resource bundle that was loaded as 
     * a result of this call, the string &quot;???&lt;key&gt;???&quot; is
     * returned, where &quot;&lt;key&gt;&quot; is replaced with the given
     * <tt>key</tt> argument.
     * 
     * @param pageContext the page in which the given key must be localized 
     * @param key the message key to be looked up 
     * @param args the arguments for parametric replacement 
     * @param basename the resource bundle base name 
     * 
     * @return the localized message corresponding to the given key 
     */ 
    public static String getLocalizedMessage(PageContext pageContext, 
                                             String key, 
                                             Object[] args, 
                                             String basename) {
	ResourceBundle bundle = null;
	String message = MessageSupport.UNDEFINED_KEY + key
	    + MessageSupport.UNDEFINED_KEY;

	if (basename != null)
	    bundle = BundleSupport.getBundle(pageContext, basename);
	else
	    bundle = BundleSupport.getDefaultBundle(
                pageContext, BundleSupport.DEFAULT_BASENAME);

	if (bundle != null) {
	    try {
		message = bundle.getString(key);
		if (args != null) {
		    MessageFormat formatter = new MessageFormat("");
		    formatter.setLocale(bundle.getLocale());
		    formatter.applyPattern(message);
		    message = formatter.format(args);
		}
	    } catch (MissingResourceException mre) {
	    }
	}

	return message;
    }
}

