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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

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
