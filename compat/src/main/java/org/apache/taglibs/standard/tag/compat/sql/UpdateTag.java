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

package org.apache.taglibs.standard.tag.compat.sql;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.sql.UpdateTagSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <update>} using the container's EL implementation.
 */
public class UpdateTag extends UpdateTagSupport {
    private ValueExpression dataSourceExpression;
    private ValueExpression sqlExpression;

    @Override
    public int doStartTag() throws JspException {
        rawDataSource = ExpressionUtil.evaluate(dataSourceExpression, pageContext);
        sql = ExpressionUtil.evaluate(sqlExpression, pageContext);

        return super.doStartTag();
    }

    @Override
    public void release() {
        super.release();

        dataSourceExpression = null;
        sqlExpression = null;
    }

    public void setDataSource(String expression) {
        dataSourceExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
        dataSourceSpecified = true;
    }

    public void setSql(String expression) {
        sqlExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }
}
