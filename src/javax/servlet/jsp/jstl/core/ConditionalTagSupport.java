/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package javax.servlet.jsp.jstl.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

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
     * Includes its body if <tt>condition()</tt> evaluates to true.
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
	if (scope.equalsIgnoreCase("page"))
	    this.scope = PageContext.PAGE_SCOPE;
	else if (scope.equalsIgnoreCase("request"))
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
