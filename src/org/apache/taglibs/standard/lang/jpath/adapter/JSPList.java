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

import java.util.Iterator;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.expression.EvaluationException;
import org.apache.taglibs.standard.lang.jpath.expression.Predicate;

public interface JSPList extends Iterator {
    public Object next();
    public Object getCurrent();
    public boolean hasNext();
    public int getPosition();
    public int getLast();
    public boolean applyPredicate(PageContext pageContext, Predicate predicate) throws ConversionException, EvaluationException;
    public void remove();
}
