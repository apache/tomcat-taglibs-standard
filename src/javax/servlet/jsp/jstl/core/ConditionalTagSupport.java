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

package javax.servlet.jsp.jstl.core;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * <p>ConditionalTagSupport is an abstract class that facilitates
 * implementation of conditional tags -- specifically, tags in the style
 * of &lt;if&gt;.</p>
 *
 * <p>In particular, this base class provides support for:</p>
 * 
 * <ul>
 *  <li> conditional execution based on an overridable <tt>condition()</tt>
 *       method </li>
 *  <li> scoped attributes storing the result (as a Boolean) of the
 *       logic executed by a subclass (optionally by attribute) </li>
 * </ul>
 *
 * <p>Since this method implements the behavior anticipated to be
 * recommended by the standard (with respect to tags that support
 * boolean logic), it is expected that it will be part of the JSTL API.
 * </p>
 * 
 * @author Shawn Bayern
 */

public abstract class ConditionalTagSupport
    extends TagSupport
{
    //*********************************************************************
    // Abstract methods

    /**
     * <p>Returns a <tt>boolean</tt> representing the condition that
     * a particular subclass uses to drive its conditional logic.
     *
     * @return a boolean representing the result of arbitrary logic
     *         that will be used to drive a tag's behavior
     */
    protected abstract boolean condition() throws JspTagException;


    //*********************************************************************
    // Constructor

    /**
     * Base constructor to initialize local state.  As with TagSupport,
     * subclasses should not provide other constructors and are expected
     * to call the superclass constructor.
     */
    public ConditionalTagSupport() {
        super();
        init();
    }


    //*********************************************************************
    // Lifecycle management and implementation of conditional behavior

    /**
     * Includes its body if <tt>condition()</tt> evalutes to true.
     */
    public int doStartTag() throws JspException {

        // execute our condition() method once per invocation
        result = condition();

        // expose variables if appropriate
        exposeVariables();

        // handle conditional behavior
        if (result)
            return EVAL_BODY_INCLUDE;
        else
            return SKIP_BODY;
    }

    /**
     * Releases any resources this ConditionalTagSupport may have (or inherit).
     */
    public void release() {
        super.release();
        init();
    }

    //*********************************************************************
    // Private state

    private boolean result;             // the saved result of condition()
    private String var;			// scoped attribute name
    private int scope;			// scoped attribute scope


    //*********************************************************************
    // Accessors

    /**
     * Sets the 'var' attribute.
     *
     * @param var Name of the exported scoped variable storing the result of
     * <tt>condition()</tt>.
     */
    public void setVar(String var) {
	this.var = var;
    }

    /**
     * Sets the 'scope' attribute.
     *
     * @param scope Scope of the 'var' attribute
     */
    public void setScope(String scope) {
	if (scope.equalsIgnoreCase("request"))
	    this.scope = PageContext.REQUEST_SCOPE;
	else if (scope.equalsIgnoreCase("session"))
	    this.scope = PageContext.SESSION_SCOPE;
	else if (scope.equalsIgnoreCase("application"))
	    this.scope = PageContext.APPLICATION_SCOPE;
	// TODO: Add error handling?  Needs direction from spec.
    }


    //*********************************************************************
    // Utility methods

    // expose attributes if we have a non-null 'var'
    private void exposeVariables() {
        if (var != null)
            pageContext.setAttribute(var, new Boolean(result), scope);
    }

    // initializes internal state
    private void init() {
        result = false;                 // not really necessary
	var = null;
	scope = PageContext.PAGE_SCOPE;
    }
}
