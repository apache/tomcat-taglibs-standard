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
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * JSTL 1.0 compatible implementation of &lt;if&gt; that uses JSP EL support.
 */
public class IfTag extends ConditionalTagSupport {
    private ValueExpression testExpression;

    public IfTag() {
    }

    @Override
    public void release() {
        testExpression = null;
        super.release();
    }

    @Override
    protected boolean condition() throws JspTagException {
        return (Boolean) testExpression.getValue(pageContext.getELContext());
    }

    public void setTest(String test) {
        testExpression = ExpressionUtil.createValueExpression(pageContext, test, Boolean.TYPE);
    }
}
