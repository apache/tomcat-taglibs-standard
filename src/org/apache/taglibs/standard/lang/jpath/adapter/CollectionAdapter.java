/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.taglibs.standard.lang.jpath.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.expression.EvaluationException;
import org.apache.taglibs.standard.lang.jpath.expression.Expression;
import org.apache.taglibs.standard.lang.jpath.expression.Predicate;

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
