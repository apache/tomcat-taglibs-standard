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

package org.apache.taglibs.standard.tei;

import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.tag.common.fmt.FormatDateSupport;

/**
 * An implementation of TagExtraInfo that implements validation for
 * &lt;formatDate&gt; tag's attributes.
 *
 * @author Jan Luehe
 */
public class FormatDateTEI extends TagExtraInfo {

    private static final String TYPE_ATTRIBUTE = "type";          
    private static final String TIMESTYLE_ATTRIBUTE = "timeStyle";
    private static final String DATESTYLE_ATTRIBUTE = "dateStyle";

    /**
     * Validates the attributes of the &lt;formatDate&gt; tag.
     *
     * <p> The following validation rules are enforced:
     * 
     * <ul>
     * <li> The (case-insensitive) value of the <tt>type</tt> attribute must
     * be equal to &quot;date&quot;, &quot;time&quot;, or &quot;both&quot;.
     * <li> The (case-insensitive) value of the <tt>timeStyle</tt> and
     * <tt>dateStyle</tt> attributes must be equal to &quot;default&quot;,
     * &quot;short&quot;, &quot;medium&quot;, &quot;long&quot;, or
     * &quot;full&quot;.
     * </ul>
     */
    public boolean isValid(TagData data) {
	if (!isValidType(data)
	    || !isValidStyle(data.getAttributeString(TIMESTYLE_ATTRIBUTE))
	    || !isValidStyle(data.getAttributeString(DATESTYLE_ATTRIBUTE)))
	    return false;
        return true;
    }

    /*
     * Returns true if the 'type' attribute is valid.
     */
    static boolean isValidType(TagData data) {
	String type = data.getAttributeString(TYPE_ATTRIBUTE);
	if ((type != null)
	    && !type.equals(FormatDateSupport.DATE_STRING)
	    && !type.equals(FormatDateSupport.TIME_STRING)
	    && !type.equals(FormatDateSupport.DATETIME_STRING))
	    return false;
	return true;
    }

    /*
     * Returns true if the given 'timeStyle' or 'dateStyle' attribute is
     * present.
     */
    private boolean isValidStyle(String style) {
	if ((style != null)
	    && !style.equals(FormatDateSupport.DEFAULT_STYLE)
	    && !style.equals(FormatDateSupport.SHORT_STYLE)
	    && !style.equals(FormatDateSupport.MEDIUM_STYLE)
	    && !style.equals(FormatDateSupport.LONG_STYLE)
	    && !style.equals(FormatDateSupport.FULL_STYLE))
	    return false;
	return true;
    }
}
