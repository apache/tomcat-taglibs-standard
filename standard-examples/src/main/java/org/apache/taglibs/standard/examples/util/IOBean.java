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

package org.apache.taglibs.standard.examples.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import jakarta.servlet.jsp.JspException;

/**
 * <p>String repository for Reader/Writer.
 *
 * @author Pierre Delisle
 */
public class IOBean {
    StringWriter stringWriter = null;
    String content = null;

    public Reader getReader() throws JspException {
        //p("getReader()");
        if (content == null) {
            if (stringWriter == null) {
                throw new JspException(
                        "content must first be added to the bean via the writer");
            }
            content = stringWriter.toString();
        }
        return new StringReader(content);
    }

    public Writer getWriter() {
        //p("getWriter()");
        content = null;
        stringWriter = new StringWriter();
        return stringWriter;
    }

    public void release() {
        stringWriter = null;
        content = null;
    }

    private void p(String s) {
        System.out.println("[IOBean] " + s);
    }
}
