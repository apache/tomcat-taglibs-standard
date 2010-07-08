/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.taglibs.standard.tag.common.core;

import org.apache.taglibs.standard.util.EscapeXML;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * <p>Support for handlers of the &lt;out&gt; tag, which simply evalutes and
 * prints the result of the expression it's passed.  If the result is
 * null, we print the value of the 'default' attribute's expression or
 * our body (which two are mutually exclusive, although this constraint
 * is enforced outside this handler, in our TagLibraryValidator).</p>
 *
 * @author Shawn Bayern
 */
public class OutSupport extends BodyTagSupport {

    /*
     * (One almost wishes XML and JSP could support "anonymous tags,"
     * given the amount of trouble we had naming this one!)  :-)  - sb
     */

    //*********************************************************************
    // Internal state

    protected Object value;                     // tag attribute
    protected String def;			// tag attribute
    protected boolean escapeXml;		// tag attribute
    private Object output;

    //*********************************************************************
    // Construction and initialization

    /**
     * Constructs a new handler.  As with TagSupport, subclasses should
     * not provide other constructors and are expected to call the
     * superclass constructor.
     */
    public OutSupport() {
        super();
        init();
    }

    // resets local state
    private void init() {
        value = def = null;
        output = null;
        escapeXml = true;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
        super.release();
        init();
    }


    //*********************************************************************
    // Tag logic

    @Override
    public int doStartTag() throws JspException {

        this.bodyContent = null;  // clean-up body (just in case container is pooling tag handlers)

        // output value if not null
        if (value != null) {
            output = value;
            return SKIP_BODY;
        }

        // output default if supplied
        if (def != null ) {
            output = def;
            return SKIP_BODY;
        }

        // output body as default
        output = ""; // need to reset as doAfterBody will not be called with an empty tag
        // TODO: to avoid buffering, can we wrap out in a filter that performs escaping and use EVAL_BODY_INCLUDE?
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doAfterBody() throws JspException {
        output = bodyContent.getString().trim();
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            EscapeXML.emit(output, escapeXml, pageContext.getOut());
        } catch (IOException e) {
            throw new JspTagException(e);
        }
        return EVAL_PAGE;
    }
}
