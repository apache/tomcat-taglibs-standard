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

package org.apache.taglibs.standard.lang.spel;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.spel.parser.ParseException;
import org.apache.taglibs.standard.lang.spel.parser.SpelParser;
import org.apache.taglibs.standard.lang.spel.parser.TokenMgrError;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluator;

/**
 *
 * <p>This is the main class for evaluating expressions.  If the
 * expression starts with "$", then it is evaluated as an SPEL
 * expression.  Otherwise, it is parsed into its expected type using
 * the PropertyEditor mechanism.
 *
 * <p>The evaluator maintains a cache mapping expression strings to
 * ParsedExpressions.  It also maintains a cache mapping
 * ExpectedType/LiteralString to parsed literal values.
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class Evaluator
  implements ExpressionEvaluator
{
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Member variables
  //-------------------------------------

  // The mapping from expression to ParsedExpression
  static Map mCachedExpressions = 
    Collections.synchronizedMap (new HashMap ());

  // The mapping from ExpectedType to Maps mapping literal String to
  // parsed value
  static Map mCachedExpectedTypes = new HashMap ();

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public Evaluator ()
  {
  }

  //-------------------------------------
  /**
   *
   * Evaluates the given expression as either an SPEL expression (if
   * it starts with $) or a literal value converted to the specified
   * expected type
   **/
  public Object evaluate (String pExpression,
			  PageContext pPageContext,
			  Class pExpectedType)
    throws JspException
  {
    // Check for null expression
    if (pExpression == null) {
      throw new SpelException
	(Constants.NULL_EXPRESSION);
    }

    // See if the expression starts with $
    if (pExpression.startsWith ("$")) {
      return evaluateExpression (pExpression,
				 pPageContext,
				 pExpectedType);
    }
    else {
      return evaluateLiteral (pExpression,
			      pPageContext,
			      pExpectedType);
    }
  }

  //-------------------------------------
  /**
   *
   * Evaluates the given expression as an SPEL expression.
   **/
  Object evaluateExpression (String pExpression,
			     PageContext pPageContext,
			     Class pExpectedType)
    throws JspException
  {
    ParsedExpression exp = getOrParseExpression (pExpression);
    return
      exp.evaluate (pPageContext,
		    pExpectedType);
  }

  //-------------------------------------
  /**
   *
   * Gets the parsed form of the given expression.  If the parsed form
   * is cached, return the cached form, otherwise parse and cache the
   * expression.
   **/
  public static ParsedExpression getOrParseExpression (String pExpression)
    throws JspException
  {
    // See if it's in the cache
    ParsedExpression ret = (ParsedExpression)
      mCachedExpressions.get (pExpression);

    if (ret == null) {
      // Parse the expression
      Reader r = new StringReader (pExpression);

      // Read past the '$'
      try {
	r.read ();
      }
      catch (IOException exc) {
	// This should never occur
      }

      SpelParser parser = new SpelParser (r);
      try {
	Expression exp = parser.Expression ();
	ret = new ParsedExpression (pExpression, exp);
	mCachedExpressions.put (pExpression, ret);
      }
      catch (ParseException exc) {
	throw new SpelException
	  (Constants.PARSE_EXCEPTION,
	   exc);
      }
      catch (TokenMgrError exc) {
	throw new SpelException
	  (Constants.PARSE_EXCEPTION,
	   exc);
      }
    }
    return ret;
  }

  //-------------------------------------
  /**
   *
   * Evaluates the given expression as a literal value converted to
   * the given expected type
   **/
  public Object evaluateLiteral (String pExpression,
			  PageContext pPageContext,
			  Class pExpectedType)
    throws JspException
  {
    if (pExpectedType == null) {
      return pExpression;
    }

    // Get the expectedType's mapping from value to parsed value
    Map values = getOrCreateExpectedTypeMap (pExpectedType);

    // See if the parsed value is already in there
    if (values.containsKey (pExpression)) {
      return values.get (pExpression);
    }

    // If the value starts with "\$", remove the leading "\"
    String valStr = pExpression;
    if (valStr.startsWith ("\\$")) {
      valStr = valStr.substring (1);
    }
    Object val;

    // See if we're parsing to a String or Object type
    if (pExpectedType == String.class ||
	pExpectedType == Object.class) {
      val = valStr;
    }

    // Otherwise, use the PropertyEditor mechanism to parse the
    // property type.
    else {
      val = parseToExpectedType (pExpression, pExpectedType);
    }

    values.put (valStr, val);
    return val;
  }

  //-------------------------------------
  /**
   *
   * Parses the specified value to the expected type using the
   * primitive valueOf mechanism, or the PropertyEditor mechanism.
   **/
  Object parseToExpectedType (String pValue,
			      Class pExpectedType)
    throws JspException
  {
    // Special-case the primitives
    if (pExpectedType == Boolean.TYPE ||
	pExpectedType == Boolean.class) {
      return Boolean.valueOf (pValue);
    }
    else if (pExpectedType == Byte.TYPE ||
	     pExpectedType == Byte.class) {
      return Byte.valueOf (pValue);
    }
    else if (pExpectedType == Short.TYPE ||
	     pExpectedType == Short.class) {
      return Short.valueOf (pValue);
    }
    else if (pExpectedType == Integer.TYPE ||
	     pExpectedType == Integer.class) {
      return Integer.valueOf (pValue);
    }
    else if (pExpectedType == Long.TYPE ||
	     pExpectedType == Long.class) {
      return Long.valueOf (pValue);
    }
    else if (pExpectedType == Float.TYPE ||
	     pExpectedType == Float.class) {
      return Float.valueOf (pValue);
    }
    else if (pExpectedType == Double.TYPE ||
	     pExpectedType == Double.class) {
      return Double.valueOf (pValue);
    }
    else {
      // Use the PropertyEditor mechanism
      PropertyEditor pe = PropertyEditorManager.findEditor (pExpectedType);
      if (pe == null) {
	throw new SpelException
	  (Constants.NO_PROPERTY_EDITOR,
	   pValue,
	   pExpectedType.getName ());
      }

      try {
	pe.setAsText (pValue);
	return pe.getValue ();
      }
      catch (IllegalArgumentException exc) {
	throw new SpelException
	  (Constants.CANT_PARSE_LITERAL,
	   pValue,
	   pExpectedType.getName (),
	   exc);
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Creates or returns the Map that maps string literals to parsed
   * values for the specified expected type.
   **/
  static Map getOrCreateExpectedTypeMap (Class pExpectedType)
  {
    synchronized (mCachedExpectedTypes) {
      Map ret = (Map) mCachedExpectedTypes.get (pExpectedType);
      if (ret == null) {
	ret = Collections.synchronizedMap (new HashMap ());
	mCachedExpectedTypes.put (pExpectedType, ret);
      }
      return ret;
    }
  }

  //-------------------------------------
  // ExpressionEvaluator methods
  //-------------------------------------
  /** 
   *
   * Translation time validation of an expression.  This method will
   * return a null String if the expression is valid; otherwise an
   * error message.
   **/ 
  public String validate (String pAttributeName,
			  String pExpression)
  {
    // If the expression starts with "$", try to parse it
    if (pExpression.startsWith ("$")) {
      try {
	getOrParseExpression (pExpression);
	return null;
      }
      catch (JspException exc) {
	return exc.toString ();
      }
    }
    else {
      return null;
    }
  }

  //-------------------------------------
  /**
   *
   * Evaluates the expression at request time
   **/
  public Object evaluate (String pAttributeName,
			  String pExpression,
			  Class pExpectedType,
			  Tag pTag,
			  PageContext pPageContext)
    throws JspException
  {
    return evaluate (pExpression, pPageContext, pExpectedType);
  }

  //-------------------------------------

}
