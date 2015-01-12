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
import javax.xml.transform.Result;

import org.apache.taglibs.standard.tag.common.xml.TransformSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <param>} using the container's EL implementation.
 */
public class TransformTag extends TransformSupport {
    private ValueExpression xmlExpression;
    private ValueExpression xmlSystemIdExpression;
    private ValueExpression xsltExpression;
    private ValueExpression xsltSystemIdExpression;
    private ValueExpression resultExpression;

    @Override
    public int doStartTag() throws JspException {
        xml = ExpressionUtil.evaluate(xmlExpression, pageContext);
        xmlSystemId = ExpressionUtil.evaluate(xmlSystemIdExpression, pageContext);
        xslt = ExpressionUtil.evaluate(xsltExpression, pageContext);
        xsltSystemId = ExpressionUtil.evaluate(xsltSystemIdExpression, pageContext);
        result = ExpressionUtil.evaluate(resultExpression, pageContext);

        return super.doStartTag();
    }

    @Override
    public void release() {
        super.release();

        xmlExpression = null;
        xmlSystemIdExpression = null;
        xsltExpression = null;
        xsltSystemIdExpression = null;
        resultExpression = null;
    }

    public void setXml(String expression) {
        xmlExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
        xmlSpecified = true;
    }

    public void setXmlSystemId(String expression) {
        xmlSystemIdExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setXslt(String expression) {
        xsltExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
    }

    public void setXsltSystemId(String expression) {
        xsltSystemIdExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setResult(String expression) {
        resultExpression = ExpressionUtil.createValueExpression(pageContext, expression, Result.class);
    }
}
