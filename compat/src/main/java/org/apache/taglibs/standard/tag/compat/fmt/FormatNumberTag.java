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

import org.apache.taglibs.standard.tag.common.fmt.FormatNumberSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <formatNumber>} using the container's EL implementation.
 */
public class FormatNumberTag extends FormatNumberSupport {

    private ValueExpression valueExpression;
    private ValueExpression typeExpression;
    private ValueExpression patternExpression;
    private ValueExpression currencyCodeExpression;
    private ValueExpression currencySymbolExpression;
    private ValueExpression groupingUsedExpression;
    private ValueExpression maxIntegerDigitsExpression;
    private ValueExpression minIntegerDigitsExpression;
    private ValueExpression maxFractionDigitsExpression;
    private ValueExpression minFractionDigitsExpression;

    @Override
    public int doStartTag() throws JspException {
        value = ExpressionUtil.evaluate(valueExpression, pageContext);
        type = ExpressionUtil.evaluate(typeExpression, pageContext);
        pattern = ExpressionUtil.evaluate(patternExpression, pageContext);
        currencyCode = ExpressionUtil.evaluate(currencyCodeExpression, pageContext);
        currencySymbol = ExpressionUtil.evaluate(currencySymbolExpression, pageContext);
        isGroupingUsed = ExpressionUtil.evaluate(groupingUsedExpression, pageContext, false);
        maxIntegerDigits = ExpressionUtil.evaluate(maxIntegerDigitsExpression, pageContext, 0);
        minIntegerDigits = ExpressionUtil.evaluate(minIntegerDigitsExpression, pageContext, 0);
        maxFractionDigits = ExpressionUtil.evaluate(maxFractionDigitsExpression, pageContext, 0);
        minFractionDigits = ExpressionUtil.evaluate(minFractionDigitsExpression, pageContext, 0);

        return super.doStartTag();
    }

    @Override
    public void release() {
        valueExpression = null;
        typeExpression = null;
        patternExpression = null;
        currencyCodeExpression = null;
        currencySymbolExpression = null;
        groupingUsedExpression = null;
        maxIntegerDigitsExpression = null;
        minIntegerDigitsExpression = null;
        maxFractionDigitsExpression = null;
        minFractionDigitsExpression = null;

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

    public void setCurrencyCode(String expression) {
        currencyCodeExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setCurrencySymbol(String expression) {
        currencySymbolExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setGroupingUsed(String expression) {
        groupingUsedExpression = ExpressionUtil.createValueExpression(pageContext, expression, Boolean.class);
        groupingUsedSpecified = true;
    }

    public void setMaxIntegerDigits(String expression) {
        maxIntegerDigitsExpression = ExpressionUtil.createValueExpression(pageContext, expression, Integer.class);
        maxIntegerDigitsSpecified = true;
    }

    public void setMinIntegerDigits(String expression) {
        minIntegerDigitsExpression = ExpressionUtil.createValueExpression(pageContext, expression, Integer.class);
        minIntegerDigitsSpecified = true;
    }

    public void setMaxFactionDigits(String expression) {
        maxFractionDigitsExpression = ExpressionUtil.createValueExpression(pageContext, expression, Integer.class);
        maxFractionDigitsSpecified = true;
    }

    public void setMinFractionDigits(String expression) {
        minFractionDigitsExpression = ExpressionUtil.createValueExpression(pageContext, expression, Integer.class);
        minFractionDigitsSpecified = true;
    }
}
