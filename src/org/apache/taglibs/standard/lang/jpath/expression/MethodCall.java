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
import java.beans.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;

/**
 * The MethodCall class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class MethodCall extends SimpleNode implements Introspectable {

    /**
     * Used to create an instance of the MethodCall class
     *
     *
     * @param id
     *
     */
    public MethodCall(int id) {
        super(id);
    }

    /**
     * Used to create an instance of the MethodCall class
     *
     *
     * @param p
     * @param id
     *
     */
    public MethodCall(Parser p, int id) {
        super(p, id);
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
        throw new EvaluationException(this,
                "A MethodCall must be called on " + "another object");
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
        String methodName = ((Identifier) jjtGetChild(0)).val;

        if (parent != null) {
            try {
                MethodDescriptor md = getFeatureDescriptor(parent.getClass(),
                                          methodName);
                Object[] args = new Object[jjtGetNumChildren() - 1];

                for (int i = 1; i < jjtGetNumChildren(); i++) {
                    args[i - 1] = jjtGetChild(i).evaluate(pageContext,
                            icontext);
                }

                if (md != null) {

                    //result = getAttribute(md, parent, args);
                    result = tempGetAttribute(parent, methodName, args);
                }
            } catch (IntrospectionException ie) {
                throw new EvaluationException(this,
                        "Introspection Exception:" + ie.getMessage());
            } catch (NoSuchMethodException nsme) {
                throw new EvaluationException(this,
                        "NoSuchMethodException:" + nsme.getMessage());
            } catch (IllegalAccessException iae) {
                throw new EvaluationException(this,
                        "IllegalAccessException:" + iae.getMessage());
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
    private MethodDescriptor getFeatureDescriptor(Class c, String key)
            throws IntrospectionException {

        BeanInfo beanInfo = Introspector.getBeanInfo(c);
        MethodDescriptor[] mda = beanInfo.getMethodDescriptors();

        for (int i = mda.length - 1; i >= 0; --i) {
            MethodDescriptor md = mda[i];

            if (md.getName().equals(key)) {
                return md;
            }
        }

        return null;
    }

    /**
     * The getAttribute method
     *
     *
     * @param md
     * @param o
     * @param args
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     *
     */
    private Object getAttribute(MethodDescriptor md, Object o, Object[] args)
            throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException {

        Object result = null;
        Method m = md.getMethod();

        result = m.invoke(o, args);

        return result;
    }

    /**
     * The tempGetAttribute method
     *
     *
     * @param parent
     * @param key
     * @param args
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     *
     */
    private Object tempGetAttribute(Object parent, String key, Object[] args)
            throws IntrospectionException, NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {

        Object result;
        Class c = parent.getClass();
        Method[] methods = c.getMethods();
        Method m = null;

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(key)) {
                m = methods[i];
            }
        }

        m = getPublicMethod(c, m.getName(), m.getParameterTypes());
        result = m.invoke(parent, args);

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
