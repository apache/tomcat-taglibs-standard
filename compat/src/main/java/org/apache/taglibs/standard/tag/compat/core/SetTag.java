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
package org.apache.taglibs.standard.tag.compat.core;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 */
public class SetTag extends SetSupport {

    private ValueExpression valueExpression;
    private ValueExpression targetExpression;
    private ValueExpression propertyExpression;

    public SetTag() {
    }

    public void setValue(String value) {
        valueExpression = ExpressionUtil.createValueExpression(pageContext, value, Object.class);
    }

    public void setTarget(String target) {
        targetExpression = ExpressionUtil.createValueExpression(pageContext, target, Object.class);
    }

    public void setProperty(String property) {
        propertyExpression = ExpressionUtil.createValueExpression(pageContext, property, String.class);
    }

    @Override
    public void release() {
        valueExpression = null;
        targetExpression = null;
        propertyExpression = null;
        super.release();
    }

    @Override
    protected boolean isValueSpecified() {
        return valueExpression != null;
    }

    @Override
    protected Object evalValue() throws JspException {
        return valueExpression.getValue(pageContext.getELContext());
    }

    @Override
    protected Object evalTarget() throws JspException {
        return targetExpression.getValue(pageContext.getELContext());
    }

    @Override
    protected String evalProperty() throws JspException {
        return (String) propertyExpression.getValue(pageContext.getELContext());
    }
}
