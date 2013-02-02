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

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

public class LocaleUtilTest {

    private static final Locale DEFAULT = Locale.getDefault();

    @Test
    @Ignore("original causes NPE")
    public void testUsesDefaultIfNull() {
        Assert.assertEquals(DEFAULT, SetLocaleSupport.parseLocale(null, null));
    }

    @Test
    @Ignore("original results in IllegalArgumentException")
    public void testUsesDefaultIfEmpty() {
        Assert.assertEquals(DEFAULT, SetLocaleSupport.parseLocale("", null));
    }

    @Test
    public void testLocaleWithLanguage() {
        Assert.assertEquals(new Locale("fr"), SetLocaleSupport.parseLocale("fr", null));
    }

    @Test
    public void testLocaleUsingHyphen() {
        Assert.assertEquals(new Locale("en", "DE"), SetLocaleSupport.parseLocale("en-DE", null));
    }

    @Test
    public void testLocaleUsingUnderscore() {
        Assert.assertEquals(new Locale("en", "IE"), SetLocaleSupport.parseLocale("en_IE", null));
    }

    @Test
    public void testLocaleWithLanguageVariant() {
        Assert.assertEquals(new Locale("fr", "", "xxx"), SetLocaleSupport.parseLocale("fr", "xxx"));
    }

    @Test
    public void testLocaleWithLanguageCountryVariant() {
        Assert.assertEquals(new Locale("en", "GB", "cockney"), SetLocaleSupport.parseLocale("en-GB", "cockney"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyLanguageIsInvalid() {
        SetLocaleSupport.parseLocale("-GB", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCountryIsInvalid() {
        SetLocaleSupport.parseLocale("en-", null);
    }
}
