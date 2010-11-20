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
package org.apache.taglibs.standard.functions;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.jsp.JspTagException;

import org.apache.taglibs.standard.resources.Resources;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.taglibs.standard.functions.Functions.endsWith;
import static org.apache.taglibs.standard.functions.Functions.escapeXml;
import static org.apache.taglibs.standard.functions.Functions.join;
import static org.apache.taglibs.standard.functions.Functions.length;
import static org.apache.taglibs.standard.functions.Functions.replace;
import static org.apache.taglibs.standard.functions.Functions.split;
import static org.apache.taglibs.standard.functions.Functions.substring;
import static org.apache.taglibs.standard.functions.Functions.substringAfter;
import static org.apache.taglibs.standard.functions.Functions.substringBefore;

/**
 */
public class FunctionsTest {

    @Test
    public void testEndsWith() {
        Assert.assertTrue(endsWith("00", "0")); // verify bug 50057 was fixed
    }

    @Test
    public void testSubstring() {
        Assert.assertEquals("el", substring("Hello", 1, 3));
        Assert.assertEquals("", substring("Hello", 10, 0));
        Assert.assertEquals("He", substring("Hello", -1, 2));
        Assert.assertEquals("Hello", substring("Hello", -4, -1));
        Assert.assertEquals("ello", substring("Hello", 1, -1));
        Assert.assertEquals("ello", substring("Hello", 1, 10));
        Assert.assertEquals("", substring("Hello", 3, 1));
        Assert.assertEquals("", substring("Hello", 10, 6));
        Assert.assertEquals("Hello", substring("Hello", -1, -4));
    }

    @Test
    public void testSubstringAfter() {
        Assert.assertEquals("lo", substringAfter("Hello", "el"));
        Assert.assertEquals("", substringAfter("", "el"));
        Assert.assertEquals("Hello", substringAfter("Hello", ""));
        Assert.assertEquals("", substringAfter("", "lx"));
        Assert.assertEquals("lo All", substringAfter("Hello All", "l"));
    }

    @Test
    public void testSubstringBefore() {
        Assert.assertEquals("H", substringBefore("Hello", "el"));
        Assert.assertEquals("", substringBefore("", "el"));
        Assert.assertEquals("", substringBefore("Hello", ""));
        Assert.assertEquals("", substringBefore("", "lx"));
        Assert.assertEquals("He", substringBefore("Hello All", "l"));
    }

    @Test
    public void testReplace() {
        Assert.assertEquals("Hxxlo", replace("Hello", "el", "xx"));
        Assert.assertEquals("Hexxxxo", replace("Hello", "l", "xx"));
        Assert.assertEquals("", replace("", "l", "xx"));
        Assert.assertEquals("Heo", replace("Hello", "l", ""));
        Assert.assertEquals("Hello", replace("Hello", "", "xx"));
        Assert.assertEquals("Hellllo", replace("Hello", "l", "ll"));
        Assert.assertEquals("Hello", replace("Hello", "x", "ll"));
    }

    @Test
    public void testSplit() {
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, split("a:b:c", ":"));
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, split("a:b/c", ":/"));
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, split("a:b/c", "/:"));
        Assert.assertArrayEquals(new String[]{"a", "b"}, split("a:b:", ":"));
        Assert.assertArrayEquals(new String[]{"a:b:c"}, split("a:b:c", "x"));
        Assert.assertArrayEquals(new String[]{""}, split("", ""));
        Assert.assertArrayEquals(new String[]{""}, split("", ":"));
        Assert.assertArrayEquals(new String[]{"Hello"}, split("Hello", ""));
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("a:b:c", join(new String[]{"a", "b", "c"}, ":"));
        Assert.assertEquals("abc", join(new String[]{"a", "b", "c"}, ""));
        Assert.assertEquals("axxbxxc", join(new String[]{"a", "b", "c"}, "xx"));
        Assert.assertEquals("", join(null, ""));
        Assert.assertEquals("", join(new String[]{}, ":"));
        Assert.assertEquals("a:null:c", join(new String[]{"a", null, "c"}, ":"));
        Assert.assertEquals("a", join(new String[]{"a"}, ":"));
        Assert.assertEquals("null", join(new String[]{null}, ":"));
    }

    @Test
    public void testLength() throws Exception {
        Assert.assertEquals(0, length(null));
        Assert.assertEquals(0, length(""));
        Assert.assertEquals(3, length(new int[]{1, 2, 3}));
        Assert.assertEquals(3, length(Arrays.asList(1, 2, 3)));
        Assert.assertEquals(3, length(Arrays.asList(1, 2, 3).iterator()));
        Assert.assertEquals(3, length(Collections.enumeration(Arrays.asList(1, 2, 3))));
        Assert.assertEquals(1, length(Collections.singletonMap("Hello", "World")));
        try {
            length(3);
            Assert.fail();
        } catch (JspTagException e) {
            Assert.assertEquals(Resources.getMessage("PARAM_BAD_VALUE"), e.getMessage());
        }
    }

    @Test
    public void testEscapeXML() {
        Assert.assertEquals("Hello", escapeXml("Hello"));
        Assert.assertEquals("&lt;Hello msg=&#034;world&#034;/&gt;", escapeXml("<Hello msg=\"world\"/>"));
        Assert.assertEquals("&lt;Hello msg=&#039;world&#039;/&gt;", escapeXml("<Hello msg='world'/>"));
        Assert.assertEquals("cats &amp; dogs", escapeXml("cats & dogs"));
    }
}
