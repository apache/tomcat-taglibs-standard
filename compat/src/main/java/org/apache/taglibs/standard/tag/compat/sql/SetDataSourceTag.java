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

import org.apache.taglibs.standard.tag.common.sql.SetDataSourceTagSupport;
import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * Implementation of JSTL 1.0 {@code <setDataSource>} using the container's EL implementation.
 */
public class SetDataSourceTag extends SetDataSourceTagSupport {
    private ValueExpression dataSourceExpression;
    private ValueExpression driverClassNameExpression;
    private ValueExpression jdbcUrlExpression;
    private ValueExpression usernameExpression;
    private ValueExpression passwordExpression;

    @Override
    public int doStartTag() throws JspException {
        dataSource = ExpressionUtil.evaluate(dataSourceExpression, pageContext);
        driverClassName = ExpressionUtil.evaluate(driverClassNameExpression, pageContext);
        jdbcURL = ExpressionUtil.evaluate(jdbcUrlExpression, pageContext);
        userName = ExpressionUtil.evaluate(usernameExpression, pageContext);
        password = ExpressionUtil.evaluate(passwordExpression, pageContext);

        return super.doStartTag();
    }

    @Override
    public void release() {
        super.release();

        dataSourceExpression = null;
        driverClassNameExpression = null;
        jdbcUrlExpression = null;
        usernameExpression = null;
        passwordExpression = null;
    }

    public void setDataSource(String expression) {
        dataSourceExpression = ExpressionUtil.createValueExpression(pageContext, expression, Object.class);
        dataSourceSpecified = true;
    }

    public void setDriver(String expression) {
        driverClassNameExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setUrl(String expression) {
        jdbcUrlExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setUser(String expression) {
        usernameExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }

    public void setPassword(String expression) {
        passwordExpression = ExpressionUtil.createValueExpression(pageContext, expression, String.class);
    }
}
