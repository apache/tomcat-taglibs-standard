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

import java.util.Calendar;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.ConversionException;
import org.apache.taglibs.standard.lang.jpath.adapter.Convert;
import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;
import org.apache.taglibs.standard.lang.jpath.adapter.JSPDate;

/**
 * The AddDateFunction class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class AddDateFunction extends SimpleNode {

    /**
     * Used to create an instance of the AddDateFunction class
     *
     *
     * @param id
     *
     */
    public AddDateFunction(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the AddDateFunction class
     *
     *
     * @param p
     * @param id
     *
     */
    public AddDateFunction(Parser p, int id) {
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

        boolean first = true;
        String normalized;

        normalized = "date-add(";

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                if (!first) {
                    normalized = normalized + ",";
                }

                first = false;

                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    normalized = normalized + n.toNormalizedString();
                }
            }
        }

        normalized = normalized + ")";

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

        JSPDate result;

        try {
            result = Convert.toJSPDate(jjtGetChild(0).evaluate(pageContext,
                    icontext));

            String arg2 =
                Convert.toString(jjtGetChild(1).evaluate(pageContext,
                    icontext));
            int arg3 = Convert.toDouble(jjtGetChild(2).evaluate(pageContext,
                           icontext)).intValue();
            int field = getCalendarConstant(arg2);

            result.add(field, arg3);
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return result;
    }

    /**
     * The getCalendarConstant method
     *
     *
     * @param field
     *
     * @return
     *
     * @throws EvaluationException
     *
     */
    private int getCalendarConstant(String field) throws EvaluationException {

        int result;

        if (field.equals("era")) {
            result = Calendar.ERA;
        } else if (field.equals("year")) {
            result = Calendar.YEAR;
        } else if (field.equals("month")) {
            result = Calendar.MONTH;
        } else if (field.equals("weekOfYear")) {
            result = Calendar.WEEK_OF_YEAR;
        } else if (field.equals("date")) {
            result = Calendar.DATE;
        } else if (field.equals("dayOfMonth")) {
            result = Calendar.DAY_OF_MONTH;
        } else if (field.equals("dayOfYear")) {
            result = Calendar.DAY_OF_YEAR;
        } else if (field.equals("dayOfWeek")) {
            result = Calendar.DAY_OF_WEEK;
        } else if (field.equals("dayOfWeekInMonth")) {
            result = Calendar.DAY_OF_WEEK_IN_MONTH;
        } else if (field.equals("amPm")) {
            result = Calendar.AM_PM;
        } else if (field.equals("hour")) {
            result = Calendar.HOUR;
        } else if (field.equals("hourOfDay")) {
            result = Calendar.HOUR_OF_DAY;
        } else if (field.equals("minute")) {
            result = Calendar.MINUTE;
        } else if (field.equals("second")) {
            result = Calendar.SECOND;
        } else if (field.equals("millisecond")) {
            result = Calendar.MILLISECOND;
        } else {
            throw new EvaluationException(this,
                    "An invalid date field was supplied");
        }

        return result;
    }
}
