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

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
 * ValueExpression that refers to a specific member of an indexed variable.
 * This allows individual members of an indexed collection to be used as lvalues.
 */
public final class IndexedValueExpression extends ValueExpression implements Serializable {
    // serialVersionUID value defined by specification JavaDoc
    private static final long serialVersionUID = 1L;

    /**
     * The index variable.
     */
    protected final Integer i;

    /**
     * The indexed variable.
     */
    protected final ValueExpression orig;

    /**
     * Constructor specifying indexed variable and index.
     *
     * @param valueExpression that evaluates to the indexed variable
     * @param i               index specifying the member
     */
    public IndexedValueExpression(ValueExpression valueExpression, int i) {
        orig = valueExpression;
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IndexedValueExpression that = (IndexedValueExpression) o;
        return i.equals(that.i) && orig.equals(that.orig);
    }

    @Override
    public Class getExpectedType() {
        return orig.getExpectedType();
    }

    @Override
    public String getExpressionString() {
        return orig.getExpressionString();
    }

    @Override
    public Class getType(ELContext elContext) {
        return elContext.getELResolver().getType(elContext, orig.getValue(elContext), i);
    }

    @Override
    public Object getValue(ELContext elContext) {
        return elContext.getELResolver().getValue(elContext, orig.getValue(elContext), i);
    }

    @Override
    public int hashCode() {
        return orig.hashCode() + i;
    }

    @Override
    public boolean isLiteralText() {
        return false;
    }

    @Override
    public boolean isReadOnly(ELContext elContext) {
        return elContext.getELResolver().isReadOnly(elContext, orig.getValue(elContext), i);
    }

    @Override
    public void setValue(ELContext elContext, Object arg1) {
        elContext.getELResolver().setValue(elContext, orig.getValue(elContext), i, arg1);
    }

}
