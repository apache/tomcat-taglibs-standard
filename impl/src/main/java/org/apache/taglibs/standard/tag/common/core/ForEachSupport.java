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

package org.apache.taglibs.standard.tag.common.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.IndexedValueExpression;
import javax.servlet.jsp.jstl.core.IteratedExpression;
import javax.servlet.jsp.jstl.core.IteratedValueExpression;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;forEach&gt;, the core iteration
 * tag in JSTL 1.0.  This class extends LoopTagSupport and provides
 * ForEach-specific functionality.  The rtexprvalue library and the
 * expression-evaluating library each have handlers that extend this
 * class.</p>
 * <p>Localized here is the logic for handling the veritable smorgasbord
 * of types supported by &lt;forEach&gt;, including arrays,
 * Collections, and others.  To see how the actual iteration is controlled,
 * review the javax.servlet.jsp.jstl.core.LoopTagSupport class instead.
 * </p>
 *
 * @author Shawn Bayern
 * @see javax.servlet.jsp.jstl.core.LoopTagSupport
 */
public abstract class ForEachSupport extends LoopTagSupport {
    protected Iterator items;              // our 'digested' items
    protected Object rawItems;                    // our 'raw' items

    @Override
    protected void prepare() throws JspTagException {
        // produce the right sort of ForEachIterator
        if (rawItems == null) {
            // if no items were specified, iterate from begin to end
            items = new ToEndIterator(end);
        } else if (rawItems instanceof ValueExpression) {
            deferredExpression = (ValueExpression) rawItems;
            Object o = deferredExpression.getValue(pageContext.getELContext());
            Iterator iterator = toIterator(o);
            if (isIndexed(o)) {
                items = new IndexedDeferredIterator(iterator, deferredExpression);
            } else {
                items = new IteratedDeferredIterator(iterator, new IteratedExpression(deferredExpression, getDelims()));
            }
        } else {
            items = toIterator(rawItems);
        }
    }

    private Iterator toIterator(Object rawItems) throws JspTagException {
        if (rawItems instanceof Collection) {
            return ((Collection) rawItems).iterator();
        } else if (rawItems.getClass().isArray()) {
            return new ArrayIterator(rawItems);
        } else if (rawItems instanceof Iterator) {
            return (Iterator) rawItems;
        } else if (rawItems instanceof Enumeration) {
            return new EnumerationIterator((Enumeration) rawItems);
        } else if (rawItems instanceof Map) {
            return ((Map) rawItems).entrySet().iterator();
        } else if (rawItems instanceof String) {
            return new EnumerationIterator(new StringTokenizer((String) rawItems, ","));
        } else {
            throw new JspTagException(Resources.getMessage("FOREACH_BAD_ITEMS"));
        }
    }

    private boolean isIndexed(Object o) {
        return o.getClass().isArray();
    }

    @Override
    protected boolean hasNext() throws JspTagException {
        return items.hasNext();
    }

    @Override
    protected Object next() throws JspTagException {
        return items.next();
    }

    @Override
    public void release() {
        super.release();
        items = null;
        rawItems = null;
    }


    /**
     * Iterator that simply counts up to 'end.'
     * Unlike the previous implementation this does not attempt to pre-allocate an array
     * containing all values from 0 to 'end' as that can result in excessive memory allocation
     * for large values of 'end.'
     * LoopTagSupport calls next() 'begin' times in order to discard the initial values,
     * In order to maintain this contract, this implementation always starts at 0.
     * Future optimization to skip these redundant calls might be possible.
     */
    private static class ToEndIterator extends ReadOnlyIterator {
        private final int end;
        private int i;

        private ToEndIterator(int end) {
            this.end = end;
        }

        public boolean hasNext() {
            return i <= end;
        }

        public Object next() {
            if (i <= end) {
                return i++;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    /**
     * Iterator over an Enumeration.
     */
    private static class EnumerationIterator extends ReadOnlyIterator {
        private final Enumeration e;

        private EnumerationIterator(Enumeration e) {
            this.e = e;
        }

        public boolean hasNext() {
            return e.hasMoreElements();
        }

        public Object next() {
            return e.nextElement();
        }
    }

    /**
     * Iterator over an array, including arrays of primitive types.
     */
    private static class ArrayIterator extends ReadOnlyIterator {
        private final Object array;
        private final int length;
        private int i = 0;

        private ArrayIterator(Object array) {
            this.array = array;
            length = Array.getLength(array);
        }

        public boolean hasNext() {
            return i < length;
        }

        public Object next() {
            try {
                return Array.get(array, i++);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }
    }

    private static class IndexedDeferredIterator extends DeferredIterator {
        private final ValueExpression itemsValueExpression;

        private IndexedDeferredIterator(Iterator iterator, ValueExpression itemsValueExpression) {
            super(iterator);
            this.itemsValueExpression = itemsValueExpression;
        }

        public Object next() {
            iterator.next();
            return new IndexedValueExpression(itemsValueExpression, currentIndex++);
        }
    }

    private static class IteratedDeferredIterator extends DeferredIterator {
        private final IteratedExpression itemsValueIteratedExpression;

        private IteratedDeferredIterator(Iterator iterator, IteratedExpression itemsValueIteratedExpression) {
            super(iterator);
            this.itemsValueIteratedExpression = itemsValueIteratedExpression;
        }

        public Object next() {
            iterator.next();
            return new IteratedValueExpression(itemsValueIteratedExpression, currentIndex++);
        }
    }

    private abstract static class DeferredIterator extends ReadOnlyIterator {
        protected final Iterator iterator;
        protected int currentIndex = 0;

        protected DeferredIterator(Iterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }
    }

    private abstract static class ReadOnlyIterator implements Iterator {
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
