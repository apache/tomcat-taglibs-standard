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

  static final String sImplicitObjectNames =
    "#pagectx, #attrs, #params, #paramvalues, #headers, #headervalues, #initparams, #locales, #cookies, #now";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  PageContext mContext;
  Attributes mAttrs;
  Map mParams;
  Map mParamvalues;
  Map mHeaders;
  Map mHeadervalues;
  Map mInitparams;
  List mLocales;
  Map mCookies;

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
   * Returns true if the given name is a possible implicit object
   **/
  public static boolean isPossibleImplicitObject (String pName)
  {
    return
      pName != null &&
      pName.length () >= 1 &&
      pName.charAt (0) == '#';
  }

  //-------------------------------------
  /**
   *
   * Returns the implicit object with the given name.  All of the
   * implicit objects are stored in an ImplicitObjects, which is
   * itself cached as a page-scoped attribute.
   **/
  public static Object getImplicitObject (String pName,
					  PageContext pContext,
					  Logger pLogger)
    throws ELException
  {
    // Get the implicit objects from the PageContext
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

    return objs.implicitObject (pName, pLogger);
  }

  //-------------------------------------
  /**
   *
   * Returns the object with the given implicit object name
   **/
  public Object implicitObject (String pName,
				Logger pLogger)
    throws ELException
  {
    if ("#pagectx".equals (pName)) {
      return mContext;
    }
    else if ("#attrs".equals (pName)) {
      if (mAttrs == null) {
	mAttrs = new Attributes (mContext);
      }
      return mAttrs;
    }
    else if ("#params".equals (pName)) {
      if (mParams == null) {
	mParams = getParamsMap (mContext);
      }
      return mParams;
    }
    else if ("#paramvalues".equals (pName)) {
      if (mParamvalues == null) {
	mParamvalues = getParamvaluesMap (mContext);
      }
      return mParamvalues;
    }
    else if ("#headers".equals (pName)) {
      if (mHeaders == null) {
	mHeaders = getHeadersMap (mContext);
      }
      return mHeaders;
    }
    else if ("#headervalues".equals (pName)) {
      if (mHeadervalues == null) {
	mHeadervalues = getHeadervaluesMap (mContext);
      }
      return mHeadervalues;
    }
    else if ("#initparams".equals (pName)) {
      if (mInitparams == null) {
	mInitparams = getInitparamsMap (mContext);
      }
      return mInitparams;
    }
    else if ("#locales".equals (pName)) {
      if (mLocales == null) {
	mLocales = getLocalesList (mContext);
      }
      return mLocales;
    }
    else if ("#cookies".equals (pName)) {
      if (mCookies == null) {
	mCookies = getCookiesMap (mContext);
      }
      return mCookies;
    }
    else if ("#now".equals (pName)) {
      return new Date ();
    }
    else {
      if (pLogger.isLoggingError ()) {
	pLogger.logError
	  (Constants.BAD_IMPLICIT_OBJECT,
	   pName,
	   sImplicitObjectNames);
      }
      return null;
    }
  }

  //-------------------------------------
  // Methods for generating wrapper maps
  //-------------------------------------
  /**
   *
   * Returns the Map that "wraps" page-scoped attributes
   **/
  public static Map getPageScopeMap (PageContext pContext)
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
   * Returns the Map that "wraps" request-scoped attributes
   **/
  public static Map getRequestScopeMap (PageContext pContext)
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
   * Returns the Map that "wraps" session-scoped attributes
   **/
  public static Map getSessionScopeMap (PageContext pContext)
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
   * Returns the Map that "wraps" application-scoped attributes
   **/
  public static Map getApplicationScopeMap (PageContext pContext)
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
   * Returns the Map that maps parameter name to single parameter
   * value.
   **/
  public static Map getParamsMap (PageContext pContext)
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
   * Returns the Map that maps parameter name to an array of parameter
   * values.
   **/
  public static Map getParamvaluesMap (PageContext pContext)
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
  /**
   *
   * Returns the Map that maps header name to single header
   * value.
   **/
  public static Map getHeadersMap (PageContext pContext)
  {
    final HttpServletRequest request =
      (HttpServletRequest) pContext.getRequest ();
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return request.getHeaderNames ();
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return request.getHeader ((String) pKey);
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
   * Returns the Map that maps header name to an array of header
   * values.
   **/
  public static Map getHeadervaluesMap (PageContext pContext)
  {
    final HttpServletRequest request =
      (HttpServletRequest) pContext.getRequest ();
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return request.getHeaderNames ();
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    List l = new ArrayList ();
	    for (Enumeration e = request.getHeaders ((String) pKey);
		 e.hasMoreElements (); ) {
	      l.add (e.nextElement ());
	    }
	    return (String []) l.toArray (new String [l.size ()]);
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
   * Returns the Map that ServletContext init parameter name to init
   * parameter value.
   **/
  public static Map getInitparamsMap (PageContext pContext)
  {
    final ServletContext context = pContext.getServletContext ();
    return new EnumeratedMap ()
      {
	public Enumeration enumerateKeys () 
	{
	  return context.getInitParameterNames ();
	}

	public Object getValue (Object pKey) 
	{
	  if (pKey instanceof String) {
	    return context.getInitParameter ((String) pKey);
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
   * Returns the List of Locales acceptable to the client, in
   * decreasing order of preference.
   **/
  public static List getLocalesList (PageContext pContext)
  {
    ArrayList ret = new ArrayList ();
    for (Enumeration e = pContext.getRequest ().getLocales ();
	 e.hasMoreElements (); ) {
      ret.add (e.nextElement ());
    }
    return Collections.unmodifiableList (ret);
  }

  //-------------------------------------
  /**
   *
   * Returns the Map of Cookies, mapping Cookie name to Cookie.  Note
   * that multiple Cookies may have the same name - in such a case, it
   * is undefined which Cookie will appear in the Map
   **/
  public static Map getCookiesMap (PageContext pContext)
  {
    Map ret = new HashMap ();
    Cookie [] cookies = 
      ((HttpServletRequest) (pContext.getRequest ())).getCookies ();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
	Cookie cookie = cookies [i];
	ret.put (cookie.getName (), cookie);
      }
    }
    return ret;
  }

  //-------------------------------------
}
