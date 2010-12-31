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
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of &lt;x:forEach&gt; tag using low-level Xalan API.
 *
 * @author Shawn Bayern
 * @see javax.servlet.jsp.jstl.core.LoopTagSupport
 */
public class ForEachTag extends LoopTagSupport {

    private XPath select;
    private XPathContext context;

    @Override
    public void release() {
        super.release();
        select = null;
        context = null;
    }

    @Override
    protected void prepare() throws JspTagException {
        context = XalanUtil.getContext(this, pageContext);
        try {
            XObject nodes = select.execute(context, context.getCurrentNode(), null);

            // create an iterator over the returned nodes and push into the context
            DTMIterator iterator = nodes.iter();
            context.pushContextNodeList(iterator);
        } catch (TransformerException e) {
            throw new JspTagException(e);
        }
    }

    @Override
    protected boolean hasNext() throws JspTagException {
        DTMIterator iterator = context.getContextNodeList();
        return iterator.getCurrentPos() < iterator.getLength();
    }

    @Override
    protected Object next() throws JspTagException {
        DTMIterator iterator = context.getContextNodeList();
        int next = iterator.nextNode();
        context.pushCurrentNode(next);
        return iterator.getDTM(next).getNode(next);
    }

    @Override
    public int doAfterBody() throws JspException {
        // pop the context node after executing the body
        context.popCurrentNode();
        return super.doAfterBody();
    }

    @Override
    public void doFinally() {
        // context might be null as prepare is not called if end < begin
        if (context != null) {
            // pop the list of nodes being iterated
            context.popContextNodeList();
            context = null;
        }
        super.doFinally();
    }

    public void setSelect(String select) {
        try {
            this.select = new XPath(select, null, null, XPath.SELECT);
        } catch (TransformerException e) {
            throw new AssertionError();
        }
    }

    public void setBegin(int begin) throws JspTagException {
        this.beginSpecified = true;
        this.begin = begin;
        validateBegin();
    }

    public void setEnd(int end) throws JspTagException {
        this.endSpecified = true;
        this.end = end;
        validateEnd();
    }

    public void setStep(int step) throws JspTagException {
        this.stepSpecified = true;
        this.step = step;
        validateStep();
    }

    /**
     * Return the current XPath context to support expression evaluation in nested tags.
     *
     * @return the current XPath context
     */
    XPathContext getContext() {
        return context;
    }
}

