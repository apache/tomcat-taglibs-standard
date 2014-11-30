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
package org.apache.taglibs.standard.tag.common.xml;

import java.io.InputStream;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.DocumentBuilder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.taglibs.standard.util.XmlUtil;
import org.w3c.dom.Document;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 */
public class ExprSupportTest {

    private static Document test;

    private ExprSupport tag;
    private PageContext pageContext;
    private JspWriter out;

    @BeforeClass
    public static void loadXml() throws Exception {
        DocumentBuilder db = XmlUtil.newDocumentBuilder();
        InputStream is = ExprSupportTest.class.getResourceAsStream("test.xml");
        try {
            test = db.parse(is);
        } finally {
            is.close();
        }
    }

    @Before
    public void setup() {
        out = createMock(JspWriter.class);
        pageContext = createMock(PageContext.class);
        expect(pageContext.getOut()).andStubReturn(out);
        tag = new ExprSupport() {
        };
        tag.setPageContext(pageContext);
    }

    @Test
    public void testStringLiteral() throws Exception {
        tag.setSelect("\"Hello\"");
        out.write("Hello", 0, 5);
        replay(pageContext, out);
        tag.doStartTag();
        verify(pageContext, out);
    }

    @Test
    public void testBooleanVariable() throws Exception {
        tag.setSelect("$a");
        expect(pageContext.findAttribute("a")).andReturn(Boolean.TRUE);
        out.write("true", 0, 4);
        replay(pageContext, out);
        tag.doStartTag();
        verify(pageContext, out);
    }

    @Test
    public void testNumberVariable() throws Exception {
        tag.setSelect("$a");
        expect(pageContext.findAttribute("a")).andReturn(12345.678);
        out.write("12345.678", 0, 9);
        replay(pageContext, out);
        tag.doStartTag();
        verify(pageContext, out);
    }

    @Test
    public void testElementFromDom() throws Exception {
        tag.setSelect("$doc/root/b");
        expect(pageContext.findAttribute("doc")).andReturn(test);
        out.write("Hello", 0, 5);
        replay(pageContext, out);
        tag.doStartTag();
        verify(pageContext, out);
    }
}
