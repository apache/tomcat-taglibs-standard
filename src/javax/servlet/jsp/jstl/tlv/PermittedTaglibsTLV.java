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

package javax.servlet.jsp.jstl.tlv;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * <p>A TagLibraryValidator class to allow a TLD to restrict what
 * taglibs (in addition to itself) may be imported on a page where it's
 * used.</p>
 *
 * @author Shawn Bayern
 */
public class PermittedTaglibsTLV extends TagLibraryValidator {

    //*********************************************************************
    // Constants

    // parameter names
    private final String PERMITTED_TAGLIBS_PARAM = "permittedTaglibs";

    // URI for "<jsp:root>" element
    private final String JSP_ROOT_URI = "http://java.sun.com/JSP/Page";

    // local name of "<jsp:root>" element
    private final String JSP_ROOT_NAME = "root";

    // QName for "<jsp:root>" element
    private final String JSP_ROOT_QN = "jsp:root";


    //*********************************************************************
    // Validation and configuration state (protected)

    private Set permittedTaglibs;		// what URIs are allowed?
    private boolean failed;			// did the page fail?
    private String uri;				// our taglib's URI

    //*********************************************************************
    // Constructor and lifecycle management

    public PermittedTaglibsTLV() {
	super();
	init();
    }

    private void init() {
	permittedTaglibs = null;
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
	    this.uri = uri;
	    permittedTaglibs = readConfiguration();

	    // get a handler
	    DefaultHandler h = new PermittedTaglibsHandler();

	    // parse the page
	    SAXParserFactory f = SAXParserFactory.newInstance();
	    f.setValidating(true);
	    SAXParser p = f.newSAXParser();
	    p.parse(page.getInputStream(), h);

	    if (failed)
		return vmFromString(
		    "taglib " + prefix + " (" + uri + ") allows only the "
		    + "following taglibs to be imported: " + permittedTaglibs);
	    else
		return null;

	} catch (SAXException ex) {
	    return vmFromString(ex.toString());
	} catch (ParserConfigurationException ex) {
	    return vmFromString(ex.toString());
	} catch (IOException ex) {
	    return vmFromString(ex.toString());
	}
    }


    //*********************************************************************
    // Utility functions

    /** Returns Set of permitted taglibs, based on configuration data. */
    private Set readConfiguration() {

	// initialize the Set
	Set s = new HashSet();

	// get the space-separated list of taglibs
	String uris = (String) getInitParameters().get(PERMITTED_TAGLIBS_PARAM);

        // separate the list into individual uris and store them
        StringTokenizer st = new StringTokenizer(uris);
        while (st.hasMoreTokens())
	    s.add(st.nextToken());

	// return the new Set
	return s;

    }

    // constructs a ValidationMessage[] from a single String and no ID
    private ValidationMessage[] vmFromString(String message) {
	return new ValidationMessage[] {
	    new ValidationMessage(null, message)
	};
    }


    //*********************************************************************
    // SAX handler

    /** The handler that provides the base of our implementation. */
    private class PermittedTaglibsHandler extends DefaultHandler {

        // if the element is <jsp:root>, check its "xmlns:" attributes
        public void startElement(
                String ns, String ln, String qn, Attributes a) {

	    // ignore all but <jsp:root>
	    if (!qn.equals(JSP_ROOT_QN) &&
	            (!ns.equals(JSP_ROOT_URI) || !ln.equals(JSP_ROOT_NAME)))
		return;

	    // for <jsp:root>, check the attributes
	    for (int i = 0; i < a.getLength(); i++) {
		String name = a.getQName(i);

		// ignore non-namespace attributes, and xmlns:jsp
		if (!name.startsWith("xmlns:") || name.equals("xmlns:jsp"))
		    continue;

		String value = a.getValue(i);
		// ignore our own namespace declaration
		if (value.equals(uri))
		    continue;

		// otherwise, ensure that 'value' is in 'permittedTaglibs' set
		if (!permittedTaglibs.contains(value))
		    failed = true;
	    }
	}
    }

}
