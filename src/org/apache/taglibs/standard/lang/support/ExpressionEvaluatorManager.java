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

package org.apache.taglibs.standard.lang.support;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * <p>An implementation of the ExpressionEvaluatorManager called for by
 * the JSTL rev1 draft.  This class is responsible for delegating a
 * request for expression evaluating to the particular, "active"
 * ExpressionEvaluator for the given point in the PageContext object
 * passed in.</p>
 *
 * <p><b>WARNING</b>:  This class supports experimentation for the EA2
 * release of JSTL; it is not expected to be part of the final RI or
 * specification.</p>
 *
 * @author Shawn Bayern
 */
public class ExpressionEvaluatorManager { 

    //*********************************************************************
    // Implementation overview

    /*
     * We use a simple static table that indexes the currently active
     * ExpressionEvaluator (EE) by a PageContext object.  Our strategy
     * is straightforward:  we keep a java.util.Stack of
     * ExpressionEvaluator objects for each PageContext, and if the
     * stack is empty, we use the context parameter called for by the
     * spec.
     */

    //*********************************************************************
    // Constants

    public static final String DEFAULT_EVALUATOR_CLASS =
        "org.apache.taglibs.standard.lang.javascript.JavascriptExpressionEvaluator";
    private static final String EVALUATOR_PARAMETER =
        "javax.servlet.jsp.jstl.temp.ExpressionEvaluatorClass";

    //*********************************************************************
    // Internal, static state

    /** The static table */
    private static HashMap eeTab = new HashMap();

    /** A separate static table to avoid unnecessary duplication of objects */
    private static HashMap nameMap = new HashMap();


    //*********************************************************************
    // Public static methods

    /**
     * Establishes the given ExpressionEvaluator as the "active" evaluator,
     * preserving the stack of formerly actiev evaluators underneath it.
     */
    public static synchronized void pushEvaluator(
                PageContext pageContext,
                String expressionEvaluatorName)
            throws JspException {

        // we'll use these throughout...
        Stack s;
        ExpressionEvaluator e;

        // create a Stack if one doesn't exist for our page
        Object oStack = eeTab.get(pageContext);
        if (oStack == null) {
            s = new Stack();
            eeTab.put(pageContext, s);
        } else
            s = (Stack) oStack;

        // "install" the evaluator
        e = getEvaluatorByName(expressionEvaluatorName);
        s.push(e);
    }


    /**
     * Retires the most recent ExpressionEvaluator from "activity," restoring
     * the one immediately preceding it on the stack.
     */
    public static synchronized void popEvaluator(PageContext pageContext) {

        // sanity check
        Object oStack = eeTab.get(pageContext);
        if (oStack == null) {
            throw new IllegalStateException("popEvaluator() called " +
                "on empty stack");
        }

        // remove the most recent evaluator
        Stack s = (Stack) oStack;
        s.pop();

        // if there are none left, forget about the stack
        if (s.empty())
            eeTab.remove(pageContext);
    }


    /** 
     * Invokes the evaluate() method on the "active" ExpressionEvaluator
     * for the given pageContext.
     */ 
    public static Object evaluate(String attributeName, 
                                  String expression, 
                                  Class expectedType, 
                                  Tag tag, 
                                  PageContext pageContext) 
           throws JspException
    {

        // the evaluator we'll use
        ExpressionEvaluator target;

        // figure out what evaluator to use, under a static lock
        synchronized (ExpressionEvaluatorManager.class) {
            Object oStack = eeTab.get(pageContext);
            if (oStack == null || ((Stack) oStack).empty()) {
                String name = pageContext.getServletContext().
                    getInitParameter(EVALUATOR_PARAMETER);
		if (name == null) {
		    target = getEvaluatorByName(DEFAULT_EVALUATOR_CLASS);
		    //throw new JspException("request to evaluate " +
                    //    "expression but no expression language defined " +
                    //    "(expression: '" + expression + "'; " +
                    //    "attribute name: '" + attributeName + "')");
		    if (target == null)
			throw new JspException(
			    "error loading default evaluator");
                } else
		    target = getEvaluatorByName(name);
            } else {
                Stack s = (Stack) oStack;
                target = (ExpressionEvaluator) s.peek();
            }
        }

        // delegate the call
        return (target.evaluate(
            attributeName, expression, expectedType, tag, pageContext));
    }


    //*********************************************************************
    // Public static utility method (un-spec'd, local to RI)

    /**
     * Gets an ExpressionEvaluator from the cache, or seeds the cache
     * if we haven't seen a particular ExpressionEvaluator before.
     */
    public static synchronized
	    ExpressionEvaluator getEvaluatorByName(String name)
            throws JspException {
        try {

            Object oEvaluator = nameMap.get(name);
            if (oEvaluator == null) {
                ExpressionEvaluator e = (ExpressionEvaluator)
                    Class.forName(name).newInstance();
                nameMap.put(name, e);
                return (e);
            } else
                return ((ExpressionEvaluator) oEvaluator);

        } catch (ClassCastException ex) {
            // just to display a better error message
            throw new JspException("invalid ExpressionEvaluator: " +
                ex.getMessage());
        } catch (ClassNotFoundException ex) {
            throw new JspException("couldn't find ExpressionEvaluator: " +
                ex.getMessage());
        } catch (IllegalAccessException ex) {
            throw new JspException("couldn't access ExpressionEvaluator: " +
                ex.getMessage());
        } catch (InstantiationException ex) {
            throw new JspException(
                "couldn't instantiate ExpressionEvaluator: " +
                ex.getMessage());
        }
    }

} 
