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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import jakarta.servlet.jsp.JspException;

/**
 * <p>Utility class for examples webapp.
 *
 * @author Pierre Delisle
 */
public class Util {

    public static Writer castToWriter(Object obj) throws JspException {
        if (obj instanceof OutputStream) {
            return new OutputStreamWriter((OutputStream) obj);
        } else if (obj instanceof Writer) {
            return (Writer) obj;
            /*@@@
        } else if (obj instanceof String) {
            return new StringWriter();
             */
        }
        throw new JspException("Invalid type '" + obj.getClass().getName() +
                "' for castToWriter()");
    }

    public static Reader castToReader(Object obj) throws JspException {
        if (obj instanceof InputStream) {
            return new InputStreamReader((InputStream) obj);
        } else if (obj instanceof Reader) {
            return (Reader) obj;
        } else if (obj instanceof String) {
            return new StringReader((String) obj);
        }
        throw new JspException("Invalid type '" + obj.getClass().getName() +
                "' for castToReader()");
    }
}

