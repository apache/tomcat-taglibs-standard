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

package org.apache.taglibs.standard.tag.common.fmt;

import java.io.IOException;
import java.util.TimeZone;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.Config;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;timeZone&gt;, the time zone tag in
 * JSTL 1.0.
 *
 * @author Jan Luehe
 */

public abstract class TimeZoneSupport extends BodyTagSupport {


    //*********************************************************************
    // Protected state

    protected Object value;                      // 'value' attribute
  

    //*********************************************************************
    // Private state

    private TimeZone timeZone;


    //*********************************************************************
    // Constructor and initialization

    public TimeZoneSupport() {
	super();
	init();
    }

    private void init() {
	value = null;
    }


    //*********************************************************************
    // Collaboration with subtags

    public TimeZone getTimeZone() {
	return timeZone;
    }


    //*********************************************************************
    // Tag logic

    public int doStartTag() throws JspException {

	if (value == null) {
	    timeZone = TimeZone.getTimeZone("GMT");
	} else if (value instanceof String) {
	    if (((String) value).trim().equals("")) {
		timeZone = TimeZone.getTimeZone("GMT");
	    } else {
		timeZone = TimeZone.getTimeZone((String) value);
	    }
	} else {
	    timeZone = (TimeZone) value;
	}

	return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
	try {
	    pageContext.getOut().print(getBodyContent().getString());
	} catch (IOException ioe) {
	    throw new JspTagException(ioe.getMessage());
	}

	return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Package-scoped utility methods

    /*
     * Returns the time zone.
     *
     * <p> If the given action is nested inside a &lt;timeZone&gt; tag,
     * the time zone is taken from the enclosing &lt;timeZone&gt; tag.
     *
     * <p> Otherwise, the default time zone given by the
     * <tt>javax.servlet.jsp.jstl.fmt.timeZone</tt> scoped attribute is used,
     * which is searched in the page, request, session (if valid),
     * and application scope(s) (in this order).
     * 
     * <p> If still not found, the JSP container's time zone is used.
     *
     * @param pageContext the page containing the action that requires the
     * time zone
     * @param fromTag the action that requires the time zone
     *
     * @return the time zone
     */
    static TimeZone getTimeZone(PageContext pc, Tag fromTag) {
	TimeZone tz = null;

	Tag t = findAncestorWithClass(fromTag, TimeZoneSupport.class);
	if (t != null) {
	    // use time zone from parent <timeZone> tag
	    TimeZoneSupport parent = (TimeZoneSupport) t;
	    tz = parent.getTimeZone();
	} else {
	    // get time zone from config variable or context init param
	    Object obj = Config.find(pc, Config.FMT_TIME_ZONE);
	    if (obj != null) {
		if (obj instanceof TimeZone) {
		    tz = (TimeZone) obj;
		} else {
		    tz = TimeZone.getTimeZone((String) obj);
		}
	    }
	}

	return tz;
    }
}
