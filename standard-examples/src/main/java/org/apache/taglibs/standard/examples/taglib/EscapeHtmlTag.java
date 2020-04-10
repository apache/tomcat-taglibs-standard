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

package org.apache.taglibs.standard.examples.taglib;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.examples.util.Util;

/**
 * <p>Tag handler for &lt;escapeHtml&gt;
 *
 * @author Pierre Delisle
 */
public class EscapeHtmlTag extends BodyTagSupport {

    //*********************************************************************
    // Instance variables

    private Object reader;
    private Object writer;

    //*********************************************************************
    // Constructors

    public EscapeHtmlTag() {
        super();
        init();
    }

    private void init() {
        reader = null;
        writer = null;
    }


    //*********************************************************************
    // Tag's properties

    /**
     * Tag's 'reader' attribute
     */
    public void setReader(Object reader) {
        this.reader = reader;
    }

    /**
     * Tag's 'writer' attribute
     */
    public void setWriter(Object writer) {
        this.writer = writer;
    }

    //*********************************************************************
    // TagSupport methods

    public int doEndTag() throws JspException {
        Reader in;
        Writer out;

        if (reader == null) {
            String bcs = getBodyContent().getString().trim();
            if (bcs == null || bcs.equals("")) {
                throw new JspTagException("In &lt;escapeHtml&gt;, 'reader' " +
                        "not specified and no non-whitespace content inside the tag.");
            }
            in = Util.castToReader(bcs);
        } else {
            in = Util.castToReader(reader);
        }

        if (writer == null) {
            out = pageContext.getOut();
        } else {
            out = Util.castToWriter(writer);
        }

        transform(in, out);
        return EVAL_PAGE;
    }

    /**
     * Releases any resources we may have (or inherit)
     */
    public void release() {
        super.release();
        init();
    }

    //*********************************************************************
    // Tag's scific behavior methods

    /**
     * Transform
     */
    public void transform(Reader reader, Writer writer)
            throws JspException {
        int c;
        try {
            writer.write("<pre>");
            while ((c = reader.read()) != -1) {
                if (c == '<') {
                    writer.write("&lt;");
                } else if (c == '>') {
                    writer.write("&gt;");
                } else {
                    writer.write(c);
                }
            }
            writer.write("</pre>");
        } catch (IOException ex) {
            throw new JspException("EscapeHtml: " +
                    "error copying chars", ex);
        }
    }
}
