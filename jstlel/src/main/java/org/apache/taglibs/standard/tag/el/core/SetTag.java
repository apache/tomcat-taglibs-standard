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
import org.apache.taglibs.standard.tag.common.core.SetSupport;

/**
 * JSTL 1.0 compatible version of &lt;set&gt; that accepts expressions for attribute values.
 *
 * @author Shawn Bayern
 */

public class SetTag extends SetSupport {

    private boolean valueSpecified;
    private String valueExpression;
    private String targetExpression;
    private String propertyExpression;

    public SetTag() {
    }

    public void setValue(String value) {
        this.valueExpression = value;
        this.valueSpecified = true;
    }

    public void setTarget(String target) {
        this.targetExpression = target;
    }

    public void setProperty(String property) {
        this.propertyExpression = property;
    }

    @Override
    public void release() {
        valueExpression = null;
        targetExpression = null;
        propertyExpression = null;
        valueSpecified = false;
        super.release();
    }

    @Override
    protected boolean isValueSpecified() {
        return valueSpecified;
    }

    @Override
    protected Object evalValue() throws JspException {
        return ExpressionEvaluatorManager.evaluate("value", valueExpression, Object.class, this, pageContext);
    }

    @Override
    protected Object evalTarget() throws JspException {
        return ExpressionEvaluatorManager.evaluate("target", targetExpression, Object.class, this, pageContext);
    }

    @Override
    protected String evalProperty() throws JspException {
        return (String) ExpressionEvaluatorManager.evaluate("property", propertyExpression, String.class, this, pageContext);
    }
}
