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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sun.security.krb5.internal.TGSRep;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 */
public class ForEachTagTest {
    private static Document test;
    private static DocumentBuilderFactory dbf;

    @BeforeClass
    public static void loadXml() throws Exception {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream is = ExprSupportTest.class.getResourceAsStream("test.xml");
        try {
            test = db.parse(is);
        } finally {
            is.close();
        }
    }

    private ForEachTag tag;
    private PageContext pageContext;

    @Before
    public void setup() {
        pageContext = createMock(PageContext.class);
        tag = new ForEachTag();
        tag.setPageContext(pageContext);
    }

    @Test
    public void testIteration() throws Exception {
        expect(pageContext.findAttribute("doc")).andStubReturn(test);
        tag.setSelect("$doc/root/a/num");
        replay(pageContext);
        tag.prepare();
        XPathContext context = tag.getContext();
        Assert.assertTrue(tag.hasNext());
        Node one = (Node) tag.next();
        Assert.assertEquals("one", one.getTextContent());
        Assert.assertTrue(tag.hasNext());
        Node two = (Node) tag.next();
        Assert.assertEquals("two", two.getTextContent());
        Assert.assertTrue(tag.hasNext());
        Node three = (Node) tag.next();
        Assert.assertEquals("three", three.getTextContent());
        Assert.assertFalse(tag.hasNext());
        tag.doFinally();
        Assert.assertTrue(context.getContextNodeListsStack().isEmpty());
        verify(pageContext);
    }

    @Test
    public void testIterationContext() throws Exception {
        expect(pageContext.findAttribute("doc")).andStubReturn(test);
        tag.setSelect("$doc/root/a/num");
        replay(pageContext);
        XPath dot = new XPath(".", null, null, XPath.SELECT);
        XPath position = new XPath("position()", null, null, XPath.SELECT);
        XPath last = new XPath("last()", null, null, XPath.SELECT);
        tag.prepare();
        XPathContext context = tag.getContext();
        tag.hasNext();
        tag.next();
        Assert.assertEquals("3", last.execute(context, context.getCurrentNode(), null).str());
        Assert.assertEquals("one", dot.execute(context, context.getCurrentNode(), null).str());
        Assert.assertEquals("1", position.execute(context, context.getCurrentNode(), null).str());
        tag.hasNext();
        tag.next();
        Assert.assertEquals("two", dot.execute(context, context.getCurrentNode(), null).str());
        Assert.assertEquals("2", position.execute(context, context.getCurrentNode(), null).str());
        tag.hasNext();
        tag.next();
        Assert.assertEquals("three", dot.execute(context, context.getCurrentNode(), null).str());
        Assert.assertEquals("3", position.execute(context, context.getCurrentNode(), null).str());
        verify(pageContext);
    }

    @Ignore
    @Test
    public void testIterationPerformance() throws Exception {
        // create a large document
        final int SIZE = 200000;
        DocumentBuilder db = dbf.newDocumentBuilder();
        test = db.newDocument();
        Element root = test.createElement("root");
        test.appendChild(root);
        for (int i = 0; i < SIZE; i++) {
            Element child = test.createElement("a");
            child.setTextContent(Integer.toString(i));
            root.appendChild(child);
        }

        XPath dot = new XPath(".", null, null, XPath.SELECT);
        expect(pageContext.findAttribute("doc")).andStubReturn(test);
        tag.setSelect("$doc/root/a");
        replay(pageContext);
        long time = -System.nanoTime();
        XObject result = null;
        if (tag.doStartTag() == IterationTag.EVAL_BODY_INCLUDE) {
            do {
//                XPathContext context = tag.getContext();
//                result = dot.execute(context, context.getCurrentNode(), null);
            } while (tag.doAfterBody() == IterationTag.EVAL_BODY_AGAIN);
            tag.doFinally();
        }
        time += System.nanoTime();
//        System.err.println("result = " + result.str());
        System.err.println("time = " + time/1000000 + "ms.");
    }
}
