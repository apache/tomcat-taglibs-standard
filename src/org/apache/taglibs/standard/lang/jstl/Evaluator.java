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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.jstl.parser.ELParser;
import org.apache.taglibs.standard.lang.jstl.parser.ParseException;
import org.apache.taglibs.standard.lang.jstl.parser.Token;
import org.apache.taglibs.standard.lang.jstl.parser.TokenMgrError;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluator;

/**
 *
 * <p>This is the main class for evaluating attribute values.  An
 * attribute value may contain expressions of the form ${...}.
 * Multiple expressions may appear in the same attribute value.  In
 * such a case, the attribute's value is computed by concatenating the
 * String values of those evaluated expressions and any intervening
 * non-expression text, then converting the resulting String to the
 * expected type using the PropertyEditor mechanism.
 *
 * <p>In the special case where the attribute value is a single
 * expression, the value of the attribute is determined by evaluating
 * the expression, without any intervening conversion to a String.
 *
 * <p>The evaluator maintains a cache mapping attribute value strings
 * to their parsed results.  For attribute values containing no
 * expression elements, it maintains a cache mapping
 * ExpectedType/ExpressionString to parsed value, so that static
 * attribute values won't have to go through a conversion step every
 * time they are used.
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

  // The mapping from attribute value to its parsed form (String,
  // Expression, or AttributeValue)
  static Map mCachedAttributeValues = 
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
   * Evaluates the given attribute value
   **/
  public Object evaluate (String pAttributeName,
			  String pAttributeValue,
			  PageContext pPageContext,
			  Class pExpectedType)
    throws JspException
  {
    Logger logger = new Logger (pPageContext);

    return evaluate (pAttributeName,
		     pAttributeValue,
		     pPageContext,
		     pExpectedType,
		     logger);
  }

  //-------------------------------------
  /**
   *
   * Evaluates the given attribute value
   **/
  public Object evaluate (String pAttributeName,
			  String pAttributeValue,
			  PageContext pPageContext,
			  Class pExpectedType,
			  Logger pLogger)
    throws JspException
  {
    try {
      // Check for null attribute values
      if (pAttributeValue == null) {
	throw new ELException
	  (Constants.NULL_ATTRIBUTE_VALUE);
      }

      // Get the parsed version of the attribute value
      Object parsedValue = getOrParseAttributeValue (pAttributeName,
						     pAttributeValue);

      // Evaluate differently based on the parsed type
      if (parsedValue instanceof String) {
	// Convert the String, and cache the conversion
	String strValue = (String) parsedValue;
	return convertStaticValueToExpectedType (strValue, 
						 pExpectedType, 
						 pLogger);
      }

      else if (parsedValue instanceof Expression) {
	// Evaluate the expression and convert
	Object value = 
	  ((Expression) parsedValue).evaluate (pPageContext, pLogger);
	return convertToExpectedType (value, 
				      pExpectedType,
				      pLogger);
      }

      else if (parsedValue instanceof AttributeValue) {
	// Evaluate the expression/string list and convert
	String strValue = 
	  ((AttributeValue) parsedValue).evaluate (pPageContext, pLogger);
	return convertToExpectedType (strValue,
				      pExpectedType,
				      pLogger);
      }

      else {
	// This should never be reached
	return null;
      }
    }
    catch (ELException exc) {
      throw new ELException
	(MessageFormat.format
	 (Constants.EVALUATION_EXCEPTION,
	  new Object [] {
	    "" + pAttributeName,
	    "" + pAttributeValue,
	    "" + exc
	  }));
    }
  }

  //-------------------------------------
  /**
   *
   * Gets the parsed form of the given attribute value.  If the parsed
   * form is cached, return the cached form, otherwise parse and cache
   * the value.  Returns either a String, Expression, or
   * AttributeValue.
   **/
  public static Object getOrParseAttributeValue (String pAttributeName,
						 String pAttributeValue)
    throws JspException
  {
    // See if it's an empty String
    if (pAttributeValue.length () == 0) {
      return "";
    }

    // See if it's in the cache
    Object ret = mCachedAttributeValues.get (pAttributeValue);

    if (ret == null) {
      // Parse the expression
      Reader r = new StringReader (pAttributeValue);
      ELParser parser = new ELParser (r);
      try {
	ret = parser.AttributeValue ();
	mCachedAttributeValues.put (pAttributeValue, ret);
      }
      catch (ParseException exc) {
	throw new ELException 
	  (formatParseException (pAttributeName,
				 pAttributeValue,
				 exc));
      }
      catch (TokenMgrError exc) {
	// Note - this should never be reached, since the parser is
	// constructed to tokenize any input (illegal inputs get
	// parsed to <BADLY_ESCAPED_STRING_LITERAL> or
	// <ILLEGAL_CHARACTER>
	throw new ELException (exc.getMessage ());
      }
    }
    return ret;
  }

  //-------------------------------------
  /**
   *
   * Converts the given value to the specified expected type.
   **/
  Object convertToExpectedType (Object pValue,
				Class pExpectedType,
				Logger pLogger)
    throws ELException
  {
    return Coercions.coerce (pValue,
			     pExpectedType,
			     pLogger);
  }

  //-------------------------------------
  /**
   *
   * Converts the given String, specified as a static attribute value,
   * to the given expected type.  The conversion is cached.
   **/
  Object convertStaticValueToExpectedType (String pValue,
					   Class pExpectedType,
					   Logger pLogger)
    throws ELException
  {
    // See if the value is already of the expected type
    if (pExpectedType == String.class ||
	pExpectedType == Object.class) {
      return pValue;
    }

    // Find the cached value
    Map valueByString = getOrCreateExpectedTypeMap (pExpectedType);
    if (valueByString.containsKey (pValue)) {
      return valueByString.get (pValue);
    }
    else {
      // Convert from a String
      Object ret = Coercions.coerce (pValue, pExpectedType, pLogger);
      valueByString.put (pValue, ret);
      return ret;
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
   * Translation time validation of an attribute value.  This method
   * will return a null String if the attribute value is valid;
   * otherwise an error message.
   **/ 
  public String validate (String pAttributeName,
			  String pAttributeValue)
  {
    if (pAttributeValue == null) {
      return Constants.NULL_ATTRIBUTE_VALUE;
    }
    else {
      try {
	getOrParseAttributeValue (pAttributeName,
				  pAttributeValue);
	return null;
      }
      catch (JspException exc) {
	return exc.toString ();
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Evaluates the expression at request time
   **/
  public Object evaluate (String pAttributeName,
			  String pAttributeValue,
			  Class pExpectedType,
			  Tag pTag,
			  PageContext pPageContext)
    throws JspException
  {
    return evaluate (pAttributeName,
		     pAttributeValue,
		     pPageContext,
		     pExpectedType);
  }

  //-------------------------------------
  // Formatting ParseException
  //-------------------------------------
  /**
   *
   * Formats a ParseException into an error message suitable for
   * displaying on a web page
   **/
  static String formatParseException (String pAttributeName,
				      String pAttributeValue,
				      ParseException pExc)
  {
    // Generate the String of expected tokens
    StringBuffer expectedBuf = new StringBuffer ();
    int maxSize = 0;
    boolean printedOne = false;
    for (int i = 0; i < pExc.expectedTokenSequences.length; i++) {
      if (maxSize < pExc.expectedTokenSequences [i].length) {
        maxSize = pExc.expectedTokenSequences [i].length;
      }
      for (int j = 0; j < pExc.expectedTokenSequences [i].length; j++) {
	if (printedOne) {
	  expectedBuf.append (", ");
	}
        expectedBuf.append 
	  (pExc.tokenImage [pExc.expectedTokenSequences [i] [j]]);
	printedOne = true;
      }
    }
    String expected = expectedBuf.toString ();

    // Generate the String of encountered tokens
    StringBuffer encounteredBuf = new StringBuffer ();
    Token tok = pExc.currentToken.next;
    for (int i = 0; i < maxSize; i++) {
      if (i != 0) encounteredBuf.append (" ");
      if (tok.kind == 0) {
        encounteredBuf.append (pExc.tokenImage [0]);
        break;
      }
      encounteredBuf.append (addEscapes (tok.image));
      tok = tok.next; 
    }
    String encountered = encounteredBuf.toString ();

    // Format the error message
    return MessageFormat.format
      (Constants.PARSE_EXCEPTION,
       new Object [] {
	 expected,
	 encountered,
	 "" + pAttributeName,
	 "" + pAttributeValue,
       });
  }

  //-------------------------------------
  /**
   *
   * Used to convert raw characters to their escaped version when
   * these raw version cannot be used as part of an ASCII string
   * literal.
   **/
  static String addEscapes (String str)
  {
    StringBuffer retval = new StringBuffer ();
    char ch;
    for (int i = 0; i < str.length (); i++) {
      switch (str.charAt (i)) {
	case 0 :
	  continue;
	case '\b':
	  retval.append ("\\b");
	  continue;
	case '\t':
	  retval.append ("\\t");
	  continue;
	case '\n':
	  retval.append ("\\n");
	  continue;
	case '\f':
	  retval.append ("\\f");
	  continue;
	case '\r':
	  retval.append ("\\r");
	  continue;
	default:
	  if ((ch = str.charAt (i)) < 0x20 || ch > 0x7e) {
	    String s = "0000" + Integer.toString (ch, 16);
	    retval.append ("\\u" + s.substring (s.length () - 4, s.length ()));
	  }
	  else {
	    retval.append (ch);
	  }
	  continue;
        }
    }
    return retval.toString ();
  }

  //-------------------------------------
  // Testing methods
  //-------------------------------------
  /**
   *
   * Parses the given attribute value, then converts it back to a
   * String in its canonical form.
   **/
  public static String parseAndRender (String pAttributeValue)
    throws JspException
  {
    Object val = getOrParseAttributeValue ("test", pAttributeValue);
    if (val instanceof String) {
      return (String) val;
    }
    else if (val instanceof Expression) {
      return "${" + ((Expression) val).getExpressionString () + "}";
    }
    else if (val instanceof AttributeValue) {
      return ((AttributeValue) val).getExpressionString ();
    }
    else {
      return "";
    }
  }

  //-------------------------------------

}
