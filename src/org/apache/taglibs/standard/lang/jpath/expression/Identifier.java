/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

/* +===================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
 *
 */ 

package org.apache.taglibs.standard.lang.jpath.expression;

import org.apache.taglibs.standard.lang.jpath.adapter.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.beans.*;
import java.lang.reflect.*;

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
