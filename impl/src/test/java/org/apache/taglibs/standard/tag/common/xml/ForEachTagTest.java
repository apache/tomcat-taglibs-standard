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
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.taglibs.standard.util.XmlUtil;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 */
public class ForEachTagTest {
    private static Document test;

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
        assertEquals("one", one.getTextContent());
        Assert.assertTrue(tag.hasNext());
        Node two = (Node) tag.next();
        assertEquals("two", two.getTextContent());
        Assert.assertTrue(tag.hasNext());
        Node three = (Node) tag.next();
        assertEquals("three", three.getTextContent());
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
        assertEquals("3", last.execute(context, context.getCurrentNode(), null).str());
        assertEquals("one", dot.execute(context, context.getCurrentNode(), null).str());
        assertEquals("1", position.execute(context, context.getCurrentNode(), null).str());
        tag.hasNext();
        tag.next();
        assertEquals("two", dot.execute(context, context.getCurrentNode(), null).str());
        assertEquals("2", position.execute(context, context.getCurrentNode(), null).str());
        tag.hasNext();
        tag.next();
        assertEquals("three", dot.execute(context, context.getCurrentNode(), null).str());
        assertEquals("3", position.execute(context, context.getCurrentNode(), null).str());
        verify(pageContext);
    }

    @Ignore
    @Test
    public void testIterationPerformance() throws Exception {
        // create a large document
        test = newBenchmarkDocument(200000);

        XPath dot = new XPath(".", null, null, XPath.SELECT);
        expect(pageContext.findAttribute("doc")).andStubReturn(test);
        tag.setSelect("$doc/root/a");
        replay(pageContext);
        long time = -System.nanoTime();
        XObject result = null;
        if (tag.doStartTag() == IterationTag.EVAL_BODY_INCLUDE) {
            do {
                XPathContext context = tag.getContext();
                result = dot.execute(context, context.getCurrentNode(), null);
            } while (tag.doAfterBody() == IterationTag.EVAL_BODY_AGAIN);
            tag.doFinally();
        }
        time += System.nanoTime();
        System.err.println("time = " + time / 1000000 + "ms.");
        assertEquals("199999", result.str());
    }

    @Ignore("Takes < 1s to run")
    @Test
    public void xalanPerformance() throws Exception{
        Document doc = newBenchmarkDocument(200000);
        expect(pageContext.findAttribute("doc")).andStubReturn(doc);
        replay(pageContext);

        XPath select = new XPath("$doc/root/a", null, null, XPath.SELECT);
        XPath dot = new XPath(".", null, null, XPath.SELECT);
        XObject result = null;

        long time = -System.nanoTime();
        XPathContext context = new XPathContext(false);
        context.setVarStack(new JSTLVariableStack(pageContext));
        int dtm = context.getDTMHandleFromNode(XmlUtil.newEmptyDocument());
        context.pushCurrentNodeAndExpression(dtm, dtm);

        // create an iterator over the returned nodes and push into the context
        XObject nodes = select.execute(context, context.getCurrentNode(), null);
        DTMIterator iterator = nodes.iter();
        context.pushContextNodeList(iterator);
        while (iterator.getCurrentPos() < iterator.getLength()) {
            int next = iterator.nextNode();
            context.pushCurrentNode(next);
            iterator.getDTM(next).getNode(next);

            result = dot.execute(context, context.getCurrentNode(), null);

            context.popCurrentNode();
        }
        time += System.nanoTime();
        System.err.println("time = " + time/1000000 + "ms.");
        assertEquals("199999", result.str());
    }

    @Ignore("Takes > 20s to run")
    @Test
    public void cachedAPIPerformance() throws Exception{
        Document doc = newBenchmarkDocument(200000);
        expect(pageContext.findAttribute("doc")).andStubReturn(doc);
        replay(pageContext);

        Node result = null;
        CachedXPathAPI api = new CachedXPathAPI();
        api.getXPathContext().setVarStack(new JSTLVariableStack(pageContext));

        long time = -System.nanoTime();
        NodeIterator iterator = api.selectNodeIterator(XmlUtil.newEmptyDocument(), "$doc/root/a");
        Node node = iterator.nextNode();
        while (node != null) {
            result = api.selectSingleNode(node, ".");
            node = iterator.nextNode();
        }
        time += System.nanoTime();
        System.err.println("time = " + time/1000000 + "ms.");
        assertEquals("199999", result.getTextContent());
    }

    @Ignore("Takes > 20s to run")
    @Test
    public void xpathPerformance() throws Exception {
        final Document doc = newBenchmarkDocument(200000);
        expect(pageContext.findAttribute("doc")).andStubReturn(doc);
        replay(pageContext);

        XPathVariableResolver resolver = new XPathVariableResolver() {
            public Object resolveVariable(QName variableName) {
                return doc;
            }
        };
        // XPathFactory factory = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI, "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", null);
        XPathFactory factory = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
        System.out.println("factory.getClass() = " + factory.getClass());
        factory.setXPathVariableResolver(resolver);
        XPathExpression select = factory.newXPath().compile("$doc/root/a");
        XPathExpression dot = factory.newXPath().compile(".");
        Node result = null;

        long time = -System.nanoTime();
        NodeList nodes = (NodeList) select.evaluate(XmlUtil.newEmptyDocument(), XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node context = nodes.item(i);
            result = (Node) dot.evaluate(context, XPathConstants.NODE);
        }
        time += System.nanoTime();
        System.err.println("time = " + time/1000000 + "ms.");
        assertEquals("199999", result.getTextContent());
    }

    private static Document newBenchmarkDocument(int size) {
        Document doc = XmlUtil.newEmptyDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        for (int i = 0; i < size; i++) {
            Element child = doc.createElement("a");
            child.setTextContent(Integer.toString(i));
            root.appendChild(child);
        }
        return doc;
    }
}
