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

/**
 * The ScopeLimitOperator class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class ScopeLimitOperator extends SimpleNode {

    /**
     * Used to create an instance of the ScopeLimitOperator class
     *
     *
     * @param id
     *
     */
    public ScopeLimitOperator(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the ScopeLimitOperator class
     *
     *
     * @param p
     * @param id
     *
     */
    public ScopeLimitOperator(Parser p, int id) {
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
        boolean properScope = false;
        int scope;

        try {
            String scopeLimit =
                Convert.toString(jjtGetChild(0).evaluate(pageContext,
                    icontext));

            //Map scopes to scope constants
            if (scopeLimit.equals("page:")) {
                properScope = true;
                scope = PageContext.PAGE_SCOPE;
            } else if (scopeLimit.equals("request:")) {
                properScope = true;
                scope = PageContext.REQUEST_SCOPE;
            } else if (scopeLimit.equals("session:")) {
                properScope = true;
                scope = PageContext.SESSION_SCOPE;
            } else if (scopeLimit.equals("application:")) {
                properScope = true;
                scope = PageContext.APPLICATION_SCOPE;
            } else {
                throw new EvaluationException("invalid scope operator ["
                        + scopeLimit + "]");
            }

            if (properScope) {
                result =
                    ((Introspectable) jjtGetChild(1)).evaluate(pageContext,
                        icontext, scope);
            } else {
                throw new EvaluationException("invalid scope operator ["
                        + scopeLimit + "]");
            }
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return result;
    }
}
