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
package org.apache.taglibs.standard.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.jsp.JspWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.taglibs.standard.util.EscapeXML.emit;
import static org.apache.taglibs.standard.util.EscapeXML.escape;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 */
public class EscapeXMLTest {
    private JspWriter writer;

    @Before
    public void setup() {
        writer = createMock(JspWriter.class);
    }

    @Test
    public void testEscapeString() {
        Assert.assertEquals("Hello", escape("Hello"));
        Assert.assertEquals("&lt;Hello msg=&#034;world&#034;/&gt;", escape("<Hello msg=\"world\"/>"));
        Assert.assertEquals("&lt;Hello msg=&#039;world&#039;/&gt;", escape("<Hello msg='world'/>"));
        Assert.assertEquals("cats &amp; dogs", escape("cats & dogs"));
    }

    @Test
    public void testEmitInteger() throws IOException {
        writer.write("1234");
        replay(writer);
        emit(1234, false, writer);
        verify(writer);
    }

    @Test
    public void testEmitNull() throws IOException {
        writer.write("null");
        replay(writer);
        emit((Object) null, false, writer);
        verify(writer);
    }

    @Test
    public void testEmitStringEscaped() throws IOException {
        String s = "cats & dogs";
        writer.write(s, 0, 5);
        writer.write("&amp;");
        writer.write(s, 6, 5);
        replay(writer);
        emit(s, true, writer);
        verify(writer);
    }

    @Test
    public void testEmitStringUnescaped() throws IOException {
        String s = "cats & dogs";
        writer.write(s);
        replay(writer);
        emit(s, false, writer);
        verify(writer);
    }

    @Test
    public void testEmitEntireString() throws IOException {
        writer.write("Hello World", 0, 11);
        replay(writer);
        emit("Hello World", writer);
        verify(writer);
    }

    @Test
    public void testEmitEscapedStringMiddle() throws IOException {
        String s = "cats & dogs";
        writer.write(s, 0, 5);
        writer.write("&amp;");
        writer.write(s, 6, 5);
        replay(writer);
        emit(s, writer);
        verify(writer);
    }

    @Test
    public void testEmitEscapedStringBeginning() throws IOException {
        String s = "& dogs";
        writer.write("&amp;");
        writer.write(s, 1, 5);
        replay(writer);
        emit(s, writer);
        verify(writer);
    }

    @Test
    public void testEmitEscapedStringEnd() throws IOException {
        String s = "cats &";
        writer.write(s, 0, 5);
        writer.write("&amp;");
        replay(writer);
        emit(s, writer);
        verify(writer);
    }

    @Test
    public void testEmitEscapedStringAdjacent() throws IOException {
        String s = "'cats'&\"dogs\"";
        writer.write("&#039;");
        writer.write(s, 1, 4);
        writer.write("&#039;");
        writer.write("&amp;");
        writer.write("&#034;");
        writer.write(s, 8, 4);
        writer.write("&#034;");
        replay(writer);
        emit(s, writer);
        verify(writer);
    }

    @Test
    public void testEmitEmptyString() throws IOException {
        replay(writer);
        emit("", writer);
        verify(writer);
    }

    @Test
    public void testEmitChars() throws IOException {
        char[] chars = "Hello World".toCharArray();
        writer.write(chars, 2, 5);
        replay(writer);
        emit(chars, 2, 5, writer);
        verify(writer);
    }

    @Test
    public void testEmitEscapedChars() throws IOException {
        char[] chars = "'cats'&\"dogs\"".toCharArray();
        writer.write("&#039;");
        writer.write(chars, 1, 4);
        writer.write("&#039;");
        writer.write("&amp;");
        writer.write("&#034;");
        writer.write(chars, 8, 4);
        writer.write("&#034;");
        replay(writer);
        emit(chars, 0, chars.length, writer);
        verify(writer);
    }

    @Test
    public void testEmitReaderUnescaped() throws IOException {
        Reader reader = new StringReader("'cats'&\"dogs\"");
        expect(writer.getBufferSize()).andStubReturn(0);
        writer.write(isA(char[].class), eq(0), eq(13));
        replay(writer);
        emit(reader, false, writer);
        verify(writer);
    }

    @Test
    public void testEmitReaderUnbuffered() throws IOException {
        Reader reader = new StringReader("'cats'&\"dogs\"");
        expect(writer.getBufferSize()).andStubReturn(0);
        writer.write("&#039;");
        writer.write(isA(char[].class), eq(1), eq(4));
        writer.write("&#039;");
        writer.write("&amp;");
        writer.write("&#034;");
        writer.write(isA(char[].class), eq(8), eq(4));
        writer.write("&#034;");
        replay(writer);
        emit(reader, true, writer);
        verify(writer);
    }

    @Test
    public void testEmitReaderBufferWrap() throws IOException {
        Reader reader = new StringReader("'cats'&\"dogs\"");
        expect(writer.getBufferSize()).andStubReturn(2);
        writer.write("&#039;");
        writer.write(isA(char[].class), eq(1), eq(1)); // 'c'
        writer.write(isA(char[].class), eq(0), eq(2)); // 'at'
        writer.write(isA(char[].class), eq(0), eq(1)); // 's'
        writer.write("&#039;");
        writer.write("&amp;");
        writer.write("&#034;");
        writer.write(isA(char[].class), eq(0), eq(2)); // 'do'
        writer.write(isA(char[].class), eq(0), eq(2)); // 'gs'
        writer.write("&#034;");
        replay(writer);
        emit(reader, true, writer);
        verify(writer);
    }
}
