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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 *
 * <p>Represents the various scopes
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181991 $$DateTime: 2001/07/02 16:12:52 $$Author$
 **/

public class Scope
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  static final int UNSCOPED_VALUE = 1;
  static final int PAGE_VALUE = 2;
  static final int REQUEST_VALUE = 3;
  static final int SESSION_VALUE = 4;
  static final int APP_VALUE = 5;
  static final int HEADER_VALUE = 6;
  static final int PARAM_VALUE = 7;
  static final int PARAMVALUES_VALUE = 8;

  public static final Scope UNSCOPED = new Scope (UNSCOPED_VALUE);
  public static final Scope PAGE = new Scope (PAGE_VALUE);
  public static final Scope REQUEST = new Scope (REQUEST_VALUE);
  public static final Scope SESSION = new Scope (SESSION_VALUE);
  public static final Scope APP = new Scope (APP_VALUE);
  public static final Scope HEADER = new Scope (HEADER_VALUE);
  public static final Scope PARAM = new Scope (PARAM_VALUE);
  public static final Scope PARAMVALUES = new Scope (PARAMVALUES_VALUE);

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property scope

  int mScope;
  public int getScope ()
  { return mScope; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  Scope (int pScope)
  {
    mScope = pScope;
  }

  //-------------------------------------
  /**
   *
   * Looks up the attribute value in the specified page context, using
   * the appropriate scope, returning null if not found
   **/
  Object getAttributeValue (String pName,
			    PageContext pContext)
  {
    switch (mScope) {
    case UNSCOPED_VALUE:
      return pContext.findAttribute (pName);
    case PAGE_VALUE:
      return pContext.getAttribute (pName, pContext.PAGE_SCOPE);
    case REQUEST_VALUE:
      return pContext.getAttribute (pName, pContext.REQUEST_SCOPE);
    case SESSION_VALUE:
      return pContext.getAttribute (pName, pContext.SESSION_SCOPE);
    case APP_VALUE:
      return pContext.getAttribute (pName, pContext.APPLICATION_SCOPE);
    case HEADER_VALUE:
      return ((HttpServletRequest) (pContext.getRequest ())).getHeader (pName);
    case PARAM_VALUE:
      return pContext.getRequest ().getParameter (pName);
    case PARAMVALUES_VALUE:
      return pContext.getRequest ().getParameterValues (pName);
    default:
      return null;
    }
  }

  //-------------------------------------
  /**
   *
   * Returns a form of the scope name suitable for error messages
   **/
  public String getScopeName ()
  {
    switch (mScope) {
    case UNSCOPED_VALUE:
      return "page/request/session/app";
    case PAGE_VALUE:
      return "page";
    case REQUEST_VALUE:
      return "request";
    case SESSION_VALUE:
      return "session";
    case APP_VALUE:
      return "app";
    case HEADER_VALUE:
      return "header";
    case PARAM_VALUE:
      return "param";
    case PARAMVALUES_VALUE:
      return "paramvalues";
    default:
      return "??";
    }
  }

  //-------------------------------------
  /**
   *
   * String representation
   **/
  public String toString ()
  {
    switch (mScope) {
    case UNSCOPED_VALUE:
      return ":";
    case PAGE_VALUE:
      return "page:";
    case REQUEST_VALUE:
      return "request:";
    case SESSION_VALUE:
      return "session:";
    case APP_VALUE:
      return "app:";
    case HEADER_VALUE:
      return "header:";
    case PARAM_VALUE:
      return "param:";
    case PARAMVALUES_VALUE:
      return "paramvalues:";
    default:
      return ":";
    }
  }

  //-------------------------------------
}
