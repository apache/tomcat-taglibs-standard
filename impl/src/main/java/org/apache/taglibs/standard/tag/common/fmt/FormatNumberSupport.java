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

package org.apache.taglibs.standard.tag.common.fmt;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;

/**
 * Support for tag handlers for &lt;formatNumber&gt;, the number
 * formatting tag in JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class FormatNumberSupport extends BodyTagSupport {

    //*********************************************************************
    // Private constants

    private static final Class[] GET_INSTANCE_PARAM_TYPES =
            new Class[]{String.class};
    private static final String NUMBER = "number";
    private static final String CURRENCY = "currency";
    private static final String PERCENT = "percent";


    //*********************************************************************
    // Protected state

    protected Object value;                    // 'value' attribute
    protected boolean valueSpecified;           // status
    protected String type;                     // 'type' attribute
    protected String pattern;                  // 'pattern' attribute
    protected String currencyCode;             // 'currencyCode' attribute
    protected String currencySymbol;           // 'currencySymbol' attribute
    protected boolean isGroupingUsed;          // 'groupingUsed' attribute
    protected boolean groupingUsedSpecified;
    protected int maxIntegerDigits;            // 'maxIntegerDigits' attribute
    protected boolean maxIntegerDigitsSpecified;
    protected int minIntegerDigits;            // 'minIntegerDigits' attribute
    protected boolean minIntegerDigitsSpecified;
    protected int maxFractionDigits;           // 'maxFractionDigits' attribute
    protected boolean maxFractionDigitsSpecified;
    protected int minFractionDigits;           // 'minFractionDigits' attribute
    protected boolean minFractionDigitsSpecified;


    //*********************************************************************
    // Private state

    private String var;                        // 'var' attribute
    private int scope;                         // 'scope' attribute
    private static Class currencyClass;


    //*********************************************************************
    // Constructor and initialization

    static {
        try {
            currencyClass = Class.forName("java.util.Currency");
            // container's runtime is J2SE 1.4 or greater
        } catch (Exception cnfe) {
        }
    }

    public FormatNumberSupport() {
        super();
        init();
    }

    private void init() {
        value = type = null;
        valueSpecified = false;
        pattern = var = currencyCode = currencySymbol = null;
        groupingUsedSpecified = false;
        maxIntegerDigitsSpecified = minIntegerDigitsSpecified = false;
        maxFractionDigitsSpecified = minFractionDigitsSpecified = false;
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag attributes known at translation time

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Tag logic

    @Override
    public int doEndTag() throws JspException {
        String formatted = null;
        Object input = null;

        // determine the input by...
        if (valueSpecified) {
            // ... reading 'value' attribute
            input = value;
        } else {
            // ... retrieving and trimming our body
            if (bodyContent != null && bodyContent.getString() != null) {
                input = bodyContent.getString().trim();
            }
        }

        if ((input == null) || input.equals("")) {
            // Spec says:
            // If value is null or empty, remove the scoped variable 
            // if it is specified (see attributes var and scope).
            if (var != null) {
                pageContext.removeAttribute(var, scope);
            }
            return EVAL_PAGE;
        }

        /*
       * If 'value' is a String, it is first parsed into an instance of
       * java.lang.Number
       */
        if (input instanceof String) {
            try {
                if (((String) input).indexOf('.') != -1) {
                    input = Double.valueOf((String) input);
                } else {
                    input = Long.valueOf((String) input);
                }
            } catch (NumberFormatException nfe) {
                throw new JspException(
                        Resources.getMessage("FORMAT_NUMBER_PARSE_ERROR", input),
                        nfe);
            }
        }

        // Determine formatting locale
        Locale loc = SetLocaleSupport.getFormattingLocale(
                pageContext,
                this,
                true,
                SetLocaleSupport.numberLocales);

        if (loc != null) {
            // Create formatter
            NumberFormat formatter = null;
            if ((pattern != null) && !pattern.equals("")) {
                // if 'pattern' is specified, 'type' is ignored
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(loc);
                formatter = new DecimalFormat(pattern, symbols);
            } else {
                formatter = createFormatter(loc);
            }
            if (((pattern != null) && !pattern.equals(""))
                    || CURRENCY.equalsIgnoreCase(type)) {
                try {
                    setCurrency(formatter);
                } catch (Exception e) {
                    throw new JspException(
                            Resources.getMessage("FORMAT_NUMBER_CURRENCY_ERROR"),
                            e);
                }
            }
            configureFormatter(formatter);
            formatted = formatter.format(input);
        } else {
            // no formatting locale available, use toString()
            formatted = input.toString();
        }

        if (var != null) {
            pageContext.setAttribute(var, formatted, scope);
        } else {
            try {
                pageContext.getOut().print(formatted);
            } catch (IOException ioe) {
                throw new JspTagException(ioe.toString(), ioe);
            }
        }

        return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)

    @Override
    public void release() {
        init();
    }


    //*********************************************************************
    // Private utility methods

    private NumberFormat createFormatter(Locale loc) throws JspException {
        NumberFormat formatter = null;

        if ((type == null) || NUMBER.equalsIgnoreCase(type)) {
            formatter = NumberFormat.getNumberInstance(loc);
        } else if (CURRENCY.equalsIgnoreCase(type)) {
            formatter = NumberFormat.getCurrencyInstance(loc);
        } else if (PERCENT.equalsIgnoreCase(type)) {
            formatter = NumberFormat.getPercentInstance(loc);
        } else {
            throw new JspException(
                    Resources.getMessage("FORMAT_NUMBER_INVALID_TYPE", type));
        }

        return formatter;
    }

    /*
     * Applies the 'groupingUsed', 'maxIntegerDigits', 'minIntegerDigits',
     * 'maxFractionDigits', and 'minFractionDigits' attributes to the given
     * formatter.
     */

    private void configureFormatter(NumberFormat formatter) {
        if (groupingUsedSpecified) {
            formatter.setGroupingUsed(isGroupingUsed);
        }
        if (maxIntegerDigitsSpecified) {
            formatter.setMaximumIntegerDigits(maxIntegerDigits);
        }
        if (minIntegerDigitsSpecified) {
            formatter.setMinimumIntegerDigits(minIntegerDigits);
        }
        if (maxFractionDigitsSpecified) {
            formatter.setMaximumFractionDigits(maxFractionDigits);
        }
        if (minFractionDigitsSpecified) {
            formatter.setMinimumFractionDigits(minFractionDigits);
        }
    }

    /*
     * Override the formatting locale's default currency symbol with the
     * specified currency code (specified via the "currencyCode" attribute) or
     * currency symbol (specified via the "currencySymbol" attribute).
     *
     * If both "currencyCode" and "currencySymbol" are present,
     * "currencyCode" takes precedence over "currencySymbol" if the
     * java.util.Currency class is defined in the container's runtime (that
     * is, if the container's runtime is J2SE 1.4 or greater), and
     * "currencySymbol" takes precendence over "currencyCode" otherwise.
     *
     * If only "currencyCode" is given, it is used as a currency symbol if
     * java.util.Currency is not defined.
     *
     * Example:
     *
     * JDK    "currencyCode" "currencySymbol" Currency symbol being displayed
     * -----------------------------------------------------------------------
     * all         ---            ---         Locale's default currency symbol
     *
     * <1.4        EUR            ---         EUR
     * >=1.4       EUR            ---         Locale's currency symbol for Euro
     *
     * all         ---           \u20AC       \u20AC
     * 
     * <1.4        EUR           \u20AC       \u20AC
     * >=1.4       EUR           \u20AC       Locale's currency symbol for Euro
     */

    private void setCurrency(NumberFormat formatter) throws Exception {
        String code = null;
        String symbol = null;

        if ((currencyCode == null) && (currencySymbol == null)) {
            return;
        }

        if ((currencyCode != null) && (currencySymbol != null)) {
            if (currencyClass != null) {
                code = currencyCode;
            } else {
                symbol = currencySymbol;
            }
        } else if (currencyCode == null) {
            symbol = currencySymbol;
        } else {
            if (currencyClass != null) {
                code = currencyCode;
            } else {
                symbol = currencyCode;
            }
        }

        if (code != null) {
            Currency currency = Currency.getInstance( code );
            formatter.setCurrency( currency );

        } else {
            /*
            * Let potential ClassCastException propagate up (will almost
            * never happen)
            */
            DecimalFormat df = (DecimalFormat) formatter;
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setCurrencySymbol(symbol);
            df.setDecimalFormatSymbols(dfs);
        }
    }
}
