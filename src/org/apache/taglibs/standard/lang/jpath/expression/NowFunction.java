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

import java.util.GregorianCalendar;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.GregorianCalendarAdapter;
import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;

/**
 * The NowFunction class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class NowFunction extends SimpleNode {

    /**
     * Used to create an instance of the NowFunction class
     *
     *
     * @param id
     *
     */
    public NowFunction(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the NowFunction class
     *
     *
     * @param p
     * @param id
     *
     */
    public NowFunction(Parser p, int id) {
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

        normalized = "now()";

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
        return GregorianCalendarAdapter.adapt(new GregorianCalendar());
    }
}
