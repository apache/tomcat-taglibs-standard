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

package org.apache.taglibs.standard.lang.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.taglibs.standard.lang.jstl.Coercions;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.Evaluator;
import org.apache.taglibs.standard.lang.jstl.Logger;

/**
 * <p>A conduit to the JSTL EL.  Based on...</p>
 * <p>An implementation of the ExpressionEvaluatorManager called for by
 * the JSTL rev1 draft.  This class is responsible for delegating a
 * request for expression evaluating to the particular, "active"
 * ExpressionEvaluator for the given point in the PageContext object
 * passed in.</p>
 *
 * @author Shawn Bayern
 */
public class ExpressionEvaluatorManager {

    //*********************************************************************
    // Constants

    public static final String EVALUATOR_CLASS = "org.apache.taglibs.standard.lang.jstl.Evaluator";

    //*********************************************************************
    // Internal, static state

    private static final ExpressionEvaluator EVALUATOR = new Evaluator();
    private static final ConcurrentMap<String, ExpressionEvaluator> nameMap =
            new ConcurrentHashMap<String, ExpressionEvaluator>();
    static {
        nameMap.put(EVALUATOR_CLASS, EVALUATOR);
    }

    private static final Logger logger = new Logger(System.out);

    /**
     * Invokes the evaluate() method on the "active" ExpressionEvaluator for the given pageContext.
     */
    public static Object evaluate(String attributeName,
                                  String expression,
                                  Class expectedType,
                                  Tag tag,
                                  PageContext pageContext)
            throws JspException {

        // delegate the call
        return (EVALUATOR.evaluate(attributeName, expression, expectedType, tag, pageContext));
    }

    /**
     * Invokes the evaluate() method on the "active" ExpressionEvaluator for the given pageContext.
     */
    public static Object evaluate(String attributeName,
                                  String expression,
                                  Class expectedType,
                                  PageContext pageContext)
            throws JspException {

        return evaluate(attributeName, expression, expectedType, null, pageContext);
    }

    /**
     * Gets an ExpressionEvaluator from the cache, or seeds the cache
     * if we haven't seen a particular ExpressionEvaluator before.
     */
    @Deprecated
    public static ExpressionEvaluator getEvaluatorByName(String name) throws JspException {
        try {
            ExpressionEvaluator evaluator = nameMap.get(name);
            if (evaluator == null) {
                nameMap.putIfAbsent(name, (ExpressionEvaluator) Class.forName(name).newInstance());
                evaluator = nameMap.get(name);
            }
            return evaluator;
        } catch (ClassCastException ex) {
            // just to display a better error message
            throw new JspException("invalid ExpressionEvaluator: " + name, ex);
        } catch (ClassNotFoundException ex) {
            throw new JspException("couldn't find ExpressionEvaluator: " + name, ex);
        } catch (IllegalAccessException ex) {
            throw new JspException("couldn't access ExpressionEvaluator: " + name, ex);
        } catch (InstantiationException ex) {
            throw new JspException("couldn't instantiate ExpressionEvaluator: " + name, ex);
        }
    }

    /**
     * Performs a type conversion according to the EL's rules.
     */
    public static Object coerce(Object value, Class classe) throws JspException {
        try {
            // just delegate the call
            return Coercions.coerce(value, classe, logger);
        } catch (ELException ex) {
            throw new JspException(ex);
        }
    }

    /**
     * Validates an expression.
     * @param attributeName the name of the attribute containing the expression
     * @param expression the expression to validate
     * @return null an error message or null of the expression is valid
     */
    public static String validate(String attributeName, String expression) {
        return EVALUATOR.validate(attributeName, expression);
    }
} 
