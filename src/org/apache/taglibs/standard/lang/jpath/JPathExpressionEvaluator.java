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

package org.apache.taglibs.standard.lang.jpath;

import org.apache.taglibs.standard.lang.jpath.adapter.*;
import org.apache.taglibs.standard.lang.jpath.expression.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*; 
import org.apache.taglibs.standard.lang.support.*;
import javax.servlet.jsp.jstl.core.LoopTag;


public class JPathExpressionEvaluator implements ExpressionEvaluator {
    /** 
     * Translation time validation of an expression. 
     * This method will return a null String if the expression 
     * is valid; otherwise an error message. 
     */ 
    public String validate(String attributeName, 
                               String expression) {
        String result = null;
        try {
	    if (expression.startsWith("$")) {
	      // skip '$'
              Expression exp = Parser.parse(expression.substring(1));
              exp.validate();				// expression
	    } else
	      return null;				// literal
        } catch (ParseException e) {
            result = "The attribute for " + attributeName + " was not "
                    + "valid.\n" + e.getMessage();
        } catch (ValidationException e) {
            result = "The attribute for " + attributeName + " was not "
                    + "valid.\n" + e.getMessage();
        }
        return result;
    }

    /** 
     * Evaluates the expression at request time. 
     */ 
    public Object evaluate(String attributeName, 
                           String expression, 
                           Class expectedType, 
                           Tag tag, 
                           PageContext pageContext) 
                           throws JspException {
        Object result = null;
        IterationContext icontext = null;

        LoopTag parent = (LoopTag) TagSupport.findAncestorWithClass(tag, LoopTag.class);
        if (parent == null) {
            icontext = null;
        } else {
            icontext = new StatusIterationContext(parent.getIteratorStatus());
        }

        try {
	    if (expression.startsWith("$")) {
	      // skip '$'
              Expression exp = Parser.parse(expression.substring(1));
              result = exp.evaluate(pageContext, icontext);	// exp.
	    } else
	      result = new String(expression);			// lit.
        } catch (ParseException e) {
            throw new JspException(e.getMessage(), e);
        } catch (EvaluationException e) {
            throw new JspException(e.getMessage(), e);
        }
        if (result != null && !expectedType.isInstance(result)) {
            throw new JspException("The tag expected an object of type ["
                    + expectedType.getName() + "] for the " + attributeName
                    + " attribute.  However, it received an "
                    + "object of type [" + result.getClass().getName() + "]");
        }
        return result;
    }
}
