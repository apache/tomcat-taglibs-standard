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

package org.apache.taglibs.standard.lang.javascript;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*; 
import java.util.Enumeration;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluator;
import org.apache.taglibs.standard.lang.spel.Evaluator;
import org.mozilla.javascript.*;



public class JavascriptExpressionEvaluator implements ExpressionEvaluator {

    /** 
     * Translation time validation of an expression. 
     * This method will return a null String if the expression 
     * is valid; otherwise an error message. 
     */ 
    public String validate(String attributeName, 
                           String expression) {
        return null;
    }

    /** 
     * Evaluates the expression at request time. 
     * Rhino is the java implementation of JavaScript.
     *
     */ 
    public Object evaluate(String attributeName, 
                           String expression, 
                           Class expectedType, 
                           Tag tag, 
                           PageContext pageContext) 
                           throws JspException {

        Object result = null;
        Evaluator evalLiteral = new Evaluator();

	// Creates and enters a Context. Context stores information
	// about the execution environment of a script
	Context cx = Context.enter();

	// Initialize standard objects
	Scriptable scope = cx.initStandardObjects(null);

        // Put PageContext attributes/parameters in Rhino Scope
        putAttributesInScope(scope, cx, pageContext, PageContext.PAGE_SCOPE);
        putAttributesInScope(scope, cx, pageContext, PageContext.REQUEST_SCOPE);
        putAttributesInScope(scope, cx, pageContext, PageContext.SESSION_SCOPE);
        putAttributesInScope(scope, cx, pageContext, PageContext.APPLICATION_SCOPE);
        putParametersInScope(scope, cx, pageContext);


	// Evaluate string
        try {
            // skip $, use the SPEL evaluate literal method 'cuz why reinvent the wheel?
            if (expression.startsWith("$")) {
                expression = expression.substring(1);
	        result = cx.evaluateString(scope, expression, "", 0, null);    // expression
            }
	    else result = evalLiteral.evaluateLiteral(expression, pageContext, expectedType);    //literal

            // Unwrap scoped object
            if (result instanceof Wrapper)
                result = ((Wrapper) result).unwrap();

            if (result instanceof NativeString)
                result = result.toString();

	} catch (JavaScriptException jse) {
	    throw new JspException(jse.getMessage());
	}

	//System.out.println("RHINO result: " + result + ":");

        if (result != null && !expectedType.isInstance(result)) {
            throw new JspException("The tag expected an object of type ["
                    + expectedType.getName() + "] for the " + attributeName
                    + " attribute.  However, it received an "
                    + "object of type [" + result.getClass().getName() + "]");
        }

        return result;
    }

    /**
     * put PageContext attributes into Rhino scope
     */
    void putAttributesInScope(Scriptable rhinoScope, 
                              Context rhinoContext, 
                              PageContext pageContext, 
                              int scope) {
        Enumeration attributes = null;
        Object value = null;
        String attribute = null;
        
        attributes = pageContext.getAttributeNamesInScope(scope);
        while (attributes !=null && attributes.hasMoreElements()) {
            attribute = (String)attributes.nextElement();
            value = pageContext.getAttribute(attribute, scope);
            rhinoScope.put(attribute, rhinoScope, rhinoContext.toObject(value, rhinoScope));
        }
    }

    /**
     * put PageContext parameters into Rhino scope
     */
    void putParametersInScope(Scriptable rhinoScope, 
                              Context rhinoContext, 
                              PageContext pageContext) {
        Enumeration attributes = null;
        Object value = null;
        String attribute = null;
        
        attributes = (pageContext.getRequest()).getParameterNames();
        while (attributes !=null && attributes.hasMoreElements()) {
            attribute = (String)attributes.nextElement();
            value = (pageContext.getRequest()).getParameter(attribute);
            rhinoScope.put(attribute, rhinoScope, rhinoContext.toObject(value, rhinoScope));
        }
    }
}
