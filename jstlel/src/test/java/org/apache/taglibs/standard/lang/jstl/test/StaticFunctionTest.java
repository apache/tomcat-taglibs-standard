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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.taglibs.standard.lang.jstl.Evaluator;

/**
 * <p>This class contains some test functions.</p>
 *
 * @author Shawn Bayern
 */
public class StaticFunctionTest extends TestCase {

    public void testFunctions() throws Exception {

        System.setProperty("javax.servlet.jsp.functions.allowed", "true");
        Map m = getSampleMethodMap();
        Evaluator e = new Evaluator();
        Object o;

        o = e.evaluate("", "4", Integer.class, null, null, m, "foo");
        assertEquals("4", o.toString());
        o = e.evaluate("", "${4}", Integer.class, null, null, m, "foo");
        assertEquals("4", o.toString());
        o = e.evaluate("", "${2+2}", Integer.class, null, null, m, "foo");
        assertEquals("4", o.toString());
        o = e.evaluate("", "${foo:add(2, 3)}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());
        o = e.evaluate("", "${foo:multiply(2, 3)}", Integer.class, null, null, m, "foo");
        assertEquals("6", o.toString());
        o = e.evaluate("", "${add(2, 3)}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());
        o = e.evaluate("", "${multiply(2, 3)}", Integer.class, null, null, m, "foo");
        assertEquals("6", o.toString());
        o = e.evaluate("", "${add(2, 3) + 5}", Integer.class, null, null, m, "foo");
        assertEquals("10", o.toString());


        o = e.evaluate("", "${getInt(getInteger(getInt(5)))}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());

        o = e.evaluate("", "${getInteger(getInt(getInteger(5)))}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());

        o = e.evaluate("", "${getInt(getInt(getInt(5)))}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());

        o = e.evaluate("", "${getInteger(getInteger(getInteger(5)))}", Integer.class, null, null, m, "foo");
        assertEquals("5", o.toString());
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int getInt(Integer i) {
        return i.intValue();
    }

    public static Integer getInteger(int i) {
        return new Integer(i);
    }

    public static Map getSampleMethodMap() throws Exception {
        Map m = new HashMap();
        Class c = StaticFunctionTest.class;
        m.put("foo:add", c.getMethod("add", new Class[]{Integer.TYPE, Integer.TYPE}));
        m.put("foo:multiply", c.getMethod("multiply", new Class[]{Integer.TYPE, Integer.TYPE}));
        m.put("foo:getInt", c.getMethod("getInt", new Class[]{Integer.class}));
        m.put("foo:getInteger", c.getMethod("getInteger", new Class[]{Integer.TYPE}));
        return m;
    }

}
