/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.taglibs.standard.lang.jpath.adapter;

import org.apache.taglibs.standard.lang.jpath.expression.*;
import java.text.*;
import java.util.*;

public class Convert {

    public static Double toDouble(Object o) throws ConversionException {
        Double converted;
        o = performBasicConversions(o);
        if (o == null) {
            converted = new Double(Double.NaN);
        } else if (o instanceof Boolean) {
            converted = toDouble((Boolean)o);
        } else if (o instanceof Double) {
            converted = toDouble((Double)o);
        } else if (o instanceof String) {
            converted = toDouble((String)o);
        } else if (o instanceof JSPDate) {
            converted = toDouble((JSPDate)o);
        } else if (o instanceof JSPList) {
            converted = toDouble((JSPList)o);
        } else {
            converted = toDouble(Convert.toString(o));
            //throw new ConversionException("Object [" + o + "] cannot be "
                    //+ "converted to a Double");
        }
        return converted;
    }

    private static Double toDouble(Boolean b) throws ConversionException {
        Double converted;
        if (b.booleanValue() == true) {
            converted = new Double(1); 
        } else {
            converted = new Double(0); 
        }
        return converted;
    }

    private static Double toDouble(Double d) throws ConversionException {
        return d;
    }

    private static Double toDouble(String s) throws ConversionException {
        Double converted;
        try {
            converted = Double.valueOf(s);
        } catch (NumberFormatException nfe) {
            converted = new Double(Double.NaN);
        }
        return converted;
    }

    private static Double toDouble(JSPDate d) throws ConversionException {
        Double converted;
        converted = d.getTime();
        return converted;
    }


    private static Double toDouble(JSPList list) throws ConversionException {
        Double converted;
        converted = Convert.toDouble(Convert.toString(list));
        return converted;
    }


    public static Boolean toBoolean(Object o) throws ConversionException {
        Boolean converted;
        o = performBasicConversions(o);
        if (o == null) {
            converted = new Boolean(false);
        } else if (o instanceof Boolean) {
            converted = toBoolean((Boolean)o);
        } else if (o instanceof Double) {
            converted = toBoolean((Double)o);
        } else if (o instanceof String) {
            converted = toBoolean((String)o);
        } else if (o instanceof JSPDate) {
            converted = toBoolean((JSPDate)o);
        } else if (o instanceof JSPList) {
            converted = toBoolean((JSPList)o);
        } else {
            converted = toBoolean(Convert.toString(o));
            //throw new ConversionException("Object [" + o + "] cannot be "
                    //+ "converted to a Boolean");
        }
        return converted;
    }

    private static Boolean toBoolean(Boolean b) throws ConversionException {
        return b;
    }

    private static Boolean toBoolean(Double d) throws ConversionException {
        Boolean converted;
        if (d == null) {
            converted = new Boolean(false);
        } else if (!d.isNaN() && d.doubleValue() != 0 && d.doubleValue() != -0) {
            converted = new Boolean(true);
        } else {
            converted = new Boolean(false);
        }
        return converted;
    }

    private static Boolean toBoolean(String s) throws ConversionException {
        Boolean converted;
        if (s == null) {
            converted = new Boolean(false);
        } else if (s.length() != 0) {
            converted = new Boolean(true);
        } else {
            converted = new Boolean(false);
        }
        return converted;
    }

    private static Boolean toBoolean(JSPDate d) throws ConversionException {
        Boolean converted;
        converted = Convert.toBoolean(Convert.toString(d));
        return converted;
    }

    private static Boolean toBoolean(JSPList list) throws ConversionException {
        Boolean converted;
        if (list == null || !list.hasNext()) {
            converted = new Boolean(false);
        } else {
            if (list.getPosition() != 0) {
                throw new ConversionException("cannot convert a list to "
                        + "a Boolean if the list is not at the starting "
                        + "position" );
            }
            converted = new Boolean(true);
        }
        return converted;
    }


    public static String toString(Object o) throws ConversionException {
        String converted;
        o = performBasicConversions(o);
        if (o == null) {
            converted = new String("");
        } else if (o instanceof Boolean) {
            converted = toString((Boolean)o);
        } else if (o instanceof Double) {
            converted = toString((Double)o);
        } else if (o instanceof String) {
            converted = toString((String)o);
        } else if (o instanceof JSPDate) {
            converted = toString((JSPDate)o);
        } else if (o instanceof JSPList) {
            converted = toString((JSPList)o);
        } else {
            converted = o.toString();
            //throw new ConversionException("Object [" + o + "] cannot be "
                    //+ "converted to a String");
        }
        return converted;
    }

    private static String toString(Boolean b) throws ConversionException {
        return b.toString();
    }

    private static String toString(Double d) throws ConversionException {
        String converted;
        if (d == null || d.isNaN()) {
            converted = new String("NaN");
        } else if (d.doubleValue() == 0 || d.doubleValue() == -0) {
            converted = new String("0");
        } else if (d.doubleValue() == Double.POSITIVE_INFINITY) {
            converted = new String("Infinity");
        } else if (d.doubleValue() == Double.NEGATIVE_INFINITY) {
            converted = new String("-Infinity");
        } else if (Math.floor(d.doubleValue()) == d.doubleValue()) {
            NumberFormat form;
            form = NumberFormat.getInstance();
            try {
                ((DecimalFormat)form).applyPattern("#");
                converted = form.format(d);
            } catch (IllegalArgumentException iae) { 
                converted = new String("NaN");
            }
        } else {
            converted = d.toString();
        }
        return converted;
    }

    private static String toString(String s) throws ConversionException {
        return s;
    }

    private static String toString(JSPDate d) throws ConversionException {
        String converted;
        converted = DateFormat.getDateInstance().format(new Date(d.getTime().longValue()));
        return converted;
    }

    private static String toString(JSPList list) throws ConversionException {
        String converted;
        if (list == null || !list.hasNext()) {
            converted = "";
        } else {
            if (list.getPosition() != 0) {
                throw new ConversionException("cannot convert a list to "
                        + "a String if the list is not at the starting "
                        + "position" );
            }
            converted = Convert.toString(list.next());
        }
        return converted;
    }

    public static JSPDate toJSPDate(Object o) throws ConversionException {
        JSPDate converted;
        o = performBasicConversions(o);
        if (o == null) {
            throw new ConversionException("cannot convert null to a JSPDate");
        } else if (o instanceof JSPDate) {
            converted = toJSPDate((JSPDate)o);
        } else {
            throw new ConversionException("Object [" + o + "] cannot be "
                    + "converted to a JSPList");
        }
        return converted;
    }

    private static JSPDate toJSPDate(JSPDate date) throws ConversionException {
        return date;
    }

    private static JSPDate toJSPDate(GregorianCalendar gc) throws ConversionException {
        return (JSPDate)GregorianCalendarAdapter.adapt(gc);
    }

    public static JSPList toJSPList(Object o) throws ConversionException {
        JSPList converted;
        o = performBasicConversions(o);
        if (o == null) {
            throw new ConversionException("cannot convert null to a JSPList");
        } else if (o instanceof JSPList) {
            converted = toJSPList((JSPList)o);
        } else {
            throw new ConversionException("Object [" + o + "] cannot be "
                    + "converted to a JSPList");
        }
        return converted;
    }

    private static JSPList toJSPList(JSPList list) throws ConversionException {
        return list;
    }

    private static Object performBasicConversions(Object o) {
        Object converted;
        if (o == null) {
            converted = null;
        } else if (o instanceof Short) {
            converted = new Double(((Short)o).toString());
        } else if (o instanceof Integer) {
            converted = new Double(((Integer)o).toString());
        } else if (o instanceof Float) {
            converted = new Double(((Float)o).toString());
        } else if (o instanceof Long) {
            converted = new Double(((Long)o).toString());
        } else if (o instanceof Byte) {
            converted = new String(((Byte)o).toString());
        } else if (o instanceof Character) {
            converted = new String(((Character)o).toString());
        } else {
            converted = o;
        }
        converted = Convert.toJSPType(converted);
        return converted;
    }

    public static Object toJSPType(Object o) {
        Object converted;
        if (o == null) {
            converted = null;
        } else if (o instanceof Collection) {
            converted = CollectionAdapter.adapt(o);
        } else if (o instanceof GregorianCalendar) {
            converted = GregorianCalendarAdapter.adapt(o);
        } else {
            converted = o;
        }
        return converted;
    }

}
