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

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.IteratorTagSupport;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for the XML library's &lt;forEach&gt; tag.</p>
 *
 * @see javax.servlet.jsp.jstl.core.IteratorTagSupport
 * @author Shawn Bayern
 */
public class ForEachTag extends IteratorTagSupport {

    //*********************************************************************
    // Private state

    private String select;				// tag attribute
    private List nodes;					// XPath result
    private int nodesIndex;

    //*********************************************************************
    // Iteration control methods

    protected boolean hasNext() throws JspTagException {
        return (nodesIndex < nodes.size());
    }

    protected Object next() throws JspTagException {
        return (nodes.get(nodesIndex));
    }


    //*********************************************************************
    // Tag logic

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
        super.release();
    }

    // Establishes list of context nodes over which to iterate
    public int doStartTag() throws JspException {
	nodesIndex = 0;
        try {
            XPathUtil xu = new XPathUtil(pageContext);
            nodes = xu.selectNodes(XPathUtil.getContext(this), select);
        } catch (org.saxpath.SAXPathException ex) {
            throw new JspTagException(ex.toString());
        }

	// now we're ready
	return super.doStartTag();
    }

    // Increments internal counter
    public int doAfterBody() throws JspException {
	nodesIndex++;			// insert ourselves & increment counter
	return super.doAfterBody();	// chain to parent
    }

    //*********************************************************************
    // Attribute accessors

    public void setSelect(String select) {
	this.select = select;
    }


    //*********************************************************************
    // Public methods for subtags

    /* Retrieves the current context. */
    public org.w3c.dom.Node getContext() throws JspTagException {
	// in this implementation, it's safe just to call next() to get
	// the current node.  This method's just here for abstraction
	// and casting.
	return ((org.w3c.dom.Node) next());
    }


    //*********************************************************************
    // Private utility methods

    private void init() {
	select = null;
	nodes = null;
	nodesIndex = 0;
    }	
}

