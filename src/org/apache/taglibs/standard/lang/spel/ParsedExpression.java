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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 *
 * <p>This represents the result of parsing an expression.  It
 * contains the Expression object representing the parsed result, and
 * the original expression string.
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class ParsedExpression
{
  //-------------------------------------
  // Properties
  //-------------------------------------
  // property expressionString

  String mExpressionString;
  public String getExpressionString ()
  { return mExpressionString; }

  //-------------------------------------
  // property expression  

  Expression mExpression;
  public Expression getExpression ()
  { return mExpression; }

  //-------------------------------------
  // Member variables
  //-------------------------------------

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public ParsedExpression (String pExpressionString,
			   Expression pExpression)
  {
    mExpressionString = pExpressionString;
    mExpression = pExpression;
  }

  //-------------------------------------
  /**
   *
   * Evaluates the expression within the context of the given
   * PageContext, casting the result to the given expected type.
   **/
  public Object evaluate (PageContext pPageContext,
			  Class pExpectedType)
    throws JspException
  {
    Object ret = mExpression.evaluate (pPageContext);

    // If assignable to the expected type, return
    if (ret != null &&
	pExpectedType.isAssignableFrom (ret.getClass ())) {
      return ret;
    }

    // If value is null, return if expected type is not a primitive,
    // error if it is a primitive
    if (ret == null) {
      if (pExpectedType.isPrimitive ()) {
	throw new SpelException
	  (Constants.NULL_TO_PRIMITIVE,
	   pExpectedType.getName ());
      }
      else {
	return ret;
      }
    }

    Class cl = ret.getClass ();
    Class et = getObjectPrimitiveType (pExpectedType);

    // If the non-primitive version of the expected type matches,
    // return
    if (cl == et) {
      return ret;
    }

    // If both types are numeric, cast to the expected type
    if (et == Byte.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getByte 
	  (((Number) ret).byteValue ());
      }
    }
    else if (et == Short.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getShort
	  (((Number) ret).shortValue ());
      }
    }
    else if (et == Integer.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getInteger
	  (((Number) ret).intValue ());
      }
    }
    else if (et == Long.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getLong
	  (((Number) ret).longValue ());
      }
    }
    else if (et == Float.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getFloat
	  (((Number) ret).floatValue ());
      }
    }
    else if (et == Double.class) {
      if (ret instanceof Number) {
	return PrimitiveObjects.getDouble
	  (((Number) ret).doubleValue ());
      }
    }

    // All other cases are errors
    throw new SpelException
      (Constants.ILLEGAL_CONVERSION,
       cl.getName (),
       pExpectedType.getName ());
  }

  //-------------------------------------
  /**
   *
   * If the class is a primitive type, returns the object type of that
   * class.  Otherwise, returns the class.
   **/
  Class getObjectPrimitiveType (Class pClass)
  {
    if (pClass == Boolean.TYPE) {
      return Boolean.class;
    }
    else if (pClass == Byte.TYPE) {
      return Byte.class;
    }
    else if (pClass == Character.TYPE) {
      return Character.class;
    }
    else if (pClass == Short.TYPE) {
      return Short.class;
    }
    else if (pClass == Integer.TYPE) {
      return Integer.class;
    }
    else if (pClass == Long.TYPE) {
      return Long.class;
    }
    else if (pClass == Float.TYPE) {
      return Float.class;
    }
    else if (pClass == Double.TYPE) {
      return Double.class;
    }
    else {
      return pClass;
    }
  }

  //-------------------------------------

}
