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

/**
 *
 * <p>Represents the various relational operators
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class RelationalOperator
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  static final int EQ_VALUE = 1;
  static final int NE_VALUE = 2;
  static final int GT_VALUE = 3;
  static final int LT_VALUE = 4;
  static final int GE_VALUE = 5;
  static final int LE_VALUE = 6;

  public static final RelationalOperator EQ =
    new RelationalOperator (EQ_VALUE);
  public static final RelationalOperator NE =
    new RelationalOperator (NE_VALUE);
  public static final RelationalOperator GT =
    new RelationalOperator (GT_VALUE);
  public static final RelationalOperator LT =
    new RelationalOperator (LT_VALUE);
  public static final RelationalOperator GE =
    new RelationalOperator (GE_VALUE);
  public static final RelationalOperator LE =
    new RelationalOperator (LE_VALUE);

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property op

  int mOp;
  public int getOp ()
  { return mOp; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  RelationalOperator (int pOp)
  {
    mOp = pOp;
  }

  //-------------------------------------
  /**
   *
   * String representation
   **/
  public String toString ()
  {
    switch (mOp) {
    case EQ_VALUE:
      return "==";
    case NE_VALUE:
      return "!=";
    case GT_VALUE:
      return ">";
    case LT_VALUE:
      return "<";
    case GE_VALUE:
      return ">=";
    case LE_VALUE:
      return "<=";
    default:
      return "??";
    }
  }

  //-------------------------------------
  /**
   *
   * Returns true if this is == or !=
   **/
  public boolean isEqualityOperator ()
  {
    return
      mOp == EQ_VALUE ||
      mOp == NE_VALUE;
  }

  //-------------------------------------
  /**
   *
   * Returns the Boolean object for the given boolean value
   **/
  static Boolean getBooleanValue (boolean pValue)
  {
    return (pValue) ? Boolean.TRUE : Boolean.FALSE;
  }

  //-------------------------------------
  /**
   *
   * Returns the result based on the flag indicating if the two values
   * are equal
   **/
  public Boolean getResult (boolean pEquals)
  {
    if (mOp == EQ_VALUE) {
      return getBooleanValue (pEquals);
    }
    else if (mOp == NE_VALUE) {
      return getBooleanValue (!pEquals);
    }
    else {
      return getBooleanValue (false);
    }
  }

  //-------------------------------------
  /**
   *
   * Returns the result based on the flag indicating the ordering of
   * the two values (< 0 means a < b, > 0 means a > b, 0 means a == b)
   **/
  public Boolean getResult (int pOrdering)
  {
    switch (mOp) {
    case EQ_VALUE:
      return getBooleanValue (pOrdering == 0);
    case NE_VALUE:
      return getBooleanValue (pOrdering != 0);
    case GT_VALUE:
      return getBooleanValue (pOrdering > 0);
    case LT_VALUE:
      return getBooleanValue (pOrdering < 0);
    case GE_VALUE:
      return getBooleanValue (pOrdering >= 0);
    case LE_VALUE:
      return getBooleanValue (pOrdering <= 0);
    default:
      return getBooleanValue (false);
    }
  }

  //-------------------------------------
}
