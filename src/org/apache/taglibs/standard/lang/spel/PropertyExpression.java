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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.List;

/**
 *
 * <p>Represents an expression of the form
 * {expression}.{property0}.{property1}...
 * 
 * @author Nathan Abramson - Art Technology Group
 * @author Shawn Bayern
 **/

public class PropertyExpression
  extends Expression
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  static Object [] sNoArgs = new Object [0];

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property base

  Expression mBase;
  public Expression getBase ()
  { return mBase; }

  //-------------------------------------
  // property propertyNames

  String [] mPropertyNames;
  public String [] getPropertyNames ()
  { return mPropertyNames; }

  //-------------------------------------
  // property baseIndexes
  List mBaseIndexes;

  //-------------------------------------
  // property propertyIndexes
  List mPropertyIndexes;

  //-------------------------------------
  /**
   *
   * Constructors
   **/
  public PropertyExpression (Expression pBase,
			     String [] pPropertyNames)
  {
    // mBase = pBase;
    // mPropertyNames = pPropertyNames;
    this(pBase, null, pPropertyNames, null);
  }

  public PropertyExpression (Expression pBase,
			     List pBaseIndexes,
			     String [] pPropertyNames,
			     List pPropertyIndexes)
  {
    mBase = pBase;
    mBaseIndexes = pBaseIndexes;
    mPropertyNames = pPropertyNames;
    mPropertyIndexes = pPropertyIndexes;
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
    buf.append (mBase.getExpressionString ());
    for (int i = 0; i < mPropertyNames.length; i++) {
      buf.append ('.');
      buf.append (StringLiteral.toIdentifierToken (mPropertyNames [i]));
    }
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
    // Evaluate the base first
    Object baseVal = mBase.evaluate (pContext);

    // Retrieve indexed value from baseVal as appropriate
    if (mBaseIndexes != null) {
	Indexer ind = new LiteralIndexes(mBaseIndexes);
	baseVal = ind.index(baseVal);
    }

    // Now evaluate each property (if we've got any)
    Object ret = baseVal;
    if (mPropertyNames == null)
	return ret;
    for (int i = 0; i < mPropertyNames.length; i++) {
      String propertyName = mPropertyNames [i];

      // Check for null value
      if (ret == null) {
	throw new SpelExpressionException 
	  (Constants.CANT_GET_PROPERTY_OF_NULL,
	   propertyName);
      }

      // Get the BeanInfoProperty
      BeanInfoProperty bip =
	BeanInfoManager.getBeanInfoProperty (ret.getClass (), propertyName);
      if (bip == null) {
	throw new SpelExpressionException 
	  (Constants.NO_SUCH_PROPERTY,
	   ret.getClass ().getName (),
	   propertyName);
      }

      // Get the method
      Method m = bip.getReadMethod ();
      if (m == null) {
	throw new SpelExpressionException 
	  (Constants.NO_GETTER_METHOD,
	   propertyName,
	   ret.getClass ().getName ());
      }

      // Invoke the method
      try {
	ret = m.invoke (ret, sNoArgs);
      }
      catch (IllegalAccessException exc) {
	throw new SpelExpressionException
	  (Constants.ERROR_GETTING_PROPERTY,
	   propertyName,
	   ret.getClass ().getName (),
	   exc);
      }
      catch (IllegalArgumentException exc) {
	throw new SpelExpressionException
	  (Constants.ERROR_GETTING_PROPERTY,
	   propertyName,
	   ret.getClass ().getName (),
	   exc);
      }
      catch (InvocationTargetException exc) {
	throw new SpelExpressionException
	  (Constants.ERROR_GETTING_PROPERTY,
	   propertyName,
	   ret.getClass ().getName (),
	   exc.getTargetException ());
      }

      // Retrieve indexed value from result if necessary
      if (mPropertyIndexes != null && mPropertyIndexes.get(i) != null) {
	Indexer ind = new LiteralIndexes((List) mPropertyIndexes.get(i));
	ret = ind.index(ret);
      }
    }

    return ret;
  }

  //-------------------------------------
}
