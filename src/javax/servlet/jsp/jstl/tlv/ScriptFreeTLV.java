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

package javax.servlet.jsp.jstl.tlv;

import javax.servlet.jsp.tagext.TagLibraryValidator;
import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.io.InputStream;
import java.util.Map;

import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * <p>A SAX-based TagLibraryValidator for enforcing restrictions against
 * the use of JSP scripting elements.</p>
 * <p>This TLV supports four initialization parameters, for controlling
 * which of the four types of scripting elements are allowed or prohibited:</p>
 * <ul>
 * <li><b>allowDeclarations</b>: if true, indicates that declaration elements
 * are not prohibited.
 * <li><b>allowScriptlets</b>: if true, indicates that scriptlets are not
 * prohibited
 * <li><b>allowExpressions</b>: if true, indicates that top-level expression
 * elements (i.e., expressions not associated with request-time attribute
 * values) are not prohibited.
 * <li><b>allowRTExpressions</b>: if true, indicates that expression elements
 * associated with request-time attribute values are not prohibited.
 * </ul>
 * <p>The default value for all for initialization parameters is false,
 * indicating all forms of scripting elements are to be prohibited.</p>
 * 
 * @author <a href="mailto:mak@taglib.com">Mark A. Kolb</a>
 * @author Shawn Bayern (minor changes)
 */
public class ScriptFreeTLV extends TagLibraryValidator {
  private boolean allowDeclarations = false;
  private boolean allowScriptlets = false;
  private boolean allowExpressions = false;
  private boolean allowRTExpressions = false;
  private SAXParserFactory factory;

  /**
   * Constructs a new validator instance.
   * Initializes the parser factory to create non-validating, namespace-aware
   * SAX parsers.
   */
  public ScriptFreeTLV () {
    factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
  }

  /**
   * Sets the values of the initialization parameters, as supplied in the TLD.
   * @param initParms a mapping from the names of the initialization parameters
   * to their values, as specified in the TLD.
   */
  public void setInitParameters (Map initParms) {
    super.setInitParameters(initParms);
    String declarationsParm = (String) initParms.get("allowDeclarations");
    String scriptletsParm = (String) initParms.get("allowScriptlets");
    String expressionsParm = (String) initParms.get("allowExpressions");
    String rtExpressionsParm = (String) initParms.get("allowRTExpressions");

    allowDeclarations = "true".equalsIgnoreCase(declarationsParm);
    allowScriptlets = "true".equalsIgnoreCase(scriptletsParm);
    allowExpressions = "true".equalsIgnoreCase(expressionsParm);
    allowRTExpressions = "true".equalsIgnoreCase(rtExpressionsParm);
  }

  /**
   * Validates a single JSP page.
   * @param prefix the namespace prefix specified by the page for the
   * custom tag library being validated.
   * @param uri the URI specified by the page for the TLD of the
   * custom tag library being validated.
   * @param page a wrapper around the XML representation of the page
   * being validated.
   * @return null, if the page is valid; otherwise, a ValidationMessage[]
   * containing one or more messages indicating why the page is not valid.
   */
  public ValidationMessage[] validate
      (String prefix, String uri, PageData page) {
    InputStream in = null;
    SAXParser parser;
    MyContentHandler handler = new MyContentHandler();
    try {
      synchronized (factory) {
	parser = factory.newSAXParser();
      }
      in = page.getInputStream();
      parser.parse(in, handler);
    }
    catch (ParserConfigurationException e) {
      return vmFromString(e.getMessage());
    }
    catch (SAXException e) {
      return vmFromString(e.getMessage());
    }
    catch (IOException e) {
      return vmFromString(e.getMessage());
    }
    finally {
      if (in != null) try { in.close(); } catch (IOException e) {}
    }
    return handler.reportResults();
  }

  /** 
   * Handler for SAX events. 
   * Four counters are provided as instance variables,
   * for counting occurrences of prohibited scripting elements.
   */
  private class MyContentHandler extends DefaultHandler {
    private int declarationCount = 0;
    private int scriptletCount = 0;
    private int expressionCount = 0;
    private int rtExpressionCount = 0;

    /** 
     * This event is received whenever a new element is encountered.
     * The qualified name of each such element is compared against
     * the names of any prohibited scripting elements. When found, the
     * corresponding counter is incremented.
     * If expressions representing request-time attribute values are
     * prohibited, it is also necessary to check the values of all
     * attributes specified by the element. (Trying to figure out
     * which attributes actually support request-time attribute values
     * and checking only those is far more trouble than it's worth.)
     */
    public void startElement (String namespaceUri, 
			      String localName, String qualifiedName,
			      Attributes atts) {
      if ((! allowDeclarations)
	  && qualifiedName.equals("jsp:declaration"))
	++declarationCount;
      else if ((! allowScriptlets)
	       && qualifiedName.equals("jsp:scriptlet"))
	++scriptletCount;
      else if ((! allowExpressions)
	       && qualifiedName.equals("jsp:expression"))
	++expressionCount;
      if (! allowRTExpressions) countRTExpressions(atts);
    }

    /**
     * Auxiliary method for checking attribute values to see if
     * are specified via request-time attribute values.
     * Expressions representing request-time attribute values are
     * recognized by their "%=" and "%" delimiters. When found, the
     * corresponding counter is incremented.
     */
    private void countRTExpressions (Attributes atts) {
      int stop = atts.getLength();
      for (int i = 0; i < stop; ++i) {
	String attval = atts.getValue(i);
	if (attval.startsWith("%=") && attval.endsWith("%"))
	  ++rtExpressionCount;
      }
    }

    /**
     * Constructs a String reporting the number(s) of prohibited
     * scripting elements that were detected, if any.
     * Returns null if no violations were found, making the result
     * of this method suitable for the return value of the
     * TagLibraryValidator.validate() method.
     * 
     * TODO:  The update from 7/13/2001 merely makes this validator
     * compliant with the new TLV API, but does not fully take advantage
     * of this API.  In the future, we should do so... but because
     * of the possibility that anti-script checking will be incorporated
     * into the base TLV, I've held off for now and just changed this
     * class to use the new API.  -- SB.
     */
    public ValidationMessage[] reportResults () {
      if (declarationCount + scriptletCount + expressionCount 
          + rtExpressionCount > 0) {
	StringBuffer results = new StringBuffer("JSP page contains ");
	boolean first = true;
	if (declarationCount > 0) {
	  results.append(Integer.toString(declarationCount));
	  results.append(" declaration");
	  if (declarationCount > 1) results.append('s');
	  first = false;
	}
	if (scriptletCount > 0) {
	  if (! first) results.append(", ");
	  results.append(Integer.toString(scriptletCount));
	  results.append(" scriptlet");
	  if (scriptletCount > 1) results.append('s');
	  first = false;
	}
	if (expressionCount > 0) {
	  if (! first) results.append(", ");
	  results.append(Integer.toString(expressionCount));
	  results.append(" expression");
	  if (expressionCount > 1) results.append('s');
	}
	if (rtExpressionCount > 0) {
	  if (! first) results.append(", ");
	  results.append(Integer.toString(rtExpressionCount));
	  results.append(" request-time attribute value");
	  if (rtExpressionCount > 1) results.append('s');
	}
	results.append(".");
	return vmFromString(results.toString());
      } else {
	return null;
      }
    }
  }


  // constructs a ValidationMessage[] from a single String and no ID
  private static ValidationMessage[] vmFromString(String message) {
    return new ValidationMessage[] {
      new ValidationMessage(null, message)
    };
  }

}
