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

package org.apache.taglibs.standard.tag.common.core;

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.net.URLEncoder;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;param&gt;, the URL parameter
 * subtag for &lt;import&gt; in JSTL 1.0.</p>
 *
 * @see ParamParent, ImportSupport, URLEncodeSupport
 * @author Shawn Bayern
 */

public abstract class ParamSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected String name;                       // 'name' attribute
    protected String value;                      // 'value' attribute

    /**
     * There used to be an 'encode' attribute; I've left this as a
     * vestige in case custom subclasses want to use our functionality
     * but NOT encode parameters.
     */
    protected boolean encode = true;

    //*********************************************************************
    // Constructor and initialization

    public ParamSupport() {
	super();
	init();
    }

    private void init() {
	name = value = null;
    }


    //*********************************************************************
    // Tag logic

    // simply send our name and value to our appropriate ancestor
    public int doEndTag() throws JspException {
	Tag t = findAncestorWithClass(this, ParamParent.class);
	if (t == null)
	    throw new JspTagException(
		Resources.getMessage("PARAM_OUTSIDE_PARENT"));

	// take no action for null or empty names
	if (name == null || name.equals(""))
	    return EVAL_PAGE;

	// send the parameter to the appropriate ancestor
	ParamParent parent = (ParamParent) t;
	String value = this.value;
	if (value == null) {
	    if (bodyContent == null || bodyContent.getString() == null)
		value = "";
	    else
		value = bodyContent.getString().trim();
	}
	if (encode) {
	    parent.addParameter(
		URLEncoder.encode(name), URLEncoder.encode(value));
	} else
	    parent.addParameter(name, value);
	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }

    //*********************************************************************
    // Support for parameter management

    /** 
     * Provides support for aggregating query parameters in URLs.
     * Specifically, accepts a series of parameters, ensuring that
     *  - newer parameters will precede older ones in the output URL
     *  - all supplied parameters precede those in the input URL
     */
    public static class ParamManager {

        //*********************************
        // Private state

	private List names = new LinkedList();
        private List values = new LinkedList();
	private boolean done = false;
        
	//*********************************
        // Public interface

	/** Adds a new parameter to the list. */
        public void addParameter(String name, String value) {
	    if (done)
		throw new IllegalStateException();
	    if (name != null) {
	        names.add(name);
	        if (value != null)
		    values.add(value);
	        else
		    values.add("");
	    }
	}

	/**
         * Produces a new URL with the stored parameters, in the appropriate
         * order.
         */
	public String aggregateParams(String url) {
	    /* 
             * Since for efficiency we're destructive to the param lists,
             * we don't want to run multiple times.
             */
	    if (done)
		throw new IllegalStateException();
	    done = true;

	    //// reverse the order of our two lists
	    // Collections.reverse(this.names);
	    // Collections.reverse(this.values);

	    // build a string from the parameter list 
	    StringBuffer newParams = new StringBuffer();
	    for (int i = 0; i < names.size(); i++) {
		newParams.append(names.get(i) + "=" + values.get(i));
		if (i < (names.size() - 1))
		    newParams.append("&");
	    }

	    // insert these parameters into the URL as appropriate
	    if (newParams.length() > 0) {
	        int questionMark = url.indexOf('?');
	        if (questionMark == -1) {
		    return (url + "?" + newParams);
	        } else {
		    StringBuffer workingUrl = new StringBuffer(url);
		    workingUrl.insert(questionMark + 1, (newParams + "&"));
		    return workingUrl.toString();
	        }
	    } else {
		return url;
	    }
	}
    }
}
