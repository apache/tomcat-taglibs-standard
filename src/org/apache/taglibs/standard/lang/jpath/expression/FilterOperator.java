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

package org.apache.taglibs.standard.lang.jpath.expression;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.ConversionException;
import org.apache.taglibs.standard.lang.jpath.adapter.Convert;
import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;
import org.apache.taglibs.standard.lang.jpath.adapter.JSPList;

/**
 * The FilterOperator class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class FilterOperator extends SimpleNode implements Introspectable {

    /**
     * Used to create an instance of the FilterOperator class
     *
     *
     * @param id
     *
     */
    public FilterOperator(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the FilterOperator class
     *
     *
     * @param p
     * @param id
     *
     */
    public FilterOperator(Parser p, int id) {
        super(p, id);
    }

    /**
     * Provides a method to print a normalized version of the original
     * expression.  The normalized version has standardized spacing and
     * parenthesis, and can be used to compare expressions formatted
     * in different ways to see if they are actually the same expression.
     *
     *
     * @return The normalized version of the original expression
     *
     */
    public String toNormalizedString() {

        String normalized = "";

        normalized = jjtGetChild(0).toNormalizedString()
                + jjtGetChild(1).toNormalizedString();

        return normalized;
    }

    /**
     * This method evaluates this node of the expression and all child nodes.
     * It returns the result of the
     * evaluation as an <tt>Object</tt>.  If any problems are encountered
     * during the evaluation, an <tt>EvaluationException</tt> is thrown.
     *
     *
     * @param pageContext the current JSP PageContext
     *
     * @param icontext the Iteration Context of the expression.  If there is
     *         no interation context, this should be null.
     *
     * @return the result of the expression evaluation as an object
     *
     * @throws EvaluationException if a problem is encountered during the
     *         evaluation
     */
    public Object evaluate(PageContext pageContext, IterationContext icontext)
            throws EvaluationException {

        Object result;

        try {
            JSPList leftSide =
                Convert.toJSPList(jjtGetChild(0).evaluate(pageContext,
                    icontext));
            Predicate predicate = (Predicate) jjtGetChild(1);
            boolean oneItem = leftSide.applyPredicate(pageContext, predicate);

            if (oneItem) {
                if (leftSide.getLast() == 1) {
                    result = leftSide.next();
                } else {
                    result = null;
                }
            } else {
                result = leftSide;
            }
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return result;
    }

    /**
     * The evaluate method
     *
     *
     * @param pageContext
     * @param icontext
     * @param scope
     *
     * @return
     *
     * @throws EvaluationException
     *
     */
    public Object evaluate(
            PageContext pageContext, IterationContext icontext, int scope)
                throws EvaluationException {

        Object result;

        try {
            JSPList leftSide =
                Convert.toJSPList(((Introspectable) jjtGetChild(0))
                    .evaluate(pageContext, icontext, scope));
            Predicate predicate = (Predicate) jjtGetChild(1);
            boolean oneItem = leftSide.applyPredicate(pageContext, predicate);

            if (oneItem) {
                if (leftSide.getLast() == 1) {
                    result = leftSide.next();
                } else {
                    result = null;
                }
            } else {
                result = leftSide;
            }
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return result;
    }

    /**
     * The evaluate method
     *
     *
     * @param pageContext
     * @param icontext
     * @param parent
     *
     * @return
     *
     * @throws EvaluationException
     *
     */
    public Object evaluate(
            PageContext pageContext, IterationContext icontext, Object parent)
                throws EvaluationException {

        Object result;

        try {
            JSPList leftSide =
                Convert.toJSPList(((Introspectable) jjtGetChild(0))
                    .evaluate(pageContext, icontext, parent));
            Predicate predicate = (Predicate) jjtGetChild(1);
            boolean oneItem = leftSide.applyPredicate(pageContext, predicate);

            if (oneItem) {
                if (leftSide.getLast() == 1) {
                    result = leftSide.next();
                } else {
                    result = null;
                }
            } else {
                result = leftSide;
            }
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return result;
    }
}
