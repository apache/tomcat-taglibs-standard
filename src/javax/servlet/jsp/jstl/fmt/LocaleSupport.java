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

package javax.servlet.jsp.jstl.fmt;

import java.util.*;
import java.text.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.jstl.core.Config;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Class which exposes the locale-determination logic for resource bundles
 * through convenience methods.
 *
 * <p> This class may be useful to any tag handler implementation that needs
 * to produce localized messages. For example, this might be useful for 
 * exception messages that are intended directly for user consumption on an 
 * error page.
 *
 * @author Jan Luehe
 */

public class LocaleSupport {

    /** 
     * Retrieves the localized message corresponding to the given key.
     *
     * <p> The given key is looked up in the resource bundle of the default
     * I18N localization context, which is retrieved from the
     * <tt>javax.servlet.jsp.jstl.fmt.localizationContext</tt> configuration
     * setting.
     *
     * <p> If the configuration setting is empty, or the default I18N
     * localization context does not contain any resource bundle, or the given
     * key is undefined in its resource bundle, the string "???<key>???" is
     * returned, where "<key>" is replaced with the given key.
     * 
     * @param pageContext the page in which to get the localized message
     * corresponding to the given key  
     * @param key the message key
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
     * <p> The given key is looked up in the resource bundle with the given
     * base name.
     *
     * <p> If no resource bundle with the given base name exists, or the given
     * key is undefined in the resource bundle, the string "???<key>???" is
     * returned, where "<key>" is replaced with the given key.
     * 
     * @param pageContext the page in which to get the localized message
     * corresponding to the given key  
     * @param key the message key
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
     * Retrieves the localized message corresponding to the given key, and
     * performs parametric replacement using the arguments specified via
     * <tt>args</tt>.
     *
     * <p> See the specification of the <fmt:message> action for a description
     * of how parametric replacement is implemented.
     *
     * <p> The localized message is retrieved as in
     * {@link #getLocalizedMessage(javax.servlet.jsp.PageContext,java.lang.String) getLocalizedMessage(pageContext, key)}.
     *
     * @param pageContext the page in which to get the localized message
     * corresponding to the given key  
     * @param key the message key
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
     * Retrieves the localized message corresponding to the given key, and
     * performs parametric replacement using the arguments specified via
     * <tt>args</tt>.
     *
     * <p> See the specification of the <fmt:message> action for a description
     * of how parametric replacement is implemented.
     *
     * <p> The localized message is retrieved as in
     * {@link #getLocalizedMessage(javax.servlet.jsp.PageContext,java.lang.String, java.lang.String) getLocalizedMessage(pageContext, key, basename)}.
     * 
     * @param pageContext the page in which to get the localized message
     * corresponding to the given key  
     * @param key the message key
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

	if (basename != null) {
	    bundle = BundleSupport.getBundle(pageContext, basename);
	} else {
	    bundle = (ResourceBundle)
		Config.find(pageContext, Config.FMT_LOCALIZATIONCONTEXT);
	}
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

