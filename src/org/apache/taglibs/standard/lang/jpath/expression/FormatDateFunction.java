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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.ConversionException;
import org.apache.taglibs.standard.lang.jpath.adapter.Convert;
import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;
import org.apache.taglibs.standard.lang.jpath.adapter.JSPDate;

/**
 * The FormatDateFunction class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class FormatDateFunction extends SimpleNode {

    /**
     * Used to create an instance of the FormatDateFunction class
     *
     *
     * @param id
     *
     */
    public FormatDateFunction(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the FormatDateFunction class
     *
     *
     * @param p
     * @param id
     *
     */
    public FormatDateFunction(Parser p, int id) {
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

        normalized = "format-date(" + jjtGetChild(0).toNormalizedString()
                + jjtGetChild(1).toNormalizedString() + "," + ")";

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

        String formatted;

        try {
            JSPDate arg =
                Convert.toJSPDate(jjtGetChild(0).evaluate(pageContext,
                    icontext));
            String pattern =
                Convert.toString(jjtGetChild(1).evaluate(pageContext,
                    icontext));
            DateFormat form;

            if (jjtGetNumChildren() > 2) {
                String arg3 =
                    Convert.toString(jjtGetChild(2).evaluate(pageContext,
                        icontext));
                Locale locale = getLocale(arg3);

                form = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            } else {
                form = DateFormat.getInstance();
            }

            try {
                ((SimpleDateFormat) form).applyPattern(pattern);

                formatted = form.format(new Date(arg.getTime().longValue()));
            } catch (IllegalArgumentException iae) {
                formatted = new String("");
            }
        } catch (ConversionException ce) {
            throw new EvaluationException(this, ce.getMessage());
        }

        return formatted;
    }

    /**
     * The getLocale method
     *
     *
     * @param arg
     *
     * @return
     *
     */
    private Locale getLocale(String arg) {

        Locale result;
        StringTokenizer st = new StringTokenizer(arg, "_");
        String language = null;
        String country = null;
        String variant = null;

        if (st.hasMoreTokens()) {
            language = st.nextToken();

            if (st.hasMoreTokens()) {
                country = st.nextToken();

                if (st.hasMoreTokens()) {
                    variant = st.nextToken();
                    result = new Locale(language, country, variant);
                } else {
                    result = new Locale(language, country);
                }
            } else {
                result = new Locale(language, "");
            }
        } else {
            result = Locale.getDefault();
        }

        return result;
    }
}
