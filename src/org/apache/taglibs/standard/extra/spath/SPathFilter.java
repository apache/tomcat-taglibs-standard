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

package org.apache.taglibs.standard.extra.spath;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// for main()
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;

/**
 * <p>Filters a SAX stream based on a single supplied SPath
 * expression.</p>
 *
 * @author Shawn Bayern
 */
public class SPathFilter extends XMLFilterImpl {

    //*********************************************************************
    // Protected state

    /** The steps in the SPath expression we use for filtering. */
    protected List steps;

    //*********************************************************************
    // Private state in support of filtering

    private int depth;				// depth in parsed document
    private Stack acceptedDepths;		// depth of acceptance
    private int excludedDepth;			// depth of exclusion

    private static final boolean DEBUG = false;

    //*********************************************************************
    // Main method (for testing)

    /** Simple command-line interface, mostly for testing. */
    public static void main(String args[])
	    throws ParseException, IOException, SAXException {
// temporary...
System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");

	// retrieve and parse the expression
	String expr = args[0];
	SPathParser s = new SPathParser(expr);
	Path p = s.expression();

	// construct the appropriate SAX chain
	// (reader -> us -> serializer)
	XMLReader r = XMLReaderFactory.createXMLReader();
	XMLFilter f1 = new SPathFilter(p);
	XMLFilter f2 = new XMLFilterImpl();
	f1.setParent(r);
	f2.setParent(f1);
	Serializer sz = SerializerFactory.getSerializer
	    (OutputProperties.getDefaultMethodProperties("xml"));
	sz.setOutputStream(System.out);
	f2.setContentHandler(sz.asContentHandler());

	// go!
	f2.parse(new InputSource(System.in));
	System.out.println();
    }

    //*********************************************************************
    // Constructor and initialization methods

    /** Constructs a new SPathFilter, given a Path. */
    public SPathFilter(Path path) {
	init();
	this.steps = path.getSteps();
    }

    /** Initializes state used for filtering. */
    private void init() {
	depth = 0;
	excludedDepth = -1;
	acceptedDepths = new Stack();
    }

    //*********************************************************************
    // ContentHandler methods

    // startElement() and endElement() both require and modify filter
    // state.  They contain and direct the bulk of the filter's operation.

    /** Filter for startElement(). */
    public void startElement(String uri,
			     String localName,
			     String qName,
			     Attributes a) throws SAXException {
	// always update the depth
	depth++;

	// if we're in an accepted section, simply pass through
	if (isAccepted()) {
	    getContentHandler().startElement(uri, localName, qName, a);
	    return;
	}

	// likewise, if we're excluded, then simply block and return
	if (isExcluded())
	    return;

	// now, not accepted or excluded, let's see if we've got a match.
	// we need to get the appropriate step based on the number of
	// steps we've previously accepted
	Step currentStep = (Step) steps.get(acceptedDepths.size());

	if (nodeMatchesStep(currentStep, uri, localName, qName, a)) {
	    if (DEBUG)
		System.err.println("*** Progressive match (" + acceptedDepths.size() + "): " + localName);
	    // new match (progressive)
	    acceptedDepths.push(new Integer(depth - 1));

	    // is it enough?  give acceptance another chance...
	    if (isAccepted())
	        getContentHandler().startElement(uri, localName, qName, a);
	} else if (!currentStep.isDepthUnlimited()) {
	    // if the step was preceded by '/' instead of '//', then
	    // we can't have a match at this node or beneath it
	    excludedDepth = depth - 1;
	}

	// nothing left to check; no reason to include node
        return;
    }

    /** Filter for endElement(). */
    public void endElement(String uri, String localName, String qName)
	    throws SAXException {
	// reduce the depth
	depth--;

	if (isExcluded()) {
	    // determine if exclusion ends with us
	    if (excludedDepth == depth)
	        excludedDepth = -1;

	    // either way, we have been excluded, so pass nothing through
	    return;
	}

	// if we're excepted (for now), include ourselves...
	if (isAccepted())
	    getContentHandler().endElement(uri, localName, qName);

	    if (DEBUG) {
		System.err.println("***   Closing tag: " + localName);
		System.err.println("***   acceptedDepths.size(): " + acceptedDepths.size());
		System.err.println("***   last accepted depth: " + ((Integer)acceptedDepths.peek()).intValue());
		System.err.println("***   depth: " + depth);
	    }

	// now, back off if we correspond to a "successful" start tag
        if (acceptedDepths.size() > 0 &&
		(((Integer)acceptedDepths.peek()).intValue()) == depth)
	    acceptedDepths.pop();
    }

    // The remaining ContentHandler functions require only one bit of
    // state:  are we in a mode where we pass them through, or does
    // the current state dictate that we ignore them.  They need no other
    // information and cannot have any effect on the current state.

    /** Filter for ignoreableWhitespace(). */
    public void ignorableWhitespace(char[] ch, int start, int length)
	    throws SAXException {
	if (isAccepted())
	    getContentHandler().ignorableWhitespace(ch, start, length);
    }

    /** Filter for characters(). */
    public void characters(char[] ch, int start, int length)
	    throws SAXException {
	if (isAccepted())
	    getContentHandler().characters(ch, start, length);
    }

    /** Filter for startPrefixMapping(). */
    public void startPrefixMapping(String prefix, String uri)
	    throws SAXException {
	if (isAccepted())
	    getContentHandler().startPrefixMapping(prefix, uri);
    }

    /** Filter for endPrefixMapping(). */
    public void endPrefixMapping(String prefix)
	    throws SAXException {
	if (isAccepted())
	    getContentHandler().endPrefixMapping(prefix);
    }

    /** Filter for processingInstruction(). */
    public void processingInstruction(String target, String data)
	    throws SAXException {
	if (isAccepted())
	    getContentHandler().processingInstruction(target, data);
    }

    /** Filter for skippedEntity(). */
    public void skippedEntity(String name) throws SAXException {
	if (isAccepted())
	    getContentHandler().skippedEntity(name);
    }

    // We reset state in startDocument(), in case we're reused
    /** Resets state. */
    public void startDocument() {
	init();
    }

    //*********************************************************************
    // Private utility methods

    public static boolean nodeMatchesStep(Step s,
				  String uri,
				  String localName,
				  String qName,
				  Attributes a) {
	// if the name doesn't match, then we've got a loser
	if (!s.isMatchingName(uri, localName))
	    return false;

	// it's still in the game; check the predicates
	List l = s.getPredicates();
	for (int i = 0; l != null && i < l.size(); i++) {
	    Predicate p = (Predicate) l.get(i);
	    if (!(p instanceof AttributePredicate))
		throw new UnsupportedOperationException
		    ("only attribute predicates are supported by filter");
	    if (!((AttributePredicate) p).isMatchingAttribute(a))
		return false;		// all predicates must match
	}

	// it's survived
	return true;
    }

    /** Returns true if events should be passed through, false otherwise. */
    private boolean isAccepted() {
	return (acceptedDepths.size() >= steps.size());
    }

    /** Returns true if events should be blocked, false otherwise. */
    private boolean isExcluded() {
	return (excludedDepth != -1);
    }
}
