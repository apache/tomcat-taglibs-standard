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

import org.apache.taglibs.standard.tag.common.core.RedirectSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 */
public class RedirectTag extends RedirectSupport {

    private ValueExpression urlExpression;
    private ValueExpression contextExpression;

    public RedirectTag() {
    }

    @Override
    public void release() {
        urlExpression = null;
        contextExpression = null;
        super.release();
    }

    @Override
    public int doStartTag() throws JspException {
        url = (String) urlExpression.getValue(pageContext.getELContext());
        if (contextExpression != null) {
            context = (String) contextExpression.getValue(pageContext.getELContext());
        }
        return super.doStartTag();
    }

    public void setUrl(String url) {
        urlExpression = ExpressionUtil.createValueExpression(pageContext, url, String.class);
    }

    public void setContextExpression(String context) {
        contextExpression = ExpressionUtil.createValueExpression(pageContext, context, String.class);
    }
}
