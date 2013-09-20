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

import org.apache.taglibs.standard.tag.common.core.ForTokensSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * JSTL 1.0 compatible implementation of &lt;fortokens&gt; that uses JSP EL support.
 */
public class ForTokensTag extends ForTokensSupport {

    private ValueExpression beginExpression;
    private ValueExpression endExpression;
    private ValueExpression stepExpression;
    private ValueExpression itemsExpression;
    private ValueExpression delimsExpression;

    public ForTokensTag() {
    }

    @Override
    public void release() {
        beginExpression = null;
        endExpression = null;
        stepExpression = null;
        itemsExpression = null;
        delimsExpression = null;
        super.release();
    }

    @Override
    public int doStartTag() throws JspException {
        if (beginSpecified) {
            begin = (Integer) beginExpression.getValue(pageContext.getELContext());
            validateBegin();
        }
        if (endSpecified) {
            end = (Integer) endExpression.getValue(pageContext.getELContext());
            validateEnd();
        }
        if (stepSpecified) {
            step = (Integer) stepExpression.getValue(pageContext.getELContext());
            validateStep();
        }
        if (itemsExpression != null) {
            items = itemsExpression.getValue(pageContext.getELContext());
            if (items == null) {
                items = "";
            }
        }
        if (delimsExpression != null) {
            delims = (String) delimsExpression.getValue(pageContext.getELContext());
            if (delims == null) {
                delims = "";
            }
        }
        return super.doStartTag();
    }

    public void setBegin(String begin) {
        beginExpression = ExpressionUtil.createValueExpression(pageContext, begin, Integer.TYPE);
        beginSpecified = true;
    }

    public void setEnd(String end) {
        endExpression = ExpressionUtil.createValueExpression(pageContext, end, Integer.TYPE);
        endSpecified = true;
    }

    public void setStep(String step) {
        stepExpression = ExpressionUtil.createValueExpression(pageContext, step, Integer.TYPE);
        stepSpecified = true;
    }

    public void setItems(String items) {
        itemsExpression = ExpressionUtil.createValueExpression(pageContext, items, Object.class);
    }

    public void setDelims(String delims) {
        delimsExpression = ExpressionUtil.createValueExpression(pageContext, delims, String.class);
    }
}
