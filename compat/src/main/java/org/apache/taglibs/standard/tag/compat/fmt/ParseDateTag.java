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
import org.apache.taglibs.standard.tag.common.fmt.ParseDateSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <parseDate>} using the container's EL implementation.
 */
public class ParseDateTag extends ParseDateSupport {

    private ValueExpression valueExpression;
    private ValueExpression typeExpression;
    private ValueExpression dateStyleExpression;
    private ValueExpression timeStyleExpression;
    private ValueExpression patternExpression;
    private ValueExpression timeZoneExpression;
    private ValueExpression parseLocaleExpression;

    @Override
    public int doStartTag() throws JspException {
        value = ExpressionUtil.evaluate(valueExpression, pageContext);
        type = ExpressionUtil.evaluate(typeExpression, pageContext);
        dateStyle = ExpressionUtil.evaluate(dateStyleExpression, pageContext);
        timeStyle = ExpressionUtil.evaluate(timeStyleExpression, pageContext);
        pattern = ExpressionUtil.evaluate(patternExpression, pageContext);
        timeZone = ExpressionUtil.evaluate(timeZoneExpression, pageContext);
        parseLocale = LocaleUtil.parseLocaleAttributeValue(
                ExpressionUtil.evaluate(parseLocaleExpression, pageContext));

        return super.doStartTag();
    }

    @Override
    public void release() {
        super.release();
        valueExpression = null;
        typeExpression = null;
        dateStyleExpression = null;
        timeStyleExpression = null;
        patternExpression = null;
        timeZoneExpression = null;
        parseLocaleExpression = null;
    }

    public void setValue(String expression) {
        valueExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
        valueSpecified = true;
    }

    public void setType(String expression) {
        typeExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setDateStyle(String expression) {
        dateStyleExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setTimeStyle(String expression) {
        timeStyleExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setPattern(String expression) {
        patternExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setTimeZone(String expression) {
        timeZoneExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
    }

    public void setParseLocale(String expression) {
        parseLocaleExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
    }
}
