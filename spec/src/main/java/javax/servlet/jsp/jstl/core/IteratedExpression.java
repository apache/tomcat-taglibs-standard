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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
 * Helper class for accessing members of a deferred expression result by index.
 */
public final class IteratedExpression {
    protected final ValueExpression orig;
    protected final String delims;
    private Object originalListObject = null;
    private Iterator currentListObject = null;
    private int currentIndex = 0;

    private enum TypesEnum {
        Undefined, ACollection, AnIterator, AnEnumeration, AMap, AString
    }

    private TypesEnum type = TypesEnum.Undefined;

    /**
     * Constructor specifying the expression to access.
     * If the expression evaluates to a String, then it will be split using the specified delimiters.
     *
     * @param orig   the original expression to be accessed
     * @param delims delimiters to be used to split a String result
     */
    public IteratedExpression(ValueExpression orig, String delims) {
        this.orig = orig;
        this.delims = delims;
    }

    /**
     * Iterates the original expression and returns the value at the index.
     *
     * @param context against which the expression should be evaluated
     * @param i       the index of the value to return
     * @return the value at the index
     */
    public Object getItem(ELContext context, int i) {
        if (originalListObject == null) {
            originalListObject = orig.getValue(context);
            if (originalListObject instanceof Collection) {
                type = TypesEnum.ACollection;
            } else if (originalListObject instanceof Iterator) {
                type = TypesEnum.AnIterator;
            } else if (originalListObject instanceof Enumeration) {
                type = TypesEnum.AnEnumeration;
            } else if (originalListObject instanceof Map) {
                type = TypesEnum.AMap;
            } else if (originalListObject instanceof String) { //StringTokens
                type = TypesEnum.AString;
            } else {
                //it's of some other type ... should never get here
                throw new RuntimeException("IteratedExpression.getItem: Object not of correct type.");
            }
            currentListObject = returnNewIterator(originalListObject, type);
        }
        Object currentObject = null;
        if (i < currentIndex) {
            currentListObject = returnNewIterator(originalListObject, type);
            currentIndex = 0;
        }
        for (; currentIndex <= i; currentIndex++) {
            if (currentListObject.hasNext()) {
                currentObject = currentListObject.next();
            } else {
                throw new RuntimeException("IteratedExpression.getItem: Index out of Bounds");
            }
        }
        return currentObject;
    }

    /**
     * Returns the original expression.
     *
     * @return the original expression
     */
    public ValueExpression getValueExpression() {
        return orig;
    }

    private Iterator returnNewIterator(Object o, TypesEnum type) {
        Iterator i = null;
        switch (type) {
            case ACollection:
                i = ((Collection) o).iterator();
                break;
            case AnIterator:
                if (currentListObject == null) {
                    //first time through ... need to create Vector for originalListObject
                    Vector v = new Vector();
                    Iterator myI = (Iterator) o;
                    while (myI.hasNext()) {
                        v.add(myI.next());
                    }
                    originalListObject = v;
                }
                i = ((Vector) originalListObject).iterator();
                break;
            case AnEnumeration:
                if (currentListObject == null) {
                    //first time through ... need to create Vector for originalListObject
                    Vector v = new Vector();
                    Enumeration myE = (Enumeration) o;
                    while (myE.hasMoreElements()) {
                        v.add(myE.nextElement());
                    }
                    originalListObject = v;
                }
                i = ((Vector) originalListObject).iterator();
                break;
            case AMap:
                Set s = ((Map) o).entrySet();
                i = s.iterator();
                break;
            case AString:
                if (currentListObject == null) {
                    //first time through ... need to create Vector for originalListObject
                    Vector v = new Vector();
                    StringTokenizer st = new StringTokenizer((String) o, delims);
                    while (st.hasMoreElements()) {
                        v.add(st.nextElement());
                    }
                    originalListObject = v;
                }
                i = ((Vector) originalListObject).iterator();
                break;
            default: //do Nothing ... this is not possible
                break;
        }
        return i;
    }
}