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
 * <p>A SAX-based TagLibraryValidator for the JSTL XML library.
 * Currently implements the following checks:</p>
 * 
 * <ul>
 *   <li>Expression syntax validation.
 *   <li>Choose / when / otherwise constraints</li>
 *   <li>Tag bodies that must either be empty or non-empty given
 *      particular attributes.</li>
 *   <li>Other minor constraints.</li>
 * </ul>
 * 
 * @author Shawn Bayern
 */
public class JstlXmlTLV extends JstlBaseTLV {

    //*********************************************************************
    // Implementation Overview

    /*
     * We essentially just run the page through a SAX parser, handling
     * the callbacks that interest us.  We collapse <jsp:text> elements
     * into the text they contain, since this simplifies processing
     * somewhat.  Even a quick glance at the implementation shows its
     * necessary, tree-oriented nature:  multiple Stacks, an understanding
     * of 'depth', and so on all are important as we recover necessary
     * state upon each callback.  This TLV demonstrates various techniques,
     * from the general "how do I use a SAX parser for a TLV?" to
     * "how do I read my init parameters and then validate?"  But also,
     * the specific SAX methodology was kept as general as possible to
     * allow for experimentation and flexibility.
     *
     * Much of the code and structure is duplicated from JstlCoreTLV.
     * An effort has been made to re-use code where unambiguously useful.
     * However, splitting logic among parent/child classes isn't
     * necessarily the cleanest approach when writing a parser like the
     * one we need.  I'd like to reorganize this somewhat, but it's not
     * a priority.
     */


    //*********************************************************************
    // Constants

    // tag names
    private final String CHOOSE = "choose";
    private final String WHEN = "when";
    private final String OTHERWISE = "otherwise";
    private final String PARSE = "parse";
    private final String PARAM = "param";
    private final String TRANSFORM = "transform";
    private final String JSP_TEXT = "jsp:text";

    // attribute names
    private final String EVAL = "evaluator";
    private final String VALUE = "value";
    private final String SOURCE = "source";


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
	private Stack chooseDepths = new Stack();
	private Stack chooseHasOtherwise = new Stack();
	// private Stack expressionLanguage = new Stack();
	private String lastElementName = null;
	private boolean bodyNecessary = false;
	private boolean bodyIllegal = false;
	private Stack transformWithSource = new Stack();

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

	    // check invariants for <choose>
	    if (chooseChild()) {
		// ensure <choose> has the right children
		if(!isXmlTag(ns, ln, WHEN) && !isXmlTag(ns, ln, OTHERWISE)) {
		    fail(Resources.getMessage("TLV_ILLEGAL_CHILD_TAG",
			prefix, CHOOSE, qn));
		}

		// make sure <otherwise> is the last tag
		if (((Boolean) chooseHasOtherwise.peek()).booleanValue()) {
		   fail(Resources.getMessage("TLV_ILLEGAL_ORDER",
			qn, prefix, OTHERWISE, CHOOSE));
		}
		if (isXmlTag(ns, ln, OTHERWISE)) {
		    chooseHasOtherwise.pop();
		    chooseHasOtherwise.push(new Boolean(true));
		}

	    }

	    // Specific check, directly inside <transform source="...">
	    if (!transformWithSource.empty() &&
		    topDepth(transformWithSource) == (depth - 1)) {
		// only allow <param>
		if (!isXmlTag(ns, ln, PARAM))
		    fail(Resources.getMessage("TLV_ILLEGAL_BODY",
			prefix + ":" + TRANSFORM));

		// thus, if we get the opportunity to hit depth++,
		// we know we've got a <param> subtag
	    }

	    // now, modify state

	    // we're a choose, so record new choose-specific state
	    if (isXmlTag(ns, ln, CHOOSE)) {
		chooseDepths.push(new Integer(depth));
		chooseHasOtherwise.push(new Boolean(false));
	    }

	    // set up a check against illegal attribute/body combinations
	    bodyIllegal = false;
	    bodyNecessary = false;
	    if (isXmlTag(ns, ln, PARSE)) {
		if (hasAttribute(a, SOURCE))
		    bodyIllegal = true;
	    } else if (isXmlTag(ns, ln, PARAM)) {
		if (hasAttribute(a, VALUE))
		    bodyIllegal = true;
		else
		    bodyNecessary = true;
	    } else if (isXmlTag(ns, ln, TRANSFORM)) {
		if (hasAttribute(a, SOURCE))
		    transformWithSource.push(new Integer(depth));
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

	    // make sure <choose> has no non-whitespace text
	    if (chooseChild()) {
		String msg = 
		    Resources.getMessage("TLV_ILLEGAL_TEXT_BODY",
			prefix, CHOOSE,
			(s.length() < 7 ? s : s.substring(0,7)));
		fail(msg);
	    }

            // Specific check, directly inside <transform source="...">
            if (!transformWithSource.empty()
		    && topDepth(transformWithSource) == (depth - 1)) {
                fail(Resources.getMessage("TLV_ILLEGAL_BODY",
                    prefix + ":" + TRANSFORM));
            }
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

	    // update <choose>-related state
	    if (isXmlTag(ns, ln, CHOOSE)) {
		chooseDepths.pop();
		chooseHasOtherwise.pop();
	    }

	    // update <transform source="...">-related state
	    if (!transformWithSource.empty()
		    && topDepth(transformWithSource) == (depth - 1))
		transformWithSource.pop();

	    // update our depth
	    depth--;
	}

	// are we directly under a <choose>?
	private boolean chooseChild() {
	    return (!chooseDepths.empty()
		&& (depth - 1) == ((Integer) chooseDepths.peek()).intValue());
	}

        // returns the top int depth (peeked at) from a Stack of Integer
        private int topDepth(Stack s) {
            return ((Integer) s.peek()).intValue();
        }
    }
}
