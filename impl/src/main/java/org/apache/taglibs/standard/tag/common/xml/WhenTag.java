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

import javax.servlet.jsp.JspTagException;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.tag.common.core.WhenTagSupport;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;

/**
 * <p>Tag handler for &lt;if&gt; in JSTL's XML library.</p>
 *
 * @author Shawn Bayern
 */

public class WhenTag extends WhenTagSupport {

    private XPath select;

    @Override
    public void release() {
        super.release();
        select = null;
    }

    @Override
    protected boolean condition() throws JspTagException {
        XPathContext context = XalanUtil.getContext(this, pageContext);
        try {
            return select.bool(context, context.getCurrentNode(), null);
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
