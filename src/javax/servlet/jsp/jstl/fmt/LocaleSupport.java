/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package javax.servlet.jsp.jstl.fmt;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;

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
     * key is undefined in its resource bundle, the string "???&lt;key&gt;???" is
     * returned, where "&lt;key&gt;" is replaced with the given key.
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
     * key is undefined in the resource bundle, the string "???&lt;key&gt;???" is
     * returned, where "&lt;key&gt;" is replaced with the given key.
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
     * <p> See the specification of the &lt;fmt:message&gt; action for a description
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
     * <p> See the specification of the &lt;fmt:message&gt; action for a description
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
	LocalizationContext locCtxt = null;
	String message = MessageSupport.UNDEFINED_KEY + key
	    + MessageSupport.UNDEFINED_KEY;

	if (basename != null) {
	    locCtxt = BundleSupport.getLocalizationContext(pageContext, basename);
	} else {
	    locCtxt = BundleSupport.getLocalizationContext(pageContext);
	}

	if (locCtxt != null) {
	    ResourceBundle bundle = locCtxt.getResourceBundle();
	    if (bundle != null) {
		try {
		    message = bundle.getString(key);
		    if (args != null) {
			MessageFormat formatter = new MessageFormat("");
			if (locCtxt.getLocale() != null) {
			    formatter.setLocale(locCtxt.getLocale());
			}
			formatter.applyPattern(message);
			message = formatter.format(args);
		    }
		} catch (MissingResourceException mre) {
		}
	    }
	}

	return message;
    }
}

