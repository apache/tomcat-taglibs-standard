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

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.taglibs.standard.util.XmlUtil;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObjectFactory;
import org.apache.xpath.objects.XString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 */
public class JSTLVariableStackTest {

    private JSTLVariableStack stack;
    private PageContext pageContext;
    private XPathContext xpathContext;
    private XString hello;

    @Before
    public void setup() {
        pageContext = createMock(PageContext.class);
        xpathContext = new XPathContext(false);
        hello = (XString) XObjectFactory.create("Hello");
        stack = new JSTLVariableStack(pageContext);

    }

    @Test
    public void testNoPrefix() throws TransformerException {
        expect(pageContext.findAttribute("foo")).andReturn("Hello");
        replay(pageContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName(null, "foo")));
        verify(pageContext);
    }

    @Test
    public void testParamPrefix() throws TransformerException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(pageContext.getRequest()).andReturn(request);
        expect(request.getParameter("foo")).andReturn("Hello");
        replay(pageContext, request);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("param", "foo")));
        verify(pageContext, request);
    }

    @Test
    public void testHeaderPrefix() throws TransformerException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(pageContext.getRequest()).andReturn(request);
        expect(request.getHeader("foo")).andReturn("Hello");
        replay(pageContext, request);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("header", "foo")));
        verify(pageContext, request);
    }

    @Test
    public void testCookiePrefix() throws TransformerException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(pageContext.getRequest()).andReturn(request);
        Cookie[] cookies = new Cookie[]{new Cookie("foo", "Hello")};
        expect(request.getCookies()).andReturn(cookies);
        replay(pageContext, request);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("cookie", "foo")));
        verify(pageContext, request);
    }

    @Test
    public void testInitParamPrefix() throws TransformerException {
        ServletContext servletContext = createMock(ServletContext.class);
        expect(pageContext.getServletContext()).andReturn(servletContext);
        expect(servletContext.getInitParameter("foo")).andReturn("Hello");
        replay(pageContext, servletContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("initParam", "foo")));
        verify(pageContext, servletContext);
    }

    @Test
    public void testPagePrefix() throws TransformerException {
        expect(pageContext.getAttribute("foo", PageContext.PAGE_SCOPE)).andReturn("Hello");
        replay(pageContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("pageScope", "foo")));
        verify(pageContext);
    }

    @Test
    public void testRequestPrefix() throws TransformerException {
        expect(pageContext.getAttribute("foo", PageContext.REQUEST_SCOPE)).andReturn("Hello");
        replay(pageContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("requestScope", "foo")));
        verify(pageContext);
    }

    @Test
    public void testSessionPrefix() throws TransformerException {
        expect(pageContext.getAttribute("foo", PageContext.SESSION_SCOPE)).andReturn("Hello");
        replay(pageContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("sessionScope", "foo")));
        verify(pageContext);
    }

    @Test
    public void testApplicationPrefix() throws TransformerException {
        expect(pageContext.getAttribute("foo", PageContext.APPLICATION_SCOPE)).andReturn("Hello");
        replay(pageContext);
        Assert.assertEquals(hello, stack.getVariableOrParam(xpathContext, new QName("applicationScope", "foo")));
        verify(pageContext);
    }

    @Test(expected = TransformerException.class)
    public void testValueNotFound() throws TransformerException {
        expect(pageContext.findAttribute("foo")).andReturn(null);
        replay(pageContext);
        stack.getVariableOrParam(xpathContext, new QName(null, "foo"));
        Assert.fail();
    }

    @Test
    public void verifyJavaToXPathTypeMapping() throws ParserConfigurationException {
        Assert.assertTrue(XObjectFactory.create(Boolean.TRUE, xpathContext) instanceof XBoolean);
        Assert.assertTrue(XObjectFactory.create(1234, xpathContext) instanceof XNumber);
        Assert.assertTrue(XObjectFactory.create("Hello", xpathContext) instanceof XString);

        Document d = XmlUtil.newEmptyDocument();
        Element root = d.createElement("root");
        XNodeSetForDOM xo = (XNodeSetForDOM) XObjectFactory.create(root, xpathContext);
        Assert.assertEquals(root, xo.object());

    }
}
