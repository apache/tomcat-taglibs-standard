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
 * <p>A SAX-based TagLibraryValidator for the JSTL SQL tag library.
 * 
 * @author Shawn Bayern
 */
public class JstlSqlTLV extends JstlBaseTLV {

    //*********************************************************************
    // Constants

    // tag names
    private final String SETDATASOURCE = "setDataSource";
    private final String QUERY = "query";
    private final String UPDATE = "update";
    private final String TRANSACTION = "transaction";
    private final String PARAM = "param";
    private final String DATEPARAM = "dateParam";

    private final String JSP_TEXT = "jsp:text";

    // attribute names
    private final String SQL = "sql";
    private final String DATASOURCE = "dataSource";


    //*********************************************************************
    // Contract fulfillment

    protected DefaultHandler getHandler() {
	return new Handler();
    }


    //*********************************************************************
    // SAX event handler

    /** The handler that provides the base of our implementation. */
    private class Handler extends DefaultHandler {

	// parser state
	private int depth = 0;
        private Stack queryDepths = new Stack();
        private Stack updateDepths = new Stack();
        private Stack transactionDepths = new Stack();
	private String lastElementName = null;
	private boolean bodyNecessary = false;
	private boolean bodyIllegal = false;

	// process under the existing context (state), then modify it
	public void startElement(
	        String ns, String ln, String qn, Attributes a) {

	    // substitute our own parsed 'ln' if it's not provided
	    if (ln == null)
		ln = getLocalPart(qn);

	    // for simplicity, we can ignore <jsp:text> for our purposes
	    // (don't bother distinguishing between it and its characters)
	    if (qn.equals(JSP_TEXT))
		return;

	    // check body-related constraint
	    if (bodyIllegal)
		fail(Resources.getMessage("TLV_ILLEGAL_BODY", lastElementName));

	    // validate expression syntax if we need to
	    Set expAtts;
	    if (qn.startsWith(prefix + ":")
		    && (expAtts = (Set) config.get(ln)) != null) {
		for (int i = 0; i < a.getLength(); i++) {
		    String attName = a.getLocalName(i);
		    if (expAtts.contains(attName)) {
			String vMsg =
			    validateExpression(
				ln,
				attName,
				a.getValue(i));
			if (vMsg != null)
			    fail(vMsg);
		    }
		}
	    }

            // validate attributes
            if (qn.startsWith(prefix + ":") && !hasNoInvalidScope(a))
                fail(Resources.getMessage("TLV_INVALID_ATTRIBUTE",
                    SCOPE, qn, a.getValue(SCOPE))); 
	    if (qn.startsWith(prefix + ":") && hasDanglingScope(a))
		fail(Resources.getMessage("TLV_DANGLING_SCOPE", qn));

	    // now, modify state

            /*
             * Make sure <sql:param> is nested inside <sql:query> or
             * <sql:update>. Note that <sql:param> does not need to
             * be a direct child of <sql:query> or <sql:update>.
             * Otherwise, the following would not work:
             *
             *  <sql:query sql="..." var="...">
             *   <c:forEach var="arg" items="...">
             *    <sql:param value="${arg}"/>
             *   </c:forEach>
             *  </sql:query>
             */
            if ( (isTag(qn, PARAM) || isTag(qn, DATEPARAM)) 
                && (queryDepths.empty() && updateDepths.empty()) ) {
                fail(Resources.getMessage("SQL_PARAM_OUTSIDE_PARENT"));
            }

            // If we're in a <query>, record relevant state
            if (isTag(qn, QUERY)) {
                queryDepths.push(new Integer(depth));
            }
            // If we're in a <update>, record relevant state
            if (isTag(qn, UPDATE)) {
                updateDepths.push(new Integer(depth));
            }
            // If we're in a <transaction>, record relevant state
            if (isTag(qn, TRANSACTION)) {
                transactionDepths.push(new Integer(depth));
            }

	    // set up a check against illegal attribute/body combinations
	    bodyIllegal = false;
	    bodyNecessary = false;

            if (isTag(qn, QUERY) || isTag(qn, UPDATE)) {
                if (!hasAttribute(a, SQL)) {
                    bodyNecessary = true;
                }
                if (hasAttribute(a, DATASOURCE) && !transactionDepths.empty()) {
                    fail(Resources.getMessage("ERROR_NESTED_DATASOURCE"));
                }
            }

            if (isTag(qn, DATEPARAM)) {
                bodyIllegal = true;
            }

	    // record the most recent tag (for error reporting)
	    lastElementName = qn;
	    lastElementId = a.getValue("http://java.sun.com/JSP/Page", "id");

	    // we're a new element, so increase depth
	    depth++;
	}

	public void characters(char[] ch, int start, int length) {

	    bodyNecessary = false;		// body is no longer necessary!

	    // ignore strings that are just whitespace
	    String s = new String(ch, start, length).trim();
	    if (s.equals(""))
		return;

	    // check and update body-related constraints
	    if (bodyIllegal)
		fail(Resources.getMessage("TLV_ILLEGAL_BODY", lastElementName));
	}

	public void endElement(String ns, String ln, String qn) {

	    // consistently, we ignore JSP_TEXT
	    if (qn.equals(JSP_TEXT))
		return;

	    // handle body-related invariant
	    if (bodyNecessary)
		fail(Resources.getMessage("TLV_MISSING_BODY",
		    lastElementName));
	    bodyIllegal = false;	// reset: we've left the tag

            // update <query>-related state
            if (isTag(qn, QUERY)) {
                queryDepths.pop();
            }
            // update <update>-related state
            if (isTag(qn, UPDATE)) {
                updateDepths.pop();
            }
            // update <update>-related state
            if (isTag(qn, TRANSACTION)) {
                transactionDepths.pop();
            }

	    // update our depth
	    depth--;
	}
    }
}
