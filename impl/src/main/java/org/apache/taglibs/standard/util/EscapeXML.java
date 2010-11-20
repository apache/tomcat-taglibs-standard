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

import javax.servlet.jsp.JspWriter;

/**
 * Handles escaping of characters that could be interpreted as XML markup.
 * <p>The specification for <code>&lt;c:out&gt;</code> defines the following
 * character conversions to be applied:
 * <table rules="all" frame="border">
 * <thead align="center">
 * <tr><th>Character</th><th>Character Entity Code</th></tr>
 * </thead>
 * <tbody align="center">
 * <tr><td>&lt;</td><td>&amp;lt;</td></tr>
 * <tr><td>&gt;</td><td>&amp;gt;</td></tr>
 * <tr><td>&amp;</td><td>&amp;amp;</td></tr>
 * <tr><td>&#039;</td><td>&amp;#039;</td></tr>
 * <tr><td>&#034;</td><td>&amp;#034;</td></tr>
 * </tbody>
 * </table>
 */
public class EscapeXML {

    private static final String[] ESCAPES;

    static {
        int size = '>' + 1; // '>' is the largest escaped value
        ESCAPES = new String[size];
        ESCAPES['<'] = "&lt;";
        ESCAPES['>'] = "&gt;";
        ESCAPES['&'] = "&amp;";
        ESCAPES['\''] = "&#039;";
        ESCAPES['"'] = "&#034;";
    }

    private static String getEscape(char c) {
        if (c < ESCAPES.length) {
            return ESCAPES[c];
        } else {
            return null;
        }
    }

    /**
     * Escape a string.
     *
     * @param src the string to escape; must not be null
     * @return the escaped string
     */
    public static String escape(String src) {
        // first pass to determine the length of the buffer so we only allocate once
        int length = 0;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            String escape = getEscape(c);
            if (escape != null) {
                length += escape.length();
            } else {
                length += 1;
            }
        }

        // skip copy if no escaping is needed
        if (length == src.length()) {
            return src;
        }

        // second pass to build the escaped string
        StringBuilder buf = new StringBuilder(length);
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            String escape = getEscape(c);
            if (escape != null) {
                buf.append(escape);
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Emit the supplied object to the specified writer, escaping characters if needed.
     *
     * @param src       the object to write
     * @param escapeXml if true, escape unsafe characters before writing
     * @param out       the JspWriter to emit to
     * @throws IOException if there was a problem emitting the content
     */
    public static void emit(Object src, boolean escapeXml, JspWriter out) throws IOException {
        if (src instanceof Reader) {
            emit((Reader) src, escapeXml, out);
        } else {
            emit(String.valueOf(src), escapeXml, out);
        }
    }

    /**
     * Emit the supplied String to the specified writer, escaping characters if needed.
     *
     * @param src       the String to write
     * @param escapeXml if true, escape unsafe characters before writing
     * @param out       the JspWriter to emit to
     * @throws IOException if there was a problem emitting the content
     */
    public static void emit(String src, boolean escapeXml, JspWriter out) throws IOException {
        if (escapeXml) {
            emit(src, out);
        } else {
            out.write(src);
        }
    }

    /**
     * Emit escaped content into the specified JSPWriter.
     *
     * @param src the string to escape; must not be null
     * @param out the JspWriter to emit to
     * @throws IOException if there was a problem emitting the content
     */
    public static void emit(String src, JspWriter out) throws IOException {
        int end = src.length();
        int from = 0;
        for (int to = from; to < end; to++) {
            String escape = getEscape(src.charAt(to));
            if (escape != null) {
                if (to != from) {
                    out.write(src, from, to - from);
                }
                out.write(escape);
                from = to + 1;
            }
        }
        if (from != end) {
            out.write(src, from, end - from);
        }
    }

    /**
     * Copy the content of a Reader into the specified JSPWriter escaping characters if needed.
     *
     * @param src       the Reader to read from
     * @param escapeXml if true, escape characters
     * @param out       the JspWriter to emit to
     * @throws IOException if there was a problem emitting the content
     */
    public static void emit(Reader src, boolean escapeXml, JspWriter out) throws IOException {
        int bufferSize = out.getBufferSize();
        if (bufferSize == 0) {
            bufferSize = 4096;
        }
        char[] buffer = new char[bufferSize];
        int count;
        while ((count = src.read(buffer)) > 0) {
            if (escapeXml) {
                emit(buffer, 0, count, out);
            } else {
                out.write(buffer, 0, count);
            }
        }
    }

    /**
     * Emit escaped content into the specified JSPWriter.
     *
     * @param buffer characters to escape
     * @param from   start position in the buffer
     * @param count  number of characters to emit
     * @param out    the JspWriter to emit to
     * @throws IOException if there was a problem emitting the content
     */
    public static void emit(char[] buffer, int from, int count, JspWriter out) throws IOException {
        int end = from + count;
        for (int to = from; to < end; to++) {
            String escape = getEscape(buffer[to]);
            if (escape != null) {
                if (to != from) {
                    out.write(buffer, from, to - from);
                }
                out.write(escape);
                from = to + 1;
            }
        }
        if (from != end) {
            out.write(buffer, from, end - from);
        }
    }
}
