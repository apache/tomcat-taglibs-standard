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

import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 *
 * <p>This contains all of the non-public constants, including
 * messsage strings read from the resource file.
 *
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class Constants
{
  //-------------------------------------
  // Resources

  static ResourceBundle sResources =
  ResourceBundle.getBundle ("org.apache.taglibs.standard.lang.spel.Resources");

  //-------------------------------------
  // Messages from the resource bundle
  //-------------------------------------

  public static final String EXCEPTION_GETTING_BEANINFO =
    getStringResource ("EXCEPTION_GETTING_BEANINFO");

  public static final String CANT_GET_PROPERTY_OF_NULL =
    getStringResource ("CANT_GET_PROPERTY_OF_NULL");

  public static final String NO_SUCH_PROPERTY =
    getStringResource ("NO_SUCH_PROPERTY");

  public static final String NO_GETTER_METHOD =
    getStringResource ("NO_GETTER_METHOD");

  public static final String ERROR_GETTING_PROPERTY =
    getStringResource ("ERROR_GETTING_PROPERTY");

  public static final String COMPARISON_OF_NULL =
    getStringResource ("COMPARISON_OF_NULL");

  public static final String ILLEGAL_COMPARISON =
    getStringResource ("ILLEGAL_COMPARISON");

  public static final String NULL_TO_PRIMITIVE =
    getStringResource ("NULL_TO_PRIMITIVE");

  public static final String ILLEGAL_CONVERSION =
    getStringResource ("ILLEGAL_CONVERSION");

  public static final String NULL_EXPRESSION =
    getStringResource ("NULL_EXPRESSION");

  public static final String PARSE_EXCEPTION =
    getStringResource ("PARSE_EXCEPTION");

  public static final String NO_PROPERTY_EDITOR =
    getStringResource ("NO_PROPERTY_EDITOR");

  public static final String CANT_PARSE_LITERAL =
    getStringResource ("CANT_PARSE_LITERAL");

  public static final String CANT_FIND_ATTRIBUTE =
    getStringResource ("CANT_FIND_ATTRIBUTE");

  //-------------------------------------
  // Getting resources
  //-------------------------------------
  /**
   *
   * 
   **/
  public static String getStringResource (String pResourceName)
    throws MissingResourceException
  {
    try {
      String ret = sResources.getString (pResourceName);
      if (ret == null) {
	String str = "ERROR: Unable to load resource " + pResourceName;
	System.err.println (str);
	throw new MissingResourceException 
	  (str, 
	   "org.apache.taglibs.standard.lang.spel.Constants",
	   pResourceName);
      }
      else {
	return ret;
      }
    }
    catch (MissingResourceException exc) {
      System.err.println ("ERROR: Unable to load resource " +
			  pResourceName +
			  ": " +
			  exc);
      throw exc;
    }
  }

  //-------------------------------------
}
