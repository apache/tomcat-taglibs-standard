/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.taglibs.standard.lang.jpath.adapter;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.expression.EvaluationException;
import org.apache.taglibs.standard.lang.jpath.expression.Expression;
import org.apache.taglibs.standard.lang.jpath.expression.Predicate;

public class JSPListUnion implements JSPList {

    private Expression predicate;

    private JSPList left;
    private JSPList right;
    private int position;
    private Object current;
    boolean first;

    public JSPListUnion(JSPList left, JSPList right) {
        this.left = left;
        this.right = right;
        this.position = 0;
        first = true;
    }

    public Object next() {
        if (first) {
            if (left.hasNext()) {
                current = left.next();
            } else {
                first = false;
                current = right.next();
            }
        } else {
            current = right.next();
        }
        position++;
        return current;
    }

    public Object getCurrent() {
        return current;
    }

    public boolean hasNext() {
        boolean result = left.hasNext() || right.hasNext();
        return result;
    }

    public int getPosition() {
        return position;
    }

    public int getLast() {
        return (left.getLast() + right.getLast());
    }

    public boolean applyPredicate(PageContext pageContext, Predicate predicate) throws ConversionException, EvaluationException {
        Object result;
        boolean oneItem = false;
        boolean predicateTrue;
        if (position != 0) {
            throw new ConversionException("You cannot apply a predicate to "
                    + "a JSPList that has begun to be iterated");
        }
        Collection predicated = new ArrayList();
        while (this.hasNext()) {
            this.next();
            result = predicate.evaluate(pageContext, new JSPListIterationContext(this));
            if (result instanceof Double) {
                oneItem = true;
                predicateTrue = ((Double)result).doubleValue() == position;
            } else {
                oneItem = false;
                predicateTrue = Convert.toBoolean(result).booleanValue();
            }
            if (predicateTrue) {
                predicated.add(current);
            }
        }
        this.left = Convert.toJSPList(predicated);
        this.right = Convert.toJSPList(new ArrayList());
        this.position = 0;
        first = true;
        return oneItem;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
