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

import org.apache.taglibs.standard.lang.jpath.expression.*;
import javax.servlet.jsp.*;

import java.util.*;

public class CollectionAdapter extends JSPAdapter implements JSPList {

    private Expression predicate;

    private Iterator iterator;
    private int size;
    private int position;
    private Object current;

    public Object next() {
        position++;
        current = iterator.next();
        return current;
    }

    public Object getCurrent() {
        return current;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public int getPosition() {
        return position;
    }

    public int getLast() {
        return size;
    }

    private void setCollection(Collection c) {
        this.iterator = c.iterator();
        this.size = c.size();
        this.position = 0;
    }

    public boolean applyPredicate(PageContext pageContext, Predicate predicate) throws ConversionException, EvaluationException {
        boolean oneItem = false;
        Object result;
        boolean predicateTrue;
        if (position != 0) {
            throw new ConversionException("You cannot apply a predicate to "
                    + "a JSPList that has begun to be iterated");
        }
        Collection predicated = new ArrayList();
        while (iterator.hasNext()) {
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
        this.iterator = predicated.iterator();
        this.size = predicated.size();
        this.position = 0;
        return oneItem;
    }

    public static Object adapt(Object o) {
        CollectionAdapter adapter = new CollectionAdapter();
        adapter.setCollection((Collection)o);
        return adapter;
    }

    public static Class[] getAdaptedClasses() {
        Class[] adaptedClasses = {Collection.class};
        return adaptedClasses;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    
}
