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
package org.apache.taglibs.standard.tag.common.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class TestSetSupport {
    private static String VALUE = "Hello";
    private static final String VAR = "x";

    private PageContext pageContext;
    private ELContext elContext;
    private VariableMapper vm;
    private SetSupport tag;

    @Before
    public void setup() {
        pageContext = createMock(PageContext.class);
        elContext = createMock(ELContext.class);
        vm = createMock(VariableMapper.class);

        expect(pageContext.getELContext()).andStubReturn(elContext);
        expect(elContext.getVariableMapper()).andStubReturn(vm);

        tag = new SetSupport();
        tag.setPageContext(pageContext);
    }

    /**
     * Verify Bug 49526 when there is no existing variable mapping.
     *
     * @throws JspException if there's an error
     */
    @Test
    public void test49526WhenNotMapped() throws JspException {
        tag.setVar(VAR);
        tag.value = VALUE;

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.setAttribute(VAR, VALUE, PageContext.PAGE_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    /**
     * Verify Bug 49526 when there is an existing variable mapping.
     *
     * @throws JspException if there's an error
     */
    @Test
    public void test49526WhenAlreadyMapped() throws JspException {
        tag.setVar(VAR);
        tag.value = VALUE;

        // verify mapper is checked and the mapped variable removed
        ValueExpression ve = createMock(ValueExpression.class);
        expect(vm.resolveVariable(VAR)).andReturn(ve);
        expect(vm.setVariable(VAR, null)).andReturn(ve);
        pageContext.setAttribute(VAR, VALUE, PageContext.PAGE_SCOPE);
        replay(pageContext, elContext, vm, ve);
        tag.doEndTag();
        verify(pageContext, elContext, vm, ve);
    }

    /**
     * Verify Bug 49526 when we are not setting into the page context.
     *
     * @throws JspException if there's an error
     */
    @Test
    public void test49526WhenNotUsingPageContext() throws JspException {
        tag.setVar(VAR);
        tag.value = VALUE;
        tag.setScope("request");

        // verify mapper is not checked
        pageContext.setAttribute(VAR, VALUE, PageContext.REQUEST_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testResultFromValueAttribute() {
        tag.valueSpecified = true;
        tag.value = VALUE;
        Assert.assertSame(VALUE, tag.getResult());
    }

    @Test
    public void testResultFromNullValueAttribute() {
        tag.valueSpecified = true;
        tag.value = null;
        Assert.assertNull(tag.getResult());
    }

    @Test
    public void testResultFromBodyContent() {
        tag.valueSpecified = false;
        BodyContent bodyContent = createMock(BodyContent.class);
        expect(bodyContent.getString()).andStubReturn("  Hello  ");
        replay(bodyContent);
        tag.setBodyContent(bodyContent);
        Assert.assertEquals(VALUE, tag.getResult());
    }

    @Test
    public void testResultFromNullBodyContent() {
        tag.valueSpecified = false;
        tag.setBodyContent(null);
        Assert.assertEquals("", tag.getResult());
    }

    @Test
    public void testResultFromEmptyBodyContent() {
        tag.valueSpecified = false;
        BodyContent bodyContent = createMock(BodyContent.class);
        expect(bodyContent.getString()).andStubReturn(null);
        Assert.assertEquals("", tag.getResult());
    }
}
