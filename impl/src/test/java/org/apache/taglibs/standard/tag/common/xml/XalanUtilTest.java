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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 */
public class XalanUtilTest {

    private Tag tag;
    private PageContext pageContext;

    @Before
    public void setup() {
        tag = createMock(Tag.class);
        pageContext = createMock(PageContext.class);
    }

    @Test
    public void testEmptyContext() {
        expect(tag.getParent()).andReturn(null);
        replay(pageContext, tag);
        XPathContext context = XalanUtil.getContext(tag, pageContext);
        // verify current node is an empty Document
        int node = context.getCurrentNode();
        Node doc = context.getDTM(node).getNode(node);
        Assert.assertTrue(doc instanceof Document);
        Assert.assertNull(doc.getFirstChild());
        verify(pageContext, tag);
    }

    @Test
    public void testContextSupportsMultipleVariables() throws Exception {
        expect(tag.getParent()).andReturn(null);
        expect(pageContext.findAttribute("a")).andReturn("Hello");
        expect(pageContext.getAttribute("b", PageContext.REQUEST_SCOPE)).andReturn("World");
        replay(pageContext, tag);
        XPathContext context = XalanUtil.getContext(tag, pageContext);
        XPath xpath = new XPath("concat($a, ' ', $requestScope:b)", null, null, XPath.SELECT);
        Assert.assertEquals("Hello World", xpath.execute(context, context.getCurrentNode(), null).str());
        verify(pageContext, tag);
    }
}
