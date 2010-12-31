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

package org.apache.taglibs.standard.tag.common.xml;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.util.EscapeXML;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;

/**
 * Tag handler for &lt;out&gt; in JSTL's XML library.
 * TODO: should we rename this to OutSupport to match the tag name?
 *
 * @author Shawn Bayern
 */
public abstract class ExprSupport extends TagSupport {

    private XPath select;
    protected boolean escapeXml = true;  // tag attribute

    @Override
    public void release() {
        super.release();
        select = null;
    }

    //*********************************************************************
    // Tag logic

    // applies XPath expression from 'select' and prints the result
    @Override
    public int doStartTag() throws JspException {
        try {
            XPathContext context = XalanUtil.getContext(this, pageContext);
            String result = select.execute(context, context.getCurrentNode(), null).str();
            EscapeXML.emit(result, escapeXml, pageContext.getOut());
            return SKIP_BODY;
        } catch (IOException ex) {
            throw new JspTagException(ex.toString(), ex);
        } catch (TransformerException e) {
            throw new JspTagException(e);
        }
    }

    public void setSelect(String select) {
        try {
            this.select = new XPath(select, null, null, XPath.SELECT);
        } catch (TransformerException e) {
            throw new AssertionError();
        }
    }
}
