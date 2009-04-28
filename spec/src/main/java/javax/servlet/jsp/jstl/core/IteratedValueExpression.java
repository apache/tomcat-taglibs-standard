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

public final class IteratedValueExpression extends ValueExpression {

    private static final long serialVersionUID = 2771035360633553883L;
    //IteratedExpression is not serializable
    protected final IteratedExpression iteratedExpression;
    protected final int i;
    
    public IteratedValueExpression(IteratedExpression _iteratedExpression, int _i) {
        iteratedExpression = _iteratedExpression;
        i = _i;
    }
    
    public boolean equals(Object arg0) {
        if (arg0==null) {
            return false;
        }
        if (iteratedExpression.getValueExpression().equals(arg0)) {
            return true;
        }
        return false;
    }

    public Class getExpectedType() {
        return iteratedExpression.getValueExpression().getExpectedType();
    }

    public String getExpressionString() {
        return iteratedExpression.getValueExpression().getExpressionString();
    }

    public Class getType(ELContext elContext) {
        return iteratedExpression.getValueExpression().getType(elContext); 
    }

    public Object getValue(ELContext elContext) {
        return iteratedExpression.getItem(elContext, i); 
    }

    public int hashCode() {
        return iteratedExpression.hashCode()+i;
    }

    public boolean isLiteralText() {
        return false;
    }

    public boolean isReadOnly(ELContext elContext) {
        return true;
    }

    public void setValue(ELContext elContext, Object arg1) {
    }

}
