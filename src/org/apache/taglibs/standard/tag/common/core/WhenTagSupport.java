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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>WhenTagSupport is an abstract class that facilitates
 * implementation of &lt;when&gt;-style tags in both the rtexprvalue
 * and expression-evaluating libraries.  It also supports
 * &lt;otherwise&gt;.</p>
 *
 * <p>In particular, this base class does the following:</p>
 * 
 * <ul>
 *  <li> overrides ComparisonTagSupport.doStartTag() to implement the
 *       appropriate semantics of subtags of &lt;choose&gt; </li>
 * </ul>
 *
 * @author Shawn Bayern
 */
public abstract class WhenTagSupport extends ConditionalTagSupport
{
    //*********************************************************************
    // Implementation of exclusive-conditional behavior

    /*
     * Includes its body if condition() evalutes to true AND its parent
     * ChooseTag wants it to do so.  The condition will not even be
     * evaluated if ChooseTag instructs us not to run.
     */
    public int doStartTag() throws JspException {

        Tag parent;

        // make sure we're contained properly
        if (!((parent = getParent()) instanceof ChooseTag))
            throw new JspTagException(
		Resources.getMessage("WHEN_OUTSIDE_CHOOSE"));

        // make sure our parent wants us to continue
        if (!((ChooseTag) parent).gainPermission())
            return SKIP_BODY;                   // we've been reeled in

        // handle conditional behavior
        if (condition()) {
            ((ChooseTag) parent).subtagSucceeded();
            return EVAL_BODY_INCLUDE;
        } else
            return SKIP_BODY;
    }
}
