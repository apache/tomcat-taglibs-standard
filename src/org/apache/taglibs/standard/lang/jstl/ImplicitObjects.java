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

package org.apache.taglibs.standard.lang.jstl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 *
 * <p>This class is used to generate the implicit Map and List objects
 * that wrap various elements of the PageContext.  It also returns the
 * correct implicit object for a given implicit object name.
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class ImplicitObjects
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  // FIXME - better attribute name
  static final String sAttributeName = 
    "javax.servlet.http.jsp.jstl.ImplicitObjects";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  PageContext mContext;
  Map mPage;
  Map mRequest;
  Map mSession;
  Map mApplication;
  Map mParam;
  Map mParams;

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public ImplicitObjects (PageContext pContext)
  {
    mContext = pContext;
  }

  //-------------------------------------
  /**
   *
   * Finds the ImplicitObjects associated with the PageContext,
   * creating it if it doesn't yet exist.
   **/
  public static ImplicitObjects getImplicitObjects (PageContext pContext)
  {
    ImplicitObjects objs = 
      (ImplicitObjects)
      pContext.getAttribute (sAttributeName,
			     PageContext.PAGE_SCOPE);
    if (objs == null) {
      objs = new ImplicitObjects (pContext);
      pContext.setAttribute (sAttributeName,
			     objs,
			     PageContext.PAGE_SCOPE);
    }
    return objs;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that "wraps" page-scoped attributes
   **/
  public Map getPageScopeMap ()
  {
    if (mPage == null) {
      mPage = createPageScopeMap (mContext);
    }
    return mPage;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that "wraps" request-scoped attributes
   **/
  public Map getRequestScopeMap ()
  {
    if (mRequest == null) {
      mRequest = createRequestScopeMap (mContext);
    }
    return mRequest;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that "wraps" session-scoped attributes
   **/
  public Map getSessionScopeMap ()
  {
    if (mSession == null) {
      mSession = createSessionScopeMap (mContext);
    }
    return mSession;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that "wraps" application-scoped attributes
   **/
  public Map getApplicationScopeMap ()
  {
    if (mApplication == null) {
      mApplication = createApplicationScopeMap (mContext);
    }
    return mApplication;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that maps parameter name to a single parameter
   * values.
   **/
  public Map getParamMap ()
  {
    if (mParam == null) {
      mParam = createParamMap (mContext);
    }
    return mParam;
  }

  //-------------------------------------
  /**
   *
   * Returns the Map that maps parameter name to an array of parameter
   * values.
   **/
  public Map getParamsMap ()
  {
    if (mParams == null) {
      mParams = createParamsMap (mContext);
    }
    return mParams;
  }

  //-------------------------------------
  // Methods for generating wrapper maps
  //-------------------------------------
  /**
   *
   * Creates the Map that "wraps" page-scoped attributes
   **/
  public static Map createPageScopeMap (PageContext pContext)
  {
    final PageContext context = pContext;
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return context.getAttributeNamesInScope
	    (PageContext.PAGE_SCOPE);
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return context.getAttribute
	      ((String) pKey, 
	       PageContext.PAGE_SCOPE);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return true;
	}
      };
  }

  //-------------------------------------
  /**
   *
   * Creates the Map that "wraps" request-scoped attributes
   **/
  public static Map createRequestScopeMap (PageContext pContext)
  {
    final PageContext context = pContext;
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return context.getAttributeNamesInScope
	    (PageContext.REQUEST_SCOPE);
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return context.getAttribute
	      ((String) pKey, 
	       PageContext.REQUEST_SCOPE);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return true;
	}
      };
  }

  //-------------------------------------
  /**
   *
   * Creates the Map that "wraps" session-scoped attributes
   **/
  public static Map createSessionScopeMap (PageContext pContext)
  {
    final PageContext context = pContext;
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return context.getAttributeNamesInScope
	    (PageContext.SESSION_SCOPE);
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return context.getAttribute
	      ((String) pKey, 
	       PageContext.SESSION_SCOPE);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return true;
	}
      };
  }

  //-------------------------------------
  /**
   *
   * Creates the Map that "wraps" application-scoped attributes
   **/
  public static Map createApplicationScopeMap (PageContext pContext)
  {
    final PageContext context = pContext;
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return context.getAttributeNamesInScope
	    (PageContext.APPLICATION_SCOPE);
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return context.getAttribute
	      ((String) pKey, 
	       PageContext.APPLICATION_SCOPE);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return true;
	}
      };
  }

  //-------------------------------------
  /**
   *
   * Creates the Map that maps parameter name to single parameter
   * value.
   **/
  public static Map createParamMap (PageContext pContext)
  {
    final HttpServletRequest request =
      (HttpServletRequest) pContext.getRequest ();
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return request.getParameterNames ();
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return request.getParameter ((String) pKey);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return false;
	}
      };
  }

  //-------------------------------------
  /**
   *
   * Creates the Map that maps parameter name to an array of parameter
   * values.
   **/
  public static Map createParamsMap (PageContext pContext)
  {
    final HttpServletRequest request =
      (HttpServletRequest) pContext.getRequest ();
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return request.getParameterNames ();
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return request.getParameterValues ((String) pKey);
	  }
	  else {
	    return null;
	  }
	}

	public boolean isMutable ()
	{
	  return false;
	}
      };
  }

  //-------------------------------------
}
