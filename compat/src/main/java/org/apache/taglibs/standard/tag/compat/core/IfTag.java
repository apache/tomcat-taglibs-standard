package org.apache.taglibs.standard.tag.compat.core;

import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import org.apache.taglibs.standard.util.ExpressionUtil;

/**
 * JSTL 1.0 compatible implementation of &lt;if&gt; that uses JSP EL support.
 */
public class IfTag extends ConditionalTagSupport {
    private ValueExpression testExpression;

    public IfTag() {
    }

    @Override
    public void release() {
        testExpression = null;
        super.release();
    }

    @Override
    protected boolean condition() throws JspTagException {
        return (Boolean) testExpression.getValue(pageContext.getELContext());
    }

    public void setTest(String test) {
        testExpression = ExpressionUtil.createValueExpression(pageContext, test, Boolean.TYPE);
    }
}
