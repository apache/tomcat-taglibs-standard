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

package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.OutSupport;

/**
 * <p>A handler for &lt;out&gt;, which redirects the browser to a
 * new URL.
 *
 * @author Shawn Bayern
 */

public class OutTag extends OutSupport {

    //*********************************************************************
    // 'Private' state (implementation details)

    private String valueExpression;            // stores EL-based property
    private String defaultExpression;            // stores EL-based property
    private String escapeXmlExpression;            // stores EL-based property


    //*********************************************************************
    // Constructor

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
        this.valueExpression = value;
    }

    public void setDefault(String def) {
        this.defaultExpression = def;
    }

    public void setEscapeXml(String escapeXml) {
        this.escapeXmlExpression = escapeXml;
    }

    @Override
    protected Object evalValue() throws JspException {
        if (valueExpression == null) {
            return null;
        }
        return ExpressionEvaluatorManager.evaluate("value", valueExpression, Object.class, this, pageContext);
    }

    @Override
    protected String evalDefault() throws JspException {
        if (defaultExpression == null) {
            return null;
        }
        return (String) ExpressionEvaluatorManager.evaluate("default", defaultExpression, String.class, this, pageContext);
    }

    @Override
    protected boolean evalEscapeXml() throws JspException {
        if (escapeXmlExpression == null) {
            return true;
        }
        Boolean result = (Boolean) ExpressionEvaluatorManager.evaluate("escapeXml", escapeXmlExpression, Boolean.class, this, pageContext);
        if (result == null) {
            return true;
        }
        return result;
    }
}
