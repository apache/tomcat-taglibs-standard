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

import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Class representing an I18N localization context.
 *
 * <p> An I18N localization context has two components: a resource bundle and
 * the locale that led to the resource bundle match.
 *
 * <p> The resource bundle component is used by <fmt:message> for mapping
 * message keys to localized messages, and the locale component is used by the
 * <fmt:formatNumber>, <fmt:parseNumber>, <fmt:formatDate>, and
 * <fmt:parseDate> actions as their formatting or parsing locale, respectively.
 *
 * @author Jan Luehe
 */

public class LocalizationContext {

    private ResourceBundle bundle;
    private Locale locale;

    /**
     * Constructor.
     *
     * @param bundle The resource bundle, or null if no resource bundle was
     * found
     * @param locale The locale that led to the resource bundle match, or null
     * if no resource bundle was found or <tt>bundle</tt> is a locale-less
     * root resource bundle.
     */
    public LocalizationContext(ResourceBundle bundle, Locale locale) {
	this.bundle = bundle;
	this.locale = locale;
    }

    /** 
     * Gets the resource bundle of this I18N localization context.
     * 
     * @return The resource bundle of this I18N localization context, or null
     * if this I18N localization context does not contain any resource bundle.
     */ 
    public ResourceBundle getResourceBundle() {
	return bundle;
    }

    /** 
     * Gets the locale of this I18N localization context.
     *
     * @return The locale of this I18N localization context, or null if this
     * I18N localization context does not contain any resource bundle, or its
     * resource bundle is a (locale-less) root resource bundle.
     */ 
    public Locale getLocale() {
	return locale;
    }
}

