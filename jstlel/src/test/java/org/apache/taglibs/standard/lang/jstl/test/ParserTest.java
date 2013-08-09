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

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.jsp.JspException;

import junit.framework.TestCase;
import org.apache.taglibs.standard.lang.jstl.Evaluator;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>This runs a series of tests specifically for the parser.  It
 * parses various expressions and prints out the canonical
 * representation of those parsed expressions.
 * <p>The expressions are stored in an input text file, with one line
 * per expression.  Blank lines and lines that start with # are
 * ignored.  The results are written to an output file (blank lines
 * and # lines are included in the output file).  The output file may
 * be compared against an existing output file to do regression
 * testing.
 *
 * @author Nathan Abramson - Art Technology Group
 */
public class ParserTest extends TestCase {
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void testParser() throws IOException {
        try {
            System.setProperty("javax.servlet.jsp.functions.allowed", "true");
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("parserTests.txt"), UTF8));
            CharArrayWriter writer = new CharArrayWriter();
            PrintWriter out = new PrintWriter(writer);

            runTests(in, out);
            out.close();
            in.close();

            LineNumberReader expected = new LineNumberReader(new InputStreamReader(getClass().getResourceAsStream("parserTestsOutput.txt"), UTF8));
            LineNumberReader actual = new LineNumberReader(new CharArrayReader(writer.toCharArray()));

            Assert.assertFalse(isDifferentStreams(actual, expected));
            actual.close();
            expected.close();
        } finally {
            System.clearProperty("javax.servlet.jsp.functions.allowed");
        }
    }

    /**
     * Runs the tests, reading expressions from pIn and writing the
     * results to pOut.
     */
    public static void runTests(BufferedReader pIn, PrintWriter pOut)
            throws IOException {
        while (true) {
            String str = pIn.readLine();
            if (str == null) {
                break;
            }
            if (str.startsWith("#") ||
                    "".equals(str.trim())) {
                pOut.println(str);
            } else {
                pOut.println("Attribute value: " + str);
                try {
                    String result = Evaluator.parseAndRender(str);
                    pOut.println("Parses to: " + result);
                }
                catch (JspException exc) {
                    pOut.println("Causes an error: " + exc.getMessage());
                }
            }
        }

    }

    /**
     * Performs a line-by-line comparison of the two files, returning
     * true if the files are different, false if not.
     */
    public static boolean isDifferentStreams(LineNumberReader actual,
                                             LineNumberReader expected)
            throws IOException {
        while (true) {
            String str1 = actual.readLine();
            String str2 = expected.readLine();
            if (str1 == null &&
                    str2 == null) {
                return false;
            } else if (str1 == null ||
                    str2 == null) {
                return true;
            } else {
                if (!str1.equals(str2)) {
                    System.out.println("Files differ at line " + actual.getLineNumber());
                    return true;
                }
            }
        }
    }

    @Test
    public void testUnicodeCharacter() throws JspException {
        Assert.assertEquals("\u1111", Evaluator.parseAndRender("\u1111"));
    }
}
