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

public final class IndexedValueExpression extends ValueExpression implements Serializable {

    private static final long serialVersionUID = -7300711701036452952L;
    protected final Integer i;
    protected final ValueExpression orig;
    
    public IndexedValueExpression(ValueExpression valueExpression, int _i) {
        orig = valueExpression;
        i=_i;
    }
    
    public boolean equals(Object arg0) {
        boolean rc=false;
        if (arg0!=null) {
            if (arg0.equals(orig)) {
                rc = true;
            }
        }
        return rc;
    }

    public Class getExpectedType() {
        return orig.getExpectedType();
    }

    public String getExpressionString() {
        return orig.getExpressionString();
    }

    public Class getType(ELContext elContext) {
        return elContext.getELResolver().getType(elContext, orig.getValue(elContext), i);
    }

    public Object getValue(ELContext elContext) {
        return elContext.getELResolver().getValue(elContext, orig.getValue(elContext), i);
    }

    public int hashCode() {
        return orig.hashCode()+i;
    }

    public boolean isLiteralText() {
        return false;
    }

    public boolean isReadOnly(ELContext elContext) {
        return elContext.getELResolver().isReadOnly(elContext, orig.getValue(elContext), i);
    }

    public void setValue(ELContext elContext, Object arg1) {
        elContext.getELResolver().setValue(elContext, orig.getValue(elContext), i, arg1);
    }

}
