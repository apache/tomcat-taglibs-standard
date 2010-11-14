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

import org.apache.taglibs.standard.tag.common.core.ParamSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 */
public class ParamTag extends ParamSupport {

    private ValueExpression nameExpression;
    private ValueExpression valueExpression;

    public ParamTag() {
    }

    @Override
    public int doStartTag() throws JspException {
        name = (String) nameExpression.getValue(pageContext.getELContext());
        value = (String) valueExpression.getValue(pageContext.getELContext());
        return super.doStartTag();
    }

    @Override
    public void release() {
        nameExpression = null;
        valueExpression = null;
        super.release();
    }

    public void setName(String name) {
        nameExpression = ExpressionUtil.createValueExpression(pageContext, name, String.class);
    }

    public void setValue(String value) {
        valueExpression = ExpressionUtil.createValueExpression(pageContext, value, String.class);
    }
}
