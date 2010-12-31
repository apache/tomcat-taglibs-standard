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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * <p>Tag handler for &lt;set&gt; in JSTL's XML library.</p>
 *
 * @author Shawn Bayern
 */
public class SetTag extends TagSupport {

    private XPath select;
    private String var;
    private int scope = PageContext.PAGE_SCOPE;

    //*********************************************************************
    // Construction and initialization

    @Override
    public void release() {
        super.release();
        select = null;
        var = null;
    }

    //*********************************************************************
    // Tag logic

    // applies XPath expression from 'select' and stores the result in 'var'

    @Override
    public int doStartTag() throws JspException {
        try {
            XPathContext context = XalanUtil.getContext(this, pageContext);
            XObject result = select.execute(context, context.getCurrentNode(), null);
            pageContext.setAttribute(var, XalanUtil.coerceToJava(result), scope);
            return SKIP_BODY;
        } catch (TransformerException e) {
            throw new JspTagException(e);
        }
    }

    //*********************************************************************
    // Attribute accessors

    public void setSelect(String select) {
        try {
            this.select = new XPath(select, null, null, XPath.SELECT);
        } catch (TransformerException e) {
            throw new AssertionError();
        }
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }
}
