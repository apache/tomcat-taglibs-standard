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

import java.util.StringTokenizer;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.IteratedExpression;
import javax.servlet.jsp.jstl.core.IteratedValueExpression;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

/**
 * <p>Support for tag handlers for &lt;forTokens&gt;, the tokenizing
 * iteration tag in JSTL 1.0.  This class extends LoopTagSupport and
 * provides ForTokens-specific functionality.  The rtexprvalue and
 * expression-evaluating libraries each have handlers that extend this
 * class.</p>
 *
 * @see javax.servlet.jsp.jstl.core.LoopTagSupport
 * @author Shawn Bayern
 */

public abstract class ForTokensSupport extends LoopTagSupport {

    //*********************************************************************
    // Implementation overview

    /*
     * This handler simply constructs a StringTokenizer based on its input
     * and relays tokens to the iteration implementation that it inherits.
     * The 'items' and 'delims' attributes are expected to be provided by
     * a subtag (presumably in the rtexprvalue or expression-evaluating
     * versions of the JSTL library).
     */


    //*********************************************************************
    // ForEachTokens-specific state (protected)

    protected Object items;                       // 'items' attribute
    protected String delims;                      // 'delims' attribute
    protected StringTokenizer st;                 // digested tokenizer
    protected int currentIndex = 0;
    private IteratedExpression itemsValueIteratedExpression;


    //*********************************************************************
    // Iteration control methods

    /*
     * These just create and use a StringTokenizer.
     * We inherit semantics and Javadoc from LoopTagSupport.
     */

    protected void prepare() throws JspTagException {
        if (items instanceof ValueExpression) {
            deferredExpression = (ValueExpression) items;
            ELContext myELContext = pageContext.getELContext();
            Object originalValue = deferredExpression.getValue(myELContext);
            if (originalValue instanceof String) {
                st = new StringTokenizer((String)originalValue, delims);
            } else {
                throw new JspTagException();
            }
        } else {
            st = new StringTokenizer((String)items, delims);
        }
    }

    protected boolean hasNext() throws JspTagException {
        return st.hasMoreElements();
    }

    protected Object next() throws JspTagException {
        if (deferredExpression!=null) {
            st.nextElement();
            if (itemsValueIteratedExpression==null) {
                itemsValueIteratedExpression = new IteratedExpression(deferredExpression, getDelims());
            }
            ValueExpression nextValue = new IteratedValueExpression(itemsValueIteratedExpression, currentIndex);
            currentIndex++;
            return nextValue;
        } else {
            return st.nextElement();
        }
    }


    //*********************************************************************
    // Tag logic and lifecycle management


    // Releases any resources we may have (or inherit)
    public void release() {
        super.release();
        items = delims = null;
        st = null;
    }

    /**
     * Get the delimiter for string tokens. Used only for constructing
     * the deferred expression for it.
     */
    protected String getDelims() {
        return delims;
    }
}
