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

package org.apache.taglibs.standard.tag.common.xml;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;transform&gt;, the XML transformation
 * tag.</p>
 *
 * @author Shawn Bayern
 */
public abstract class TransformSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected Object source;                       // 'source' attribute
    protected Object xslt;			   // 'xslt attribute
    protected Transformer transformer;		   // 'transformer' attribute
    protected Result result;			   // 'result' attribute

    //*********************************************************************
    // Private state

    private String var;                            // 'var' attribute
    private int scope;				   // processed 'scope' attr
    private Transformer t;			   // actual Transformer
    private TransformerFactory tf;		   // reusable factory
    private DocumentBuilder db;			   // reusable factory


    //*********************************************************************
    // Constructor and initialization

    public TransformSupport() {
	super();
	init();
    }

    private void init() {
	source = null;
	xslt = null;
	transformer = null;
	var = null;
	result = null;
	tf = null;
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    public int doStartTag() throws JspException {
      /*
       * We can set up our Transformer here, so we do so, and we let
       * it receive parameters directly from subtags (instead of
       * caching them.
       */
      try {

	//************************************
	// Initialize

	// set up the TransformerFactory if necessary
        if (tf == null)
            tf = TransformerFactory.newInstance();

	//************************************
	// Determine transformer

	// we can assume only one of 'xslt' or 'transformer' is specified
	if (transformer != null)
	    t = transformer;
	else {
	    // assume 'xslt'
	    if (xslt == null)
		throw new JspTagException(
		    Resources.getMessage("TRANSFORM_NO_TRANSFORMER"));
	    t = tf.newTransformer(getSource(xslt));
	}

	return EVAL_BODY_BUFFERED;

      } catch (TransformerConfigurationException ex) {
	throw new JspTagException(ex.toString());
      }
    }

    // parse 'source' or body, transform via 'xslt' or 'transformer',
    // store as 'var' or 'result'
    public int doEndTag() throws JspException {
      try {

	//************************************
	// Initialize

	// set up our DocumentBuilderFactory if necessary
	if (db == null)
	    db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	//************************************
	// Determine source XML

	// if we haven't gotten a source, use the body (which may be empty)
	Object source = this.source;
	if (source == null)
	    source = bodyContent.getString();

	// let the Source be with you
	Source xml = getSource(source);

	//************************************
	// Conduct the transformation

	// we can assume at most one of 'var' or 'result' is specified
	if (result != null)
	    // we can write directly to the Result
	    t.transform(xml, result);
	else if (var != null) {
	    // we need a Document
	    Document d = db.newDocument();
	    Result doc = new DOMResult(d);
	    t.transform(xml, doc);
	    pageContext.setAttribute(var, d, scope);
	} else {
	    /*
	     * We're going to output the text directly.  I'd love to
	     * construct a StreamResult directly from pageContext.getOut(),
	     * but I can't trust the transformer not to flush our writer.
	     */
	    StringWriter bufferedResult = new StringWriter();
	    Result page = new StreamResult(bufferedResult);
	    t.transform(xml, page);
	    pageContext.getOut().print(bufferedResult);
	}

	return EVAL_PAGE;
      } catch (IOException ex) {
	throw new JspTagException(ex.toString());
      } catch (ParserConfigurationException ex) {
	throw new JspTagException(ex.toString());
      } catch (TransformerException ex) {
	throw new JspTagException(ex.toString());
      }
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Public methods for subtags

    /** Sets a transformation parameter on our transformer. */
    public void setParameter(String name, String value) {
	t.setParameter(name, value);
    }


    //*********************************************************************
    // Public utility methods

    /**
     * Retrieves a Source from the given Object, whether it be a String,
     * Reader, or Source (the latter of which simply results in an identity).
     * A null input results in a null output.
     */
    public static Source getSource(Object o) {
	if (o == null || o instanceof Source)
	    return null;
	if (o instanceof String)
	    o = new StringReader((String)o);
	return new StreamSource((Reader)o);
    }


    //*********************************************************************
    // Tag attributes

    public void setVar(String var) {
	this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }

}
