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
 * <p>Represents a relational comparison expression
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class RelationalExpression
  extends Expression
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property value1

  Expression mValue1;
  public Expression getValue1 ()
  { return mValue1; }

  //-------------------------------------
  // property value2

  Expression mValue2;
  public Expression getValue2 ()
  { return mValue2; }

  //-------------------------------------
  // property op

  RelationalOperator mOp;
  public RelationalOperator getOp ()
  { return mOp; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public RelationalExpression (Expression pValue1,
			       Expression pValue2,
			       RelationalOperator pOp)
  {
    mValue1 = pValue1;
    mValue2 = pValue2;
    mOp = pOp;
  }

  //-------------------------------------
  // Expression methods
  //-------------------------------------
  /**
   *
   * Returns the expression in the expression language syntax
   **/
  public String getExpressionString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append (mValue1.getExpressionString ());
    buf.append (' ');
    buf.append (mOp.toString ());
    buf.append (' ');
    buf.append (mValue2.getExpressionString ());
    return buf.toString ();
  }

  //-------------------------------------
  /**
   *
   * Evaluates by traversing the list of properties
   **/
  public Object evaluate (PageContext pContext)
    throws JspException
  {
    // Evaluate the two values first
    Object val1 = mValue1.evaluate (pContext);
    Object val2 = mValue2.evaluate (pContext);

    // See if the rules for equality or ordering should be applied
    if (mOp.isEqualityOperator ()) {
      return mOp.getResult (testEquality (val1, val2));
    }
    else {
      return mOp.getResult (testOrdering (val1, val2));
    }
  }

  //-------------------------------------
  /**
   *
   * Applies the rules for testing equality of two values, returning
   * true if they are equal, false if not.
   **/
  boolean testEquality (Object pVal1,
			Object pVal2)
  {
    // Two null values are equal
    if (pVal1 == null &&
	pVal2 == null) {
      return true;
    }

    // One null value is not equal
    if (pVal1 == null ||
	pVal2 == null) {
      return false;
    }

    // Equality by identity
    if (pVal1 == pVal2) {
      return true;
    }

    // Test numeric equality by casting both classes "up" to the same
    // precision level and testing equality that way
    Class cl1 = pVal1.getClass ();
    Class cl2 = pVal2.getClass ();

    // If first class is Byte
    if (cl1 == Byte.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Byte) pVal1).intValue () == 
	  ((Byte) pVal2).intValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Byte) pVal1).intValue () == 
	  ((Short) pVal2).intValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Byte) pVal1).intValue () == 
	  ((Integer) pVal2).intValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Byte) pVal1).longValue () == 
	  ((Long) pVal2).longValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Byte) pVal1).floatValue () == 
	  ((Float) pVal2).floatValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Byte) pVal1).floatValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // If first class is Short
    else if (cl1 == Short.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Short) pVal1).intValue () == 
	  ((Byte) pVal2).intValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Short) pVal1).intValue () == 
	  ((Short) pVal2).intValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Short) pVal1).intValue () == 
	  ((Integer) pVal2).intValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Short) pVal1).longValue () == 
	  ((Long) pVal2).longValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Short) pVal1).floatValue () == 
	  ((Float) pVal2).floatValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Short) pVal1).doubleValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // If first class is Integer
    else if (cl1 == Integer.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Integer) pVal1).intValue () == 
	  ((Byte) pVal2).intValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Integer) pVal1).intValue () == 
	  ((Short) pVal2).intValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Integer) pVal1).intValue () == 
	  ((Integer) pVal2).intValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Integer) pVal1).longValue () == 
	  ((Long) pVal2).longValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Integer) pVal1).floatValue () == 
	  ((Float) pVal2).floatValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Integer) pVal1).doubleValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // If first class is Long
    else if (cl1 == Long.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Long) pVal1).longValue () == 
	  ((Byte) pVal2).longValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Long) pVal1).longValue () == 
	  ((Short) pVal2).longValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Long) pVal1).longValue () == 
	  ((Integer) pVal2).longValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Long) pVal1).longValue () == 
	  ((Long) pVal2).longValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Long) pVal1).floatValue () == 
	  ((Float) pVal2).floatValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Long) pVal1).doubleValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // If first class is float
    else if (cl1 == Float.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Float) pVal1).floatValue () == 
	  ((Byte) pVal2).floatValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Float) pVal1).floatValue () == 
	  ((Short) pVal2).floatValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Float) pVal1).floatValue () == 
	  ((Integer) pVal2).floatValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Float) pVal1).floatValue () == 
	  ((Long) pVal2).floatValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Float) pVal1).floatValue () == 
	  ((Float) pVal2).floatValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Float) pVal1).doubleValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // If first class is double
    else if (cl1 == Double.class) {
      if (cl2 == Byte.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Byte) pVal2).doubleValue ();
      }
      else if (cl2 == Short.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Short) pVal2).doubleValue ();
      }
      else if (cl2 == Integer.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Integer) pVal2).doubleValue ();
      }
      else if (cl2 == Long.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Long) pVal2).doubleValue ();
      }
      else if (cl2 == Float.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Float) pVal2).doubleValue ();
      }
      else if (cl2 == Double.class) {
	return 
	  ((Double) pVal1).doubleValue () == 
	  ((Double) pVal2).doubleValue ();
      }
    }

    // Equality by value
    if (pVal1.equals (pVal2)) {
      return true;
    }

    // All other cases are not equal
    return false;
  }

  //-------------------------------------
  /**
   *
   * Applies the rules for testing ordering of two values, returning <
   * 0 if pVal1 < pVal2, > 0 if pVal1 > pVal2, and 0 if pVal1 ==
   * pVal2.
   **/
  int testOrdering (Object pVal1,
		    Object pVal2)
    throws JspException
  {
    // Make sure neither value is null
    if (pVal1 == null ||
	pVal2 == null) {
      throw new SpelException
	(Constants.COMPARISON_OF_NULL,
	 mOp.toString ());
    }
    
    // Test Strings
    if (pVal1 instanceof String &&
	pVal2 instanceof String) {
      return ((String) pVal1).compareTo ((String) pVal2);
    }

    // Test numeric ordering by casting both classes "up" to the same
    // precision level and testing ordering that way
    Class cl1 = pVal1.getClass ();
    Class cl2 = pVal2.getClass ();

    // If first class is Byte
    if (cl1 == Byte.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Byte) pVal1).intValue (),
	   ((Byte) pVal2).intValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Byte) pVal1).intValue (),
	   ((Short) pVal2).intValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Byte) pVal1).intValue (),
	   ((Integer) pVal2).intValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Byte) pVal1).longValue (),
	   ((Long) pVal2).longValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Byte) pVal1).floatValue (),
	   ((Float) pVal2).floatValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Byte) pVal1).floatValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // If first class is Short
    else if (cl1 == Short.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Short) pVal1).intValue (),
	   ((Byte) pVal2).intValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Short) pVal1).intValue (),
	   ((Short) pVal2).intValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Short) pVal1).intValue (),
	   ((Integer) pVal2).intValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Short) pVal1).longValue (),
	   ((Long) pVal2).longValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Short) pVal1).floatValue (),
	   ((Float) pVal2).floatValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Short) pVal1).doubleValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // If first class is Integer
    else if (cl1 == Integer.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Integer) pVal1).intValue (),
	   ((Byte) pVal2).intValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Integer) pVal1).intValue (),
	   ((Short) pVal2).intValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Integer) pVal1).intValue (),
	   ((Integer) pVal2).intValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Integer) pVal1).longValue (),
	   ((Long) pVal2).longValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Integer) pVal1).floatValue (),
	   ((Float) pVal2).floatValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Integer) pVal1).doubleValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // If first class is Long
    else if (cl1 == Long.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Long) pVal1).longValue (),
	   ((Byte) pVal2).longValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Long) pVal1).longValue (),
	   ((Short) pVal2).longValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Long) pVal1).longValue (),
	   ((Integer) pVal2).longValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Long) pVal1).longValue (),
	   ((Long) pVal2).longValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Long) pVal1).floatValue (),
	   ((Float) pVal2).floatValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Long) pVal1).doubleValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // If first class is float
    else if (cl1 == Float.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Float) pVal1).floatValue (),
	   ((Byte) pVal2).floatValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Float) pVal1).floatValue (),
	   ((Short) pVal2).floatValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Float) pVal1).floatValue (),
	   ((Integer) pVal2).floatValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Float) pVal1).floatValue (),
	   ((Long) pVal2).floatValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Float) pVal1).floatValue (),
	   ((Float) pVal2).floatValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Float) pVal1).doubleValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // If first class is double
    else if (cl1 == Double.class) {
      if (cl2 == Byte.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Byte) pVal2).doubleValue ());
      }
      else if (cl2 == Short.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Short) pVal2).doubleValue ());
      }
      else if (cl2 == Integer.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Integer) pVal2).doubleValue ());
      }
      else if (cl2 == Long.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Long) pVal2).doubleValue ());
      }
      else if (cl2 == Float.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Float) pVal2).doubleValue ());
      }
      else if (cl2 == Double.class) {
	return compareValues
	  (((Double) pVal1).doubleValue (),
	   ((Double) pVal2).doubleValue ());
      }
    }

    // All other cases result in error
    throw new SpelException
      (Constants.ILLEGAL_COMPARISON,
       cl1.getName (),
       cl2.getName (),
       mOp.toString ());
  }

  //-------------------------------------
  /**
   *
   * Compares two values
   **/
  static int compareValues (int pVal1, int pVal2)
  {
    if (pVal1 < pVal2) return -1;
    if (pVal1 > pVal2) return 1;
    return 0;
  }

  //-------------------------------------
  /**
   *
   * Compares two values
   **/
  static int compareValues (long pVal1, long pVal2)
  {
    if (pVal1 < pVal2) return -1;
    if (pVal1 > pVal2) return 1;
    return 0;
  }

  //-------------------------------------
  /**
   *
   * Compares two values
   **/
  static int compareValues (float pVal1, float pVal2)
  {
    if (pVal1 < pVal2) return -1;
    if (pVal1 > pVal2) return 1;
    return 0;
  }

  //-------------------------------------
  /**
   *
   * Compares two values
   **/
  static int compareValues (double pVal1, double pVal2)
  {
    if (pVal1 < pVal2) return -1;
    if (pVal1 > pVal2) return 1;
    return 0;
  }

  //-------------------------------------
}
