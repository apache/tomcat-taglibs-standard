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
package org.apache.taglibs.standard.tag.el.sql;

import javax.sql.*;
import javax.servlet.jsp.*;
import org.apache.taglibs.standard.lang.support.*;
import org.apache.taglibs.standard.tag.common.sql.QueryTagSupport;
import org.apache.taglibs.standard.resources.Resources;

/**
 * Subclass for the JSTL library with EL support.
 *
 * @author Hans Bergsten
 * @author Justyna Horwat
 */
public class QueryTag extends QueryTagSupport {

    private String dataSourceEL;
    private String sqlEL;
    private String startRowEL;
    private String maxRowsEL;

    //*********************************************************************
    // Constructor

    /**
     * Constructs a new QueryTag.  As with TagSupport, subclasses
     * should not provide other constructors and are expected to call
     * the superclass constructor
     */
    public QueryTag() {
        super();
    }

    //*********************************************************************
    // Accessor methods

    public void setDataSource(String dataSourceEL) {
	this.dataSourceEL = dataSourceEL;
	this.dataSourceSpecified = true;
    }

    /**
     * The index of the first row returned can be
     * specified using startRow.
     */
    public void setStartRow(String startRowEL) {
	this.startRowEL = startRowEL;
    }

    /**
     * Query result can be limited by specifying
     * the maximum number of rows returned.
     */
    public void setMaxRows(String maxRowsEL) {
	this.maxRowsEL = maxRowsEL;
	this.maxRowsSpecified = true;
    }

    /**
     * Setter method for the SQL statement to use for the
     * query. The statement may contain parameter markers
     * (question marks, ?). If so, the parameter values must
     * be set using nested value elements.
     */
    public void setSql(String sqlEL) {
	this.sqlEL = sqlEL;
    }

    public int doStartTag() throws JspException {
        evaluateExpressions();
	return super.doStartTag();
    }

    //*********************************************************************
    // Private utility methods

    // Evaluates expressions as necessary
    private void evaluateExpressions() throws JspException {
        Object tempInt = null;

        if (dataSourceEL != null) {
            rawDataSource = (Object) ExpressionEvaluatorManager.evaluate(
                "dataSource", dataSourceEL, Object.class, this, pageContext);
        }

        if (sqlEL != null) {
            sql = (String) ExpressionEvaluatorManager.evaluate("sql", sqlEL,
                String.class, this, pageContext);
        }

        try {
            if (startRowEL != null) {
                tempInt = (Integer) ExpressionEvaluatorManager.evaluate(
                    "startRow", startRowEL, Integer.class, this, pageContext);
                if (tempInt != null)
                    startRow = ((Integer) tempInt).intValue();
            }

            if (maxRowsEL != null) {
                tempInt = (Integer) ExpressionEvaluatorManager.evaluate(
                    "maxRows", maxRowsEL, Integer.class, this, pageContext);
                if (tempInt != null)
                    maxRows = ((Integer) tempInt).intValue();
            }
        } catch (Exception ex) {
            throw new JspException(Resources.getMessage("PARAM_BAD_VALUE"),
				   ex);
        }
    }
}
