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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;

/**
 * The Identifier class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class Identifier extends SimpleNode implements Introspectable {

    protected String val;

    /**
     * Used to create an instance of the Identifier class
     *
     *
     * @param id
     *
     */
    public Identifier(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the Identifier class
     *
     *
     * @param p
     * @param id
     *
     */
    public Identifier(Parser p, int id) {
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
        return val;
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

        //try {
        //result = Convert.toJSPType(pageContext.findAttribute(val));
        //} catch (ConversionException ce) {
        //throw new EvaluationException(this, ce.getMessage());
        //}
        result = pageContext.findAttribute(val);

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

        //try {
        //result = Convert.toJSPType(pageContext.getAttribute(val, scope));
        //} catch (ConversionException ce) {
        //throw new EvaluationException(this, ce.getMessage());
        //}
        result = pageContext.getAttribute(val, scope);

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

        Object result = null;

        //try {
        if (parent != null) {
            try {
                PropertyDescriptor pd =
                    getFeatureDescriptor(parent.getClass(), val);

                if (pd != null) {
                    result = getAttribute(pd, parent);
                }
            } catch (IntrospectionException ie) {
                throw new EvaluationException(this,
                        "Introspection Exception:" + ie.getMessage());
            } catch (NoSuchMethodException nsme) {
                throw new EvaluationException(this,
                        "NoSuchMethodException:" + nsme.getMessage());
            } catch (IllegalAccessException iae) {
                throw new EvaluationException(this,
                        "IllegalAccessException:" + iae.toString());
            } catch (InvocationTargetException ite) {
                throw new EvaluationException(this,
                        "InvocationTargetException:" + ite.getMessage());
            }
        }

        //result = Convert.toJSPType(result);
        //} catch (ConversionException ce) {
        //throw new EvaluationException(this, ce.getMessage());
        //}
        return result;
    }

    /**
     * The getFeatureDescriptor method
     *
     *
     * @param c
     * @param key
     *
     * @return
     *
     * @throws IntrospectionException
     *
     */
    private PropertyDescriptor getFeatureDescriptor(Class c, String key)
            throws IntrospectionException {

        BeanInfo beanInfo = Introspector.getBeanInfo(c);
        PropertyDescriptor[] pda = beanInfo.getPropertyDescriptors();

        for (int i = pda.length - 1; i >= 0; --i) {
            PropertyDescriptor pd = pda[i];

            if (pd.getName().equals(key)) {
                return pd;
            }
        }

        return null;
    }

    /**
     * The getAttribute method
     *
     *
     * @param pd
     * @param o
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     *
     */
    private Object getAttribute(PropertyDescriptor pd, Object o)
            throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException {

        Object result = null;
        Method m = pd.getReadMethod();

        m = getPublicMethod(m.getDeclaringClass(), m.getName(),
                m.getParameterTypes());
        result = m.invoke(o, null);

        return result;
    }

    /**
     * The getPublicMethod method
     *
     *
     * @param c
     * @param name
     * @param paramTypes
     *
     * @return
     *
     * @throws NoSuchMethodException
     *
     */
    private Method getPublicMethod(Class c, String name, Class[] paramTypes)
            throws NoSuchMethodException {

        Method result = null;

        if ((c.getModifiers() & Modifier.PUBLIC) == 0) {
            Class sc = c.getSuperclass();

            if (sc != null) {
                try {
                    result = getPublicMethod(sc, name, paramTypes);
                } catch (NoSuchMethodException nsme) {

                    //Intentionally ignored and thrown later
                }
            }

            if (result == null) {
                Class[] interfaces = c.getInterfaces();

                for (int i = 0; i < interfaces.length; i++) {
                    try {
                        result = getPublicMethod(interfaces[i], name,
                                paramTypes);
                    } catch (NoSuchMethodException nsme) {

                        //Intentionally ignored and thrown later
                    }
                }
            }
        } else {

            //It was public
            result = c.getMethod(name, paramTypes);
        }

        return result;
    }
}
