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

package org.apache.taglibs.standard.tag.compat.xml;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.xml.ExprSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <out>} using the container's EL implementation.
 */
public class ExprTag extends ExprSupport {
    private ValueExpression escapeXmlExpression;

    @Override
    public int doStartTag() throws JspException {
        escapeXml = ExpressionUtil.evaluate(escapeXmlExpression, pageContext, true);

        return super.doStartTag();
    }

    @Override
    public void release() {
        super.release();

        escapeXmlExpression = null;
    }

    public void setEscapeXml(String expression) {
        escapeXmlExpression = ExpressionUtil.createValueExpression(pageContext, expression, Boolean.class);
    }
}
