/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

package org.apache.taglibs.standard.tlv;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluator;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>A base class to support SAX-based validation in JSTL.</p>
 * 
 * @author Shawn Bayern
 */
public abstract class JstlBaseTLV extends TagLibraryValidator {

    //*********************************************************************
    // Implementation Overview

    /*
     * We essentially just run the page through a SAX parser, handling
     * the callbacks that interest us.  The SAX parser is supplied by
     * subclasses using the protected getHandler() method.
     */

    protected abstract DefaultHandler getHandler();


    //*********************************************************************
    // Constants

    // parameter names
    private final String EXP_ATT_PARAM = "expressionAttributes";

    // attributes
    protected static final String VAR = "var";
    protected static final String SCOPE = "scope";  

    //scopes
    protected static final String PAGE_SCOPE = "page";        
    protected static final String REQUEST_SCOPE = "request";  
    protected static final String SESSION_SCOPE = "session";  
    protected static final String APPLICATION_SCOPE = "application";

    // Relevant URIs
    protected final String JSP = "http://java.sun.com/JSP/Page"; 
    
    // JSTL 1.1
    private final String CORE = "http://java.sun.com/jsp/jstl/core";
    private final String FMT = "http://java.sun.com/jsp/jstl/fmt";
    private final String SQL = "http://java.sun.com/jsp/jstl/sql";
    private final String XML = "http://java.sun.com/jsp/jstl/xml";

    // JSTL 1.0 EL
    private final String CORE_EL = "http://java.sun.com/jstl/core";
    private final String FMT_EL = "http://java.sun.com/jstl/fmt";
    private final String SQL_EL = "http://java.sun.com/jstl/sql";
    private final String XML_EL = "http://java.sun.com/jstl/xml";

    // JSTL 1.0 RT
    private final String CORE_RT = "http://java.sun.com/jstl/core_rt";
    private final String FMT_RT = "http://java.sun.com/jstl/fmt_rt";
    private final String SQL_RT = "http://java.sun.com/jstl/sql_rt";
    private final String XML_RT = "http://java.sun.com/jstl/xml_rt";

    //*********************************************************************
    // Validation and configuration state (protected)

    protected String prefix;			// our taglib's prefix
    protected Vector messageVector;		// temporary error messages
    protected Map config;			// configuration (Map of Sets)
    protected boolean failed;			// have we failed >0 times?
    protected String lastElementId;		// the last element we've seen

    //*********************************************************************
    // Constructor and lifecycle management

    public JstlBaseTLV() {
	super();
	init();
    }

    private void init() {
	messageVector = null;
	prefix = null;
	config = null;
    }

    public void release() {
	super.release();
	init();
    }
    

    //*********************************************************************
    // Validation entry point

    public synchronized ValidationMessage[] validate(
	    String prefix, String uri, PageData page) {
	try {

	    // initialize
	    messageVector = new Vector();

	    // save the prefix
	    this.prefix = prefix;

	    // parse parameters if necessary
	    try {
		if (config == null)
		    configure((String) getInitParameters().get(EXP_ATT_PARAM));
	    } catch (NoSuchElementException ex) {
		// parsing error
	        return vmFromString(
		    Resources.getMessage("TLV_PARAMETER_ERROR",
			EXP_ATT_PARAM));
	    }

	    // get a handler
	    DefaultHandler h = getHandler();

	    // parse the page
	    SAXParserFactory f = SAXParserFactory.newInstance();
	    f.setValidating(false);
	    f.setNamespaceAware(true);
	    SAXParser p = f.newSAXParser();
	    p.parse(page.getInputStream(), h);

	    if (messageVector.size() == 0)
		return null;
	    else
		return vmFromVector(messageVector);

	} catch (SAXException ex) {
	    return vmFromString(ex.toString());
	} catch (ParserConfigurationException ex) {
	    return vmFromString(ex.toString());
	} catch (IOException ex) {
	    return vmFromString(ex.toString());
	}
    }

    //*********************************************************************
    // Protected utility functions

    // delegate validation to the appropriate expression language
    protected String validateExpression(
	    String elem, String att, String expr) {

	// let's just use the cache kept by the ExpressionEvaluatorManager
	ExpressionEvaluator current;
	try {
	    current =
	        ExpressionEvaluatorManager.getEvaluatorByName(
                  ExpressionEvaluatorManager.EVALUATOR_CLASS);
	} catch (JspException ex) {
	    // (using JspException here feels ugly, but it's what EEM uses)
	    return ex.getMessage();
	}
	
	String response = current.validate(att, expr);
	if (response == null)
	    return response;
	else
	    return "tag = '" + elem + "' / attribute = '" + att + "': "
		+ response;
    }

    // utility methods to help us match elements in our tagset
    protected boolean isTag(String tagUri,
			    String tagLn,
			    String matchUri,
			    String matchLn) {
	if (tagUri == null
	        || tagLn == null
		|| matchUri == null
		|| matchLn == null)
	    return false;
        return (tagUri.equals(matchUri) && tagLn.equals(matchLn));
    }

    protected boolean isJspTag(String tagUri, String tagLn, String target) {
        return isTag(tagUri, tagLn, JSP, target);
    }

    protected boolean isCoreTag(String tagUri, String tagLn, String target) {
        return (isTag(tagUri, tagLn, CORE, target)
	     || isTag(tagUri, tagLn, CORE_EL, target)
	     || isTag(tagUri, tagLn, CORE_RT, target));
    }

    protected boolean isFmtTag(String tagUri, String tagLn, String target) {
        return (isTag(tagUri, tagLn, FMT, target) 
	     || isTag(tagUri, tagLn, FMT_EL, target)
	     || isTag(tagUri, tagLn, FMT_RT, target));
    }

    protected boolean isSqlTag(String tagUri, String tagLn, String target) {
        return (isTag(tagUri, tagLn, SQL, target) 
	     || isTag(tagUri, tagLn, SQL_EL, target)
	     || isTag(tagUri, tagLn, SQL_RT, target));
    }

    protected boolean isXmlTag(String tagUri, String tagLn, String target) {
        return (isTag(tagUri, tagLn, XML, target)
	     || isTag(tagUri, tagLn, XML_EL, target)
	     || isTag(tagUri, tagLn, XML_RT, target));
    }

    // utility method to determine if an attribute exists
    protected boolean hasAttribute(Attributes a, String att) {
        return (a.getValue(att) != null);
    }

    /*
     * method to assist with failure [ as if it's not easy enough
     * already :-) ]
     */
    protected void fail(String message) {
        failed = true;
        messageVector.add(new ValidationMessage(lastElementId, message));
    }

    // returns true if the given attribute name is specified, false otherwise
    protected boolean isSpecified(TagData data, String attributeName) {
        return (data.getAttribute(attributeName) != null);
    }

    // returns true if the 'scope' attribute is valid
    protected boolean hasNoInvalidScope(Attributes a) {
        String scope = a.getValue(SCOPE);

	if ((scope != null)
	    && !scope.equals(PAGE_SCOPE)
	    && !scope.equals(REQUEST_SCOPE)
	    && !scope.equals(SESSION_SCOPE)
	    && !scope.equals(APPLICATION_SCOPE))
	    return false;

        return true;
    }

    // returns true if the 'var' attribute is empty
    protected boolean hasEmptyVar(Attributes a) {
	if ("".equals(a.getValue(VAR)))
	    return true;
	return false;
    }

    // returns true if the 'scope' attribute is present without 'var'
    protected boolean hasDanglingScope(Attributes a) {
	return (a.getValue(SCOPE) != null && a.getValue(VAR) == null);
    }

    // retrieves the local part of a QName
    protected String getLocalPart(String qname) {
	int colon = qname.indexOf(":");
	if (colon == -1)
	    return qname;
	else
	    return qname.substring(colon + 1);
    }

    //*********************************************************************
    // Miscellaneous utility functions

    // parses our configuration parameter for element:attribute pairs
    private void configure(String info) {
        // construct our configuration map
	config = new HashMap();

	// leave the map empty if we have nothing to configure
	if (info == null)
	    return;

	// separate parameter into space-separated tokens and store them
	StringTokenizer st = new StringTokenizer(info);
	while (st.hasMoreTokens()) {
	    String pair = st.nextToken();
	    StringTokenizer pairTokens = new StringTokenizer(pair, ":");
	    String element = pairTokens.nextToken();
	    String attribute = pairTokens.nextToken();
	    Object atts = config.get(element);
	    if (atts == null) {
	        atts = new HashSet();
	        config.put(element, atts);
	    }
	    ((Set) atts).add(attribute);
	}
    }

    // constructs a ValidationMessage[] from a single String and no ID
    static ValidationMessage[] vmFromString(String message) {
	return new ValidationMessage[] {
	    new ValidationMessage(null, message)
	};
    }

    // constructs a ValidationMessage[] from a ValidationMessage Vector
    static ValidationMessage[] vmFromVector(Vector v) {
	ValidationMessage[] vm = new ValidationMessage[v.size()];
	for (int i = 0; i < vm.length; i++)
	   vm[i] = (ValidationMessage) v.get(i);
	return vm;
    }
}
