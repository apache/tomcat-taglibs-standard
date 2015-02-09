/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.taglibs.standard.tag.common.fmt;

import java.util.Locale;

import org.apache.taglibs.standard.resources.Resources;

/**
 * Utility functions for working with Locales.
 *
 * This implementation sticks with the Locale parsing rules defined by the JSTL Specification.
 * When permitted, this should be updated to use the richer Locale parsing rules provided
 * in Java 7 and later.
 *
 */
public class LocaleUtil {
    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';

    /**
     * Handles Locales that can be passed to tags as instances of String or Locale.
     * If the parameter is an instance of Locale, it is simply returned.
     * If the parameter is a String and is not empty, then it is parsed to a Locale
     * using {@link LocaleUtil#parseLocale(String)}.
     * Otherwise null will be returned.
     *
     * @param stringOrLocale locale represented as an instance of Locale or as a String
     * @return the locale represented by the parameter, or null if the parameter is undefined
     */
    public static Locale parseLocaleAttributeValue(Object stringOrLocale) {
        if (stringOrLocale instanceof Locale) {
            return (Locale) stringOrLocale;
        } else if (stringOrLocale instanceof String) {
            String string = (String) stringOrLocale;
            if (string.length() == 0) {
                return null;
            } else {
                return parseLocale(string.trim());
            }
        } else {
            return null;
        }
    }

    /**
     * See parseLocale(String, String) for details.
     */
    public static Locale parseLocale(String locale) {
        return parseLocale(locale, null);
    }

    /**
     * Parses the given locale string into its language and (optionally)
     * country components, and returns the corresponding {@link Locale} object.
     *
     * @param locale  the locale string to parse; must not be null or empty
     * @param variant the variant
     * @return the specified Locale
     * @throws IllegalArgumentException if the given locale does not have a
     *                                  language component or has an empty country component
     */
    public static Locale parseLocale(String locale, String variant) {

        String language;
        String country;
        int index;

        if (((index = locale.indexOf(HYPHEN)) > -1) || ((index = locale.indexOf(UNDERSCORE)) > -1)) {
            if (index == 0) {
                throw new IllegalArgumentException(Resources.getMessage("LOCALE_NO_LANGUAGE"));
            } else if (index == locale.length() - 1) {
                throw new IllegalArgumentException(Resources.getMessage("LOCALE_EMPTY_COUNTRY"));
            }
            language = locale.substring(0, index);
            country = locale.substring(index + 1);
        } else {
            language = locale;
            country = "";
        }

        if (variant != null) {
            return new Locale(language, country, variant);
        } else {
            return new Locale(language, country);
        }
    }
}
