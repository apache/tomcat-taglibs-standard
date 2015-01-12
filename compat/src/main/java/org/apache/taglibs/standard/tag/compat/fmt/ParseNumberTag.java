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

package org.apache.taglibs.standard.tag.compat.fmt;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.fmt.LocaleUtil;
import org.apache.taglibs.standard.tag.common.fmt.ParseNumberSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <parseNumber>} using the container's EL implementation.
 */
public class ParseNumberTag extends ParseNumberSupport {

    private ValueExpression valueExpression;
    private ValueExpression typeExpression;
    private ValueExpression patternExpression;
    private ValueExpression parseLocaleExpression;
    private ValueExpression integerOnlyExpression;

    @Override
    public int doStartTag() throws JspException {
        value = ExpressionUtil.evaluate(valueExpression, pageContext);
        type = ExpressionUtil.evaluate(typeExpression, pageContext);
        pattern = ExpressionUtil.evaluate(patternExpression, pageContext);
        parseLocale = LocaleUtil.parseLocaleAttributeValue(
                ExpressionUtil.evaluate(parseLocaleExpression, pageContext));
        isIntegerOnly = ExpressionUtil.evaluate(integerOnlyExpression, pageContext, false);

        return super.doStartTag();
    }

    @Override
    public void release() {
        valueExpression = null;
        typeExpression = null;
        patternExpression = null;
        parseLocaleExpression = null;
        integerOnlyExpression = null;

        super.release();
    }

    public void setValue(String expression) {
        valueExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
        valueSpecified = true;
    }

    public void setType(String expression) {
        typeExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setPattern(String expression) {
        patternExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setParseLocale(String expression) {
        parseLocaleExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
    }

    public void setIntegerOnly(String expression) {
        integerOnlyExpression = ExpressionUtil.createValueExpression(pageContext, expression, Boolean.class);
        integerOnlySpecified = true;
    }
}
