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

package org.apache.taglibs.standard.lang.jstl.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import junit.framework.TestCase;
import org.apache.taglibs.standard.lang.jstl.Evaluator;
import org.apache.taglibs.standard.lang.jstl.test.beans.Factory;

/**
 * Tests for EL Evaluation based on previous golden files.
 *
 * @author Nathan Abramson - Art Technology Group
 */

public class EvaluationTest extends TestCase {

    private PageContext context;
    private Evaluator e;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = createTestContext();
        e = new Evaluator();
    }

    public void testBasicLiterals() throws JspException {
        assertEquals(1, e.evaluate("test", "${1}", Integer.TYPE, null, context));
        assertEquals(-12, e.evaluate("test", "${-12}", Integer.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${true}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${false}", Boolean.TYPE, null, context));
        assertEquals("", e.evaluate("test", "${null}", String.class, null, context));
        assertEquals(4.2, e.evaluate("test", "${4.2}", Double.TYPE, null, context));
        assertEquals(-21.3f, e.evaluate("test", "${-21.3}", Float.TYPE, null, context));
        assertEquals(4.0f, e.evaluate("test", "${4.}", Float.TYPE, null, context));
        assertEquals(0.21f, e.evaluate("test", "${.21}", Float.TYPE, null, context));
        assertEquals(0.3, e.evaluate("test", "${3e-1}", Double.TYPE, null, context));
        assertEquals(0.2222222222, e.evaluate("test", "${.2222222222}", Double.TYPE, null, context));
    }

    public void testBasicRelationalsBetweenLiterals() throws JspException {
        assertEquals(true, e.evaluate("test", "${1 < 2}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${1 > 2}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${1 >= 2}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${1 <= 2}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${1 == 2}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${1 != 2}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${3 >= 3}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${3 <= 3}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${3 == 3}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${3 < 3}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${3 > 3}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${3 != 3}", Boolean.TYPE, null, context));
    }

    public void testBasicRelationalsBetweenBooleans() throws JspException {
        // next two tests pass on Java5 or later as Boolean now implements Comparable
        assertEquals(true, e.evaluate("test", "${false < true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${false > true}", Object.class, null, context));

        assertEquals(true, e.evaluate("test", "${true >= true}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${true <= true}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${true == true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${true != true}", Object.class, null, context));
    }

    public void testLookingUpObjectsInScopes() throws JspException {
        assertEquals("page-scoped1", e.evaluate("test", "${pageScope.val1a}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${requestScope.val1a}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${sessionScope.val1a}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${applicationScope.val1a}", String.class, null, context));
        assertEquals("page-scoped1", e.evaluate("test", "${val1a}", String.class, null, context));

        assertEquals("", e.evaluate("test", "${pageScope.val1b}", String.class, null, context));
        assertEquals("request-scoped1", e.evaluate("test", "${requestScope.val1b}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${sessionScope.val1b}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${applicationScope.val1b}", String.class, null, context));
        assertEquals("request-scoped1", e.evaluate("test", "${val1b}", String.class, null, context));

        assertEquals("", e.evaluate("test", "${pageScope.val1c}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${requestScope.val1c}", String.class, null, context));
        assertEquals("session-scoped1", e.evaluate("test", "${sessionScope.val1c}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${applicationScope.val1c}", String.class, null, context));
        assertEquals("session-scoped1", e.evaluate("test", "${val1c}", String.class, null, context));

        assertEquals("", e.evaluate("test", "${pageScope.val1d}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${requestScope.val1d}", String.class, null, context));
        assertEquals("", e.evaluate("test", "${sessionScope.val1d}", String.class, null, context));
        assertEquals("app-scoped1", e.evaluate("test", "${applicationScope.val1d}", String.class, null, context));
        assertEquals("app-scoped1", e.evaluate("test", "${val1d}", String.class, null, context));
    }

    public void testAccessingProperties() throws JspException {
        assertEquals(4, e.evaluate("test", "${bean1a.int1}", Integer.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.boolean1}", Boolean.TYPE, null, context));
        assertEquals("hello", e.evaluate("test", "${bean1a.string1}", String.class, null, context));
        assertEquals(-224, e.evaluate("test", "${bean1a.bean1.int2}", Integer.TYPE, null, context));
        assertEquals("bean3's string", e.evaluate("test", "${bean1a.bean1.bean2.string2}", String.class, null, context));
    }

    public void testEntireConversionMatrix() throws JspException {
        assertEquals((byte) 12, e.evaluate("test", "${bean1a.byte1}", Byte.TYPE, null, context));
        assertEquals('\14', e.evaluate("test", "${bean1a.byte1}", Character.TYPE, null, context));
        assertEquals((short) 12, e.evaluate("test", "${bean1a.byte1}", Short.TYPE, null, context));
        assertEquals(12, e.evaluate("test", "${bean1a.byte1}", Integer.TYPE, null, context));
        assertEquals(12l, e.evaluate("test", "${bean1a.byte1}", Long.TYPE, null, context));
        assertEquals(12.0f, e.evaluate("test", "${bean1a.byte1}", Float.TYPE, null, context));
        assertEquals(12.0, e.evaluate("test", "${bean1a.byte1}", Double.TYPE, null, context));

        assertEquals((byte) 98, e.evaluate("test", "${bean1a.char1}", Byte.TYPE, null, context));
        assertEquals('b', e.evaluate("test", "${bean1a.char1}", Character.TYPE, null, context));
        assertEquals((short) 98, e.evaluate("test", "${bean1a.char1}", Short.TYPE, null, context));
        assertEquals(98, e.evaluate("test", "${bean1a.char1}", Integer.TYPE, null, context));
        assertEquals(98l, e.evaluate("test", "${bean1a.char1}", Long.TYPE, null, context));
        assertEquals(98.0f, e.evaluate("test", "${bean1a.char1}", Float.TYPE, null, context));
        assertEquals(98.0, e.evaluate("test", "${bean1a.char1}", Double.TYPE, null, context));

        assertEquals((byte) -124, e.evaluate("test", "${bean1a.short1}", Byte.TYPE, null, context));
        assertEquals((char) -124, e.evaluate("test", "${bean1a.short1}", Character.TYPE, null, context));
        assertEquals((short) -124, e.evaluate("test", "${bean1a.short1}", Short.TYPE, null, context));
        assertEquals(-124, e.evaluate("test", "${bean1a.short1}", Integer.TYPE, null, context));
        assertEquals(-124l, e.evaluate("test", "${bean1a.short1}", Long.TYPE, null, context));
        assertEquals(-124.0f, e.evaluate("test", "${bean1a.short1}", Float.TYPE, null, context));
        assertEquals(-124.0, e.evaluate("test", "${bean1a.short1}", Double.TYPE, null, context));

        assertEquals((byte) 4, e.evaluate("test", "${bean1a.int1}", Byte.TYPE, null, context));
        assertEquals((char) 4, e.evaluate("test", "${bean1a.int1}", Character.TYPE, null, context));
        assertEquals((short) 4, e.evaluate("test", "${bean1a.int1}", Short.TYPE, null, context));
        assertEquals(4, e.evaluate("test", "${bean1a.int1}", Integer.TYPE, null, context));
        assertEquals(4l, e.evaluate("test", "${bean1a.int1}", Long.TYPE, null, context));
        assertEquals(4.0f, e.evaluate("test", "${bean1a.int1}", Float.TYPE, null, context));
        assertEquals(4.0, e.evaluate("test", "${bean1a.int1}", Double.TYPE, null, context));

        assertEquals((byte) -41, e.evaluate("test", "${bean1a.long1}", Byte.TYPE, null, context));
        assertEquals((char) 222423, e.evaluate("test", "${bean1a.long1}", Character.TYPE, null, context));
        assertEquals((short) 25815, e.evaluate("test", "${bean1a.long1}", Short.TYPE, null, context));
        assertEquals(222423, e.evaluate("test", "${bean1a.long1}", Integer.TYPE, null, context));
        assertEquals(222423l, e.evaluate("test", "${bean1a.long1}", Long.TYPE, null, context));
        assertEquals(222423.0f, e.evaluate("test", "${bean1a.long1}", Float.TYPE, null, context));
        assertEquals(222423.0, e.evaluate("test", "${bean1a.long1}", Double.TYPE, null, context));

        assertEquals((byte) 12, e.evaluate("test", "${bean1a.float1}", Byte.TYPE, null, context));
        assertEquals((char) 12, e.evaluate("test", "${bean1a.float1}", Character.TYPE, null, context));
        assertEquals((short) 12, e.evaluate("test", "${bean1a.float1}", Short.TYPE, null, context));
        assertEquals(12, e.evaluate("test", "${bean1a.float1}", Integer.TYPE, null, context));
        assertEquals(12l, e.evaluate("test", "${bean1a.float1}", Long.TYPE, null, context));
        assertEquals(12.4f, e.evaluate("test", "${bean1a.float1}", Float.TYPE, null, context));
        assertEquals(12.399999618530273, e.evaluate("test", "${bean1a.float1}", Double.TYPE, null, context));

        assertEquals((byte) 89, e.evaluate("test", "${bean1a.double1}", Byte.TYPE, null, context));
        assertEquals((char) 89, e.evaluate("test", "${bean1a.double1}", Character.TYPE, null, context));
        assertEquals((short) 89, e.evaluate("test", "${bean1a.double1}", Short.TYPE, null, context));
        assertEquals(89, e.evaluate("test", "${bean1a.double1}", Integer.TYPE, null, context));
        assertEquals(89l, e.evaluate("test", "${bean1a.double1}", Long.TYPE, null, context));
        assertEquals(89.224f, e.evaluate("test", "${bean1a.double1}", Float.TYPE, null, context));
        assertEquals(89.224, e.evaluate("test", "${bean1a.double1}", Double.TYPE, null, context));
    }

    public void testEntireRelationalComparisonPromotionMatrix() throws JspException {
        assertEquals(false, e.evaluate("test", "${bean1a.byte1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.byte1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.byte1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.byte1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.byte1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.byte1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.byte1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.char1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.char1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.short1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.short1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(true, e.evaluate("test", "${bean1a.int1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.int1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.int1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.int1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.int1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.int1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.int1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.long1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(false, e.evaluate("test", "${bean1a.float1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.float1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.float1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.float1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.float1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.float1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.float1 < bean1a.double1}", Boolean.TYPE, null, context));

        assertEquals(false, e.evaluate("test", "${bean1a.double1 < bean1a.byte1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.double1 < bean1a.char1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.double1 < bean1a.short1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.double1 < bean1a.int1}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.double1 < bean1a.long1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.double1 < bean1a.float1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.double1 < bean1a.double1}", Boolean.TYPE, null, context));
    }

    public void testOtherRelationalComparisonRules() throws JspException {
        assertEquals(true, e.evaluate("test", "${null == null}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${noSuchAttribute == noSuchAttribute}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${noSuchAttribute == null}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${null == noSuchAttribute}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a == null}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${null == bean1a}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a == bean1a}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a > \"hello\"}", Boolean.TYPE, null, context));
        assertJspException("${bean1a < 14}");
        assertEquals(false, e.evaluate("test", "${bean1a == \"hello\"}", Boolean.TYPE, null, context));
    }

    public void testStringComparisons() throws JspException {
        assertEquals(true, e.evaluate("test", "${bean1a.string1 == \"hello\"}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.string1 != \"hello\"}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${bean1a.string1 == \"goodbye\"}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.string1 != \"goodbye\"}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${bean1a.string1 > \"goodbye\"}", Boolean.TYPE, null, context));
        assertEquals(true, e.evaluate("test", "${\"hello\" == bean1a.string1}", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${\"goodbye\" > bean1a.string1}", Boolean.TYPE, null, context));
    }

    public void testErrorsInPropertyTraversal() throws JspException {
        assertEquals(null, e.evaluate("test", "${noSuchAttribute.abc}", Object.class, null, context));
        assertEquals(null, e.evaluate("test", "${bean1a.bean2.byte1}", Object.class, null, context));
        assertJspException("${bean1a.noProperty}");
        assertJspException("${bean1a.noGetter}");
        assertJspException("${bean1a.errorInGetter}");
        assertEquals(null, e.evaluate("test", "${bean1a.bean2.string2}", Object.class, null, context));
    }

    public void testAccessingPublicPropertiesFromPrivateClasses() throws JspException {
        assertEquals("got the value", e.evaluate("test", "${pbean1.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean2.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean3.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean4.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean5.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean6.value}", Object.class, null, context));
        assertEquals("got the value", e.evaluate("test", "${pbean7.value}", Object.class, null, context));
    }

    public void testLiteralConversions() throws JspException {
        assertEquals(true, e.evaluate("test", "true", Boolean.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "false", Boolean.TYPE, null, context));
        assertEquals((byte) 12, e.evaluate("test", "12", Byte.TYPE, null, context));
        assertEquals('1', e.evaluate("test", "12", Character.TYPE, null, context));
        assertEquals((short) 12, e.evaluate("test", "12", Short.TYPE, null, context));
        assertEquals(12, e.evaluate("test", "12", Integer.TYPE, null, context));
        assertEquals(12l, e.evaluate("test", "12", Long.TYPE, null, context));
        assertEquals(12.0f, e.evaluate("test", "12", Float.TYPE, null, context));
        assertEquals(12.0, e.evaluate("test", "12", Double.TYPE, null, context));

        // conversion using property editor
        Object o = e.evaluate("test", "hello", Bean2.class, null, context);
        assertTrue(o instanceof Bean2);
        assertEquals("hello", ((Bean2) o).getValue());

        try {
            e.evaluate("test", "badvalue", Bean2.class, null, context);
            fail();
        } catch (JspException e) {
            // OK
        }

        try {
            e.evaluate("test", "hello", Bean1.class, null, context);
            fail();
        } catch (JspException e) {
            // OK
        }
    }

    public void testNullValuesBumpedUpToConstants() throws JspException {
        assertEquals(false, e.evaluate("test", "${null}", Boolean.TYPE, null, context));
        assertEquals((byte) 0, e.evaluate("test", "${null}", Byte.TYPE, null, context));
        assertEquals((short) 0, e.evaluate("test", "${null}", Short.TYPE, null, context));
        assertEquals((char) 0, e.evaluate("test", "${null}", Character.TYPE, null, context));
        assertEquals(0, e.evaluate("test", "${null}", Integer.TYPE, null, context));
        assertEquals(0l, e.evaluate("test", "${null}", Long.TYPE, null, context));
        assertEquals(0.0f, e.evaluate("test", "${null}", Float.TYPE, null, context));
        assertEquals(0.0, e.evaluate("test", "${null}", Double.TYPE, null, context));
        assertEquals(false, e.evaluate("test", "${null}", Boolean.class, null, context));
        assertEquals((byte) 0, e.evaluate("test", "${null}", Byte.class, null, context));
        assertEquals((short) 0, e.evaluate("test", "${null}", Short.class, null, context));
        assertEquals((char) 0, e.evaluate("test", "${null}", Character.class, null, context));
        assertEquals(0, e.evaluate("test", "${null}", Integer.class, null, context));
        assertEquals(0l, e.evaluate("test", "${null}", Long.class, null, context));
        assertEquals(0.0f, e.evaluate("test", "${null}", Float.class, null, context));
        assertEquals(0.0, e.evaluate("test", "${null}", Double.class, null, context));
    }

    public void testReservedWordsAsIdentifiers() throws JspException {
        assertJspException("${and}");
        assertJspException("${or}");
        assertJspException("${not}");
        assertJspException("${eq}");
        assertJspException("${ne}");
        assertJspException("${lt}");
        assertJspException("${gt}");
        assertJspException("${le}");
        assertJspException("${ge}");
        assertEquals(null, e.evaluate("test", "${instanceof}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${false}", Object.class, null, context));
        assertEquals(null, e.evaluate("test", "${null}", Object.class, null, context));
    }

    public void testReservedWordsAsPropertyNames() throws JspException {
        assertJspException("${bean1a.and}");
        assertJspException("${bean1a.or}");
        assertJspException("${bean1a.not}");
        assertJspException("${bean1a.eq}");
        assertJspException("${bean1a.ne}");
        assertJspException("${bean1a.lt}");
        assertJspException("${bean1a.gt}");
        assertJspException("${bean1a.le}");
        assertJspException("${bean1a.ge}");
        assertJspException("${bean1a.instanceof}");
        assertJspException("${bean1a.page}");
        assertJspException("${bean1a.request}");
        assertJspException("${bean1a.session}");
        assertJspException("${bean1a.application}");
        assertJspException("${bean1a.true}");
        assertJspException("${bean1a.false}");
        assertJspException("${bean1a.null}");
    }

    public void testArithmetic() throws JspException {
        assertEquals(8l, e.evaluate("test", "${3+5}", Object.class, null, context));
        assertEquals(-2l, e.evaluate("test", "${3-5}", Object.class, null, context));
        assertEquals(0.6, e.evaluate("test", "${3/5}", Object.class, null, context));
        assertEquals(15l, e.evaluate("test", "${3*5}", Object.class, null, context));
        assertEquals(15.0, e.evaluate("test", "${3*5.0}", Object.class, null, context));
        assertEquals(15.0, e.evaluate("test", "${3.0*5}", Object.class, null, context));
        assertEquals(15.0, e.evaluate("test", "${3.0*5.0}", Object.class, null, context));
        assertEquals(4l, e.evaluate("test", "${225 % 17}", Object.class, null, context));
        assertEquals(24l, e.evaluate("test", "${ 1 + 2 + 3 * 5 + 6}", Object.class, null, context));
        assertEquals(32l, e.evaluate("test", "${ 1 + (2 + 3) * 5 + 6}", Object.class, null, context));
    }

    public void testLogicalOperators() throws JspException {
        assertEquals(true, e.evaluate("test", "${ true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ not true}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ not false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ not not true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ not not false}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ true and false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ true and true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ false and true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ false and false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ true or false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ true or true}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ false or true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ false or false}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ false or false or false or true and false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ false or false or false or true and false or true}", Object.class, null, context));
    }

    public void testIndexedAccessOperator() throws JspException {
        // test as equivalent to property accessor
        assertEquals(89.224, e.evaluate("test", "${ bean1a[\"double1\"] }", Object.class, null, context));
        assertEquals(Double.class, e.evaluate("test", "${ bean1a[\"double1\"].class }", Object.class, null, context));

        // test as array accessor
        assertEquals(null, e.evaluate("test", "${ bean1a.stringArray1[-1]}", Object.class, null, context));
        assertEquals("string1", e.evaluate("test", "${ bean1a.stringArray1[0]}", Object.class, null, context));
        assertEquals("string2", e.evaluate("test", "${ bean1a.stringArray1[1]}", Object.class, null, context));
        assertEquals("string3", e.evaluate("test", "${ bean1a.stringArray1[2]}", Object.class, null, context));
        assertEquals("string4", e.evaluate("test", "${ bean1a.stringArray1[3]}", Object.class, null, context));
        assertEquals(null, e.evaluate("test", "${ bean1a.stringArray1[4]}", Object.class, null, context));

        // test as list accessor
        assertEquals(14, e.evaluate("test", "${ bean1a.list1 [0]}", Object.class, null, context));
        assertEquals("another value", e.evaluate("test", "${ bean1a.list1 [1]}", Object.class, null, context));
        assertEquals("string3", e.evaluate("test", "${ bean1a.list1 [2][2]}", Object.class, null, context));

        // test as indexed property accessor
        assertJspException("${ bean1a.indexed1[-1]}");
        assertJspException("${ bean1a.indexed1[0]}");
        assertJspException("${ bean1a.indexed1[1]}");
        assertJspException("${ bean1a.indexed1[2]}");
        assertJspException("${ bean1a.indexed1[3]}");
        assertJspException("${ bean1a.indexed1[4]}");

        // test as map accessor
        assertEquals("value1", e.evaluate("test", "${ bean1a.map1.key1 }", Object.class, null, context));
        assertEquals("value1", e.evaluate("test", "${ bean1a.map1 [\"key1\"] }", Object.class, null, context));
        assertEquals("value3", e.evaluate("test", "${ bean1a.map1 [14] }", Object.class, null, context));
        assertEquals("value3", e.evaluate("test", "${ bean1a.map1 [2 * 7] }", Object.class, null, context));
        assertEquals(14, e.evaluate("test", "${ bean1a.map1.recurse.list1[0] }", Object.class, null, context));
    }

    public void testStringConcatenation() {
        assertJspException("${ \"a\" + \"bcd\" }");
        assertJspException("${ \"a\" + (4*3) }");
        assertJspException("${ bean1a.map1 [\"key\" + (5-4)] }");
    }

    public void testStringComparisons2() throws JspException {
        assertEquals(true, e.evaluate("test", "${ \"30\" < \"4\" }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 30 < \"4\" }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 30 > \"4\" }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ \"0004\" == \"4\" }", Object.class, null, context));
    }

    public void testRelationalComparisonWithAlternateSymbols() throws JspException {
        assertEquals(false, e.evaluate("test", "${ 4 eq 3}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 ne 3}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 eq 4}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 4 ne 4}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 4 lt 3}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 gt 3}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 4 le 3}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 ge 3}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 le 4}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 4 ge 4}", Object.class, null, context));
    }

    public void testExpressionsOnLeftSideOfValueSuffix() throws JspException {
        assertEquals(Long.class, e.evaluate("test", "${(3).class}", Object.class, null, context));
        assertEquals("value1", e.evaluate("test", "${(bean1a.map1)[\"key1\"]}", Object.class, null, context));
    }

    public void testStringBooleanLogicalOperators() throws JspException {
        assertEquals(false, e.evaluate("test", "${'true' and false}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${'true' or true}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${false and 'true'}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${false or 'true'}", Object.class, null, context));
    }

    public void testEmptyOperator() throws JspException {
        assertEquals(false, e.evaluate("test", "${ empty \"A\"}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty \"\" }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty false}", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty 0}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ not empty 0}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ not empty empty 0}", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty emptyTests.emptyArray }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty emptyTests.nonemptyArray }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty emptyTests.emptyList }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty emptyTests.nonemptyList }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty emptyTests.emptyMap }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty emptyTests.nonemptyMap }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ empty emptyTests.emptySet }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ empty emptyTests.nonemptySet }", Object.class, null, context));
    }

    public void testStringArithmetic() throws JspException {
        assertEquals(2.0, e.evaluate("test", "${ \"6\" / \"3\" }", Object.class, null, context));
        assertEquals(7l, e.evaluate("test", "${ 3 + \"4\" }", Object.class, null, context));
        assertEquals(7l, e.evaluate("test", "${ \"4\" + 3 }", Object.class, null, context));
        assertEquals(7.5, e.evaluate("test", "${ 3 + \"4.5\" }", Object.class, null, context));
        assertEquals(7.5, e.evaluate("test", "${ \"4.5\" + 3 }", Object.class, null, context));
        assertEquals(9.0, e.evaluate("test", "${ 3.0 + 6.0}", Object.class, null, context));
        assertEquals(1.915590913E9, e.evaluate("test", "${ 31121.0 * 61553.0 }", Object.class, null, context));
        assertEquals(1915590913l, e.evaluate("test", "${ 31121 * 61553 }", Object.class, null, context));
        assertEquals(9220838762064379904l, e.evaluate("test", "${ 65536 * 65536 * 65536 * 32759 }", Object.class, null, context));
        assertEquals(4l, e.evaluate("test", "${ 9220838762064379904 - 9220838762064379900 }", Object.class, null, context));
    }

    public void testRelationalOperatorsInvolvingNull() throws JspException {
        assertEquals(true, e.evaluate("test", "${ null == null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null != null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null > null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null < null }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ null >= null }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ null <= null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null == 3 }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ null != 3 }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null > 3 }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null < 3 }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null >= 3 }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null <= 3 }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 3 == null }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ 3 != null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 3 > null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 3 < null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 3 >= null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ 3 <= null }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ null == \"\" }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ null != \"\" }", Object.class, null, context));
        assertEquals(false, e.evaluate("test", "${ \"\" == null }", Object.class, null, context));
        assertEquals(true, e.evaluate("test", "${ \"\" != null }", Object.class, null, context));
    }

    public void testArithmeticOperatorsInvolvingStrings() throws JspException {
        assertEquals(7L, e.evaluate("test", "${ 4 + 3 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4.0 + 3 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4 + 3.0 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4.0 + 3.0 }", Object.class, null, context));
        assertEquals(7l, e.evaluate("test", "${ \"4\" + 3 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4.0\" + 3 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4\" + 3.0 }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4.0\" + 3.0 }", Object.class, null, context));
        assertEquals(7l, e.evaluate("test", "${ 4 + \"3\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4.0 + \"3\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4 + \"3.0\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ 4.0 + \"3.0\" }", Object.class, null, context));
        assertEquals(7l, e.evaluate("test", "${ \"4\" + \"3\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4.0\" + \"3\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4\" + \"3.0\" }", Object.class, null, context));
        assertEquals(7.0, e.evaluate("test", "${ \"4.0\" + \"3.0\" }", Object.class, null, context));

        assertEquals(1L, e.evaluate("test", "${ 4 - 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 - 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4 - 3.0 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 - 3.0 }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ \"4\" - 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" - 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4\" - 3.0 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" - 3.0 }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ 4 - \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 - \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4 - \"3.0\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 - \"3.0\" }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ \"4\" - \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" - \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4\" - \"3.0\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" - \"3.0\" }", Object.class, null, context));

        assertEquals(12L, e.evaluate("test", "${ 4 * 3 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4.0 * 3 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4 * 3.0 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4.0 * 3.0 }", Object.class, null, context));
        assertEquals(12l, e.evaluate("test", "${ \"4\" * 3 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4.0\" * 3 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4\" * 3.0 }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4.0\" * 3.0 }", Object.class, null, context));
        assertEquals(12l, e.evaluate("test", "${ 4 * \"3\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4.0 * \"3\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4 * \"3.0\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ 4.0 * \"3.0\" }", Object.class, null, context));
        assertEquals(12l, e.evaluate("test", "${ \"4\" * \"3\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4.0\" * \"3\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4\" * \"3.0\" }", Object.class, null, context));
        assertEquals(12.0, e.evaluate("test", "${ \"4.0\" * \"3.0\" }", Object.class, null, context));

        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4 / 3 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4.0 / 3 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4 / 3.0 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4.0 / 3.0 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4\" / 3 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4.0\" / 3 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4\" / 3.0 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4.0\" / 3.0 }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4 / \"3\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4.0 / \"3\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4 / \"3.0\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ 4.0 / \"3.0\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4\" / \"3\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4.0\" / \"3\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4\" / \"3.0\" }", Object.class, null, context));
        assertEquals(1.3333333333333333, e.evaluate("test", "${ \"4.0\" / \"3.0\" }", Object.class, null, context));

        assertEquals(1L, e.evaluate("test", "${ 4 % 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 % 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4 % 3.0 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 % 3.0 }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ \"4\" % 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" % 3 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4\" % 3.0 }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" % 3.0 }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ 4 % \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 % \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4 % \"3.0\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ 4.0 % \"3.0\" }", Object.class, null, context));
        assertEquals(1l, e.evaluate("test", "${ \"4\" % \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" % \"3\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4\" % \"3.0\" }", Object.class, null, context));
        assertEquals(1.0, e.evaluate("test", "${ \"4.0\" % \"3.0\" }", Object.class, null, context));

        assertEquals(4.0, e.evaluate("test", "${ \"8\" / \"2\" }", Object.class, null, context));
        assertEquals(403.0, e.evaluate("test", "${ \"4e2\" + \"3\" }", Object.class, null, context));
        assertEquals(304.0, e.evaluate("test", "${ \"4\" + \"3e2\" }", Object.class, null, context));
        assertEquals(700.0, e.evaluate("test", "${ \"4e2\" + \"3e2\" }", Object.class, null, context));
    }

    public void testUnaryMinusOperatorInvolvingStrings() throws JspException {
        assertEquals(-3l, e.evaluate("test", "${ -3 }", Object.class, null, context));
        assertEquals(-3.0, e.evaluate("test", "${ -3.0 }", Object.class, null, context));
        assertEquals(-3L, e.evaluate("test", "${ -\"3\" }", Object.class, null, context));
        assertEquals(-3.0, e.evaluate("test", "${ -\"3.0\" }", Object.class, null, context));
        assertEquals(-300.0, e.evaluate("test", "${ -\"3e2\" }", Object.class, null, context));
    }

    private void assertJspException(String expr) {
        try {
            e.evaluate("test", expr, Object.class, null, context);
            fail();
        } catch (JspException e1) {
            // OK
        }
    }

    /**
     * Creates and returns the test PageContext that will be used for
     * the tests.
     */
    static PageContext createTestContext() {
        PageContext ret = new PageContextImpl();

        // Create some basic values for lookups
        ret.setAttribute("val1a", "page-scoped1", PageContext.PAGE_SCOPE);
        ret.setAttribute("val1b", "request-scoped1", PageContext.REQUEST_SCOPE);
        ret.setAttribute("val1c", "session-scoped1", PageContext.SESSION_SCOPE);
        ret.setAttribute("val1d", "app-scoped1", PageContext.APPLICATION_SCOPE);

        // Create a bean
        {
            Bean1 b1 = new Bean1();
            b1.setBoolean1(true);
            b1.setByte1((byte) 12);
            b1.setShort1((short) -124);
            b1.setChar1('b');
            b1.setInt1(4);
            b1.setLong1(222423);
            b1.setFloat1((float) 12.4);
            b1.setDouble1(89.224);
            b1.setString1("hello");
            b1.setStringArray1(new String[]{
                    "string1",
                    "string2",
                    "string3",
                    "string4"
            });
            {
                List l = new ArrayList();
                l.add(new Integer(14));
                l.add("another value");
                l.add(b1.getStringArray1());
                b1.setList1(l);
            }
            {
                Map m = new HashMap();
                m.put("key1", "value1");
                m.put(new Integer(14), "value2");
                m.put(new Long(14), "value3");
                m.put("recurse", b1);
                b1.setMap1(m);
            }
            ret.setAttribute("bean1a", b1);

            Bean1 b2 = new Bean1();
            b2.setInt2(new Integer(-224));
            b2.setString2("bean2's string");
            b1.setBean1(b2);

            Bean1 b3 = new Bean1();
            b3.setDouble1(1422.332);
            b3.setString2("bean3's string");
            b2.setBean2(b3);
        }

        // Create the public/private beans
        {
            ret.setAttribute("pbean1", Factory.createBean1());
            ret.setAttribute("pbean2", Factory.createBean2());
            ret.setAttribute("pbean3", Factory.createBean3());
            ret.setAttribute("pbean4", Factory.createBean4());
            ret.setAttribute("pbean5", Factory.createBean5());
            ret.setAttribute("pbean6", Factory.createBean6());
            ret.setAttribute("pbean7", Factory.createBean7());
        }

        // Create the empty tests
        {
            Map m = new HashMap();
            m.put("emptyArray", new Object[0]);
            m.put("nonemptyArray", new Object[]{"abc"});
            m.put("emptyList", new ArrayList());
            {
                List l = new ArrayList();
                l.add("hello");
                m.put("nonemptyList", l);
            }
            m.put("emptyMap", new HashMap());
            {
                Map m2 = new HashMap();
                m2.put("a", "a");
                m.put("nonemptyMap", m2);
            }
            m.put("emptySet", new HashSet());
            {
                Set s = new HashSet();
                s.add("hello");
                m.put("nonemptySet", s);
            }
            ret.setAttribute("emptyTests", m);
        }

        return ret;
    }
}
