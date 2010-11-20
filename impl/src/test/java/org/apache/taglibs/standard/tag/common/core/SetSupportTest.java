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

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.taglibs.standard.resources.Resources;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class SetSupportTest {
    private static String PROPERTY = "property";
    private static String VALUE = "Hello";
    private static final String VAR = "x";

    private PageContext pageContext;
    private ELContext elContext;
    private VariableMapper vm;
    private Bean bean;

    private SetSupport tag;

    @Before
    public void setup() {
        pageContext = createMock(PageContext.class);
        elContext = createMock(ELContext.class);
        vm = createMock(VariableMapper.class);

        expect(pageContext.getELContext()).andStubReturn(elContext);
        expect(pageContext.getServletContext()).andStubReturn(null);
        expect(elContext.getVariableMapper()).andStubReturn(vm);

        bean = new Bean();


        ExpressionFactory expressionFactory = createMock(ExpressionFactory.class);
        JspApplicationContext applicationContext = createMock(JspApplicationContext.class);
        JspFactory jspFactory = createMock(JspFactory.class);
        expect(expressionFactory.coerceToType(VALUE, String.class)).andStubReturn(VALUE);
        expect(expressionFactory.coerceToType(null, String.class)).andStubReturn(null);
        expect(applicationContext.getExpressionFactory()).andStubReturn(expressionFactory);
        expect(jspFactory.getJspApplicationContext(null)).andStubReturn(applicationContext);
        replay(jspFactory, applicationContext, expressionFactory);
        JspFactory.setDefaultFactory(jspFactory);
    }

    @After
    public void teardown() {
        JspFactory.setDefaultFactory(null);
    }

    @Test
    public void testSyntax1WithNoScope() throws JspException {
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.setAttribute(VAR, VALUE, PageContext.PAGE_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax1WithNullScope() throws JspException {
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);
        tag.setScope(null);

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.setAttribute(VAR, VALUE, PageContext.PAGE_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax1WithPageScope() throws JspException {
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);
        tag.setScope("page");

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.setAttribute(VAR, VALUE, PageContext.PAGE_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax1WithNonPageScope() throws JspException {
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);
        tag.setScope("request");

        // verify mapper is not checked
        pageContext.setAttribute(VAR, VALUE, PageContext.REQUEST_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax1WithNullValueAndNoScope() throws JspException {
        tag = new MockSetSupport(null);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.removeAttribute(VAR);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax1WithNullValueAndNonPageScope() throws JspException {
        tag = new MockSetSupport(null);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);
        tag.setScope("request");

        // verify mapper is checked but that no action is taken
        expect(vm.resolveVariable(VAR)).andReturn(null);
        pageContext.removeAttribute(VAR, PageContext.REQUEST_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testSyntax3WithMap() throws JspException {
        @SuppressWarnings("unchecked")
        Map<String, Object> target = createMock(Map.class);
        tag = new MockSetSupport(VALUE, target, PROPERTY);
        tag.setPageContext(pageContext);

        expect(target.put(PROPERTY, VALUE)).andStubReturn(null);
        replay(target);
        tag.doEndTag();
        verify(target);
    }

    @Test
    public void testSyntax3WithMapWhenPropertyIsNull() throws JspException {
        @SuppressWarnings("unchecked")
        Map<String, Object> target = createMock(Map.class);
        tag = new MockSetSupport(VALUE, target, null);
        tag.setPageContext(pageContext);

        expect(target.put(null, VALUE)).andStubReturn(null);
        replay(target);
        tag.doEndTag();
        verify(target);
    }

    @Test
    public void testSyntax3WithMapWhenValueIsNull() throws JspException {
        @SuppressWarnings("unchecked")
        Map<String, Object> target = createMock(Map.class);
        tag = new MockSetSupport(null, target, PROPERTY);
        tag.setPageContext(pageContext);

        expect(target.remove(PROPERTY)).andStubReturn(null);
        replay(target);
        tag.doEndTag();
        verify(target);
    }

    @Test
    public void testSyntax3WithBean() throws JspException {
        tag = new MockSetSupport(VALUE, bean, PROPERTY);
        tag.setPageContext(pageContext);

        tag.doEndTag();
        Assert.assertEquals(VALUE, bean.getProperty());
    }

    @Test
    public void testSyntax3WithBeanAndNullValue() throws JspException {
        tag = new MockSetSupport(null, bean, PROPERTY);
        tag.setPageContext(pageContext);

        tag.doEndTag();
        Assert.assertNull(bean.getProperty());
    }

    @Test
    public void testSyntax3WithBeanAndUndefinedProperty() throws JspException {
        tag = new MockSetSupport(VALUE, bean, "undefined");
        tag.setPageContext(pageContext);

        try {
            tag.doEndTag();
        } catch (JspTagException e) {
            Assert.assertEquals(e.getMessage(), Resources.getMessage("SET_INVALID_PROPERTY", "undefined"));
        }
    }

    @Test
    public void testSyntax3WithBeanAndReadOnlyProperty() throws JspException {
        tag = new MockSetSupport(VALUE, bean, "readOnly");
        tag.setPageContext(pageContext);

        try {
            tag.doEndTag();
        } catch (JspException e) {
            Assert.assertEquals(e.getMessage(), Resources.getMessage("SET_NO_SETTER_METHOD", "readOnly"));
        }
    }

    @Test
    public void testSyntax3WhenTargetIsNull() throws JspException {
        tag = new MockSetSupport(VALUE, null, PROPERTY);
        tag.setPageContext(pageContext);

        try {
            tag.doEndTag();
            Assert.fail();
        } catch (JspTagException e) {
            Assert.assertEquals(e.getMessage(), Resources.getMessage("SET_INVALID_TARGET"));
        }
    }

    /**
     * Verify Bug 49526 when there is no existing variable mapping.
     *
     * @throws JspException if there's an error
     */
    @Test
    public void test49526WhenNotMapped() throws JspException {
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);

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
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);

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
        tag = new MockSetSupport(VALUE);
        tag.setPageContext(pageContext);
        tag.setVar(VAR);
        tag.setScope("request");

        // verify mapper is not checked
        pageContext.setAttribute(VAR, VALUE, PageContext.REQUEST_SCOPE);
        replay(pageContext, elContext, vm);
        tag.doEndTag();
        verify(pageContext, elContext, vm);
    }

    @Test
    public void testResultFromValueAttribute() throws JspException {
        tag = new MockSetSupport(VALUE);
        Assert.assertSame(VALUE, tag.getResult());
    }

    @Test
    public void testResultFromNullValueAttribute() throws JspException {
        tag = new MockSetSupport(null);
        Assert.assertNull(tag.getResult());
    }

    @Test
    public void testResultFromBodyContent() throws JspException {
        tag = new MockSetSupport();
        BodyContent bodyContent = createMock(BodyContent.class);
        expect(bodyContent.getString()).andStubReturn("  Hello  ");
        replay(bodyContent);
        tag.setBodyContent(bodyContent);
        Assert.assertEquals(VALUE, tag.getResult());
    }

    @Test
    public void testResultFromNullBodyContent() throws JspException {
        tag = new MockSetSupport();
        tag.setBodyContent(null);
        Assert.assertEquals(tag.getResult(), "");
    }

    @Test
    public void testResultFromEmptyBodyContent() throws JspException {
        tag = new MockSetSupport();
        BodyContent bodyContent = createMock(BodyContent.class);
        expect(bodyContent.getString()).andStubReturn(null);
        Assert.assertEquals("", tag.getResult());
    }

    public static class MockSetSupport extends SetSupport {
        private final boolean valueSpecified;
        private final Object value;
        private final Object target;
        private final String property;

        public MockSetSupport() {
            this.value = null;
            this.valueSpecified = false;
            this.target = null;
            this.property = null;
        }

        public MockSetSupport(Object value, Object target, String property) {
            this.value = value;
            this.valueSpecified = true;
            this.target = target;
            this.property = property;
        }

        public MockSetSupport(Object value) {
            this.value = value;
            this.valueSpecified = true;
            this.target = null;
            this.property = null;
        }

        @Override
        protected boolean isValueSpecified() {
            return valueSpecified;
        }

        @Override
        protected Object evalValue() {
            return value;
        }

        @Override
        protected Object evalTarget() {
            return target;
        }

        @Override
        protected String evalProperty() {
            return property;
        }
    }

    public static class Bean {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public boolean isReadOnly() {
            return true;
        }
    }
}
