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

import org.apache.taglibs.standard.tag.common.core.OutSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 */
public class OutTag extends OutSupport {

    private ValueExpression valueExpression;
    private ValueExpression defaultExpression;
    private ValueExpression escapeXmlExpression;

    public OutTag() {
    }

    @Override
    public void release() {
        valueExpression = null;
        defaultExpression = null;
        escapeXmlExpression = null;
        super.release();
    }

    public void setValue(String value) {
        valueExpression = ExpressionUtil.createValueExpression(pageContext, value, Object.class);
    }

    public void setDefault(String def) {
        defaultExpression = ExpressionUtil.createValueExpression(pageContext, def, String.class);
    }

    public void setEscapeXml(String escapeXml) {
        escapeXmlExpression = ExpressionUtil.createValueExpression(pageContext, escapeXml, Boolean.TYPE);
    }

    @Override
    protected Object evalValue() throws JspException {
        if (valueExpression == null) {
            return null;
        }
        return valueExpression.getValue(pageContext.getELContext());
    }

    @Override
    protected String evalDefault() throws JspException {
        if (defaultExpression == null) {
            return null;
        }
        return (String) defaultExpression.getValue(pageContext.getELContext());
    }

    @Override
    protected boolean evalEscapeXml() throws JspException {
        if (escapeXmlExpression == null) {
            return true;
        }
        return (Boolean) escapeXmlExpression.getValue(pageContext.getELContext());
    }
}
