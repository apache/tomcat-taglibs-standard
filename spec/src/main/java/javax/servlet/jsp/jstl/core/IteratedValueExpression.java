/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package javax.servlet.jsp.jstl.core;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
 * ValueExpression that refers to a specific member of an indexed variable backed by an IteratedExpression.
 * This allows individual members of an indexed collection to be used as lvalues.
 */
public final class IteratedValueExpression extends ValueExpression {
    // serialVersionUID value defined by specification JavaDoc
    private static final long serialVersionUID = 1L;

    protected final int i;
    // TODO: IteratedExpression is not serializable - should this be ignored?
    protected final IteratedExpression iteratedExpression;

    public IteratedValueExpression(IteratedExpression _iteratedExpr, int i) {
        iteratedExpression = _iteratedExpr;
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        IteratedValueExpression that = (IteratedValueExpression) obj;
        return i == that.i && iteratedExpression.equals(that.iteratedExpression);
    }

    @Override
    public Class getExpectedType() {
        return iteratedExpression.getValueExpression().getExpectedType();
    }

    @Override
    public String getExpressionString() {
        return iteratedExpression.getValueExpression().getExpressionString();
    }

    @Override
    public Class getType(ELContext elContext) {
        return iteratedExpression.getValueExpression().getType(elContext);
    }

    @Override
    public Object getValue(ELContext elContext) {
        return iteratedExpression.getItem(elContext, i);
    }

    @Override
    public int hashCode() {
        return iteratedExpression.hashCode() + i;
    }

    @Override
    public boolean isLiteralText() {
        return false;
    }

    @Override
    public boolean isReadOnly(ELContext elContext) {
        return true;
    }

    @Override
    public void setValue(ELContext elContext, Object arg1) {
    }

}
