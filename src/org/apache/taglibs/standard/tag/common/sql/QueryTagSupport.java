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

package org.apache.taglibs.standard.tag.common.sql;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.jstl.sql.*;
import javax.servlet.jsp.tagext.*;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.taglibs.standard.resources.Resources;


/**
 * <p>Tag handler for &lt;Query&gt; in JSTL.  
 * 
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public abstract class QueryTagSupport extends BodyTagSupport 
    implements TryCatchFinally, SQLExecutionTag {

    private static final String MAX_ROWS =
        "javax.servlet.jsp.jstl.sql.maxRows";

    private static final String DATASOURCE =
        "javax.servlet.jsp.jstl.sql.dataSource";

    private String var;
    private int scope;


    /*
     * The following properties take expression values, so the
     * setter methods are implemented by the expression type
     * specific subclasses.
     */
    protected Object rawDataSource;
    protected DataSource dataSource;
    protected String sql;
    protected int maxRows;
    protected int startRow;

    /*
     * Instance variables that are not for attributes
     */
    private Connection conn;
    private List parameters;
    private boolean isPartOfTransaction;

    //*********************************************************************
    // Constructor

    public QueryTagSupport() {
        super();
        init();
    }

    private void init() {
        scope = PageContext.PAGE_SCOPE;
        startRow = 0;

        try {
            maxRows = Integer.parseInt(
                pageContext.getServletContext().getInitParameter(MAX_ROWS));
        } catch (Exception ex) {
            maxRows = -1;
        }
    }


    //*********************************************************************
    // Accessor methods

    /**
     * Setter method for the name of the variable to hold the
     * result.
     *
     */
    public void setVar(String var) {
	this.var = var;
    }

    /**
     * Setter method for the scope of the variable to hold the
     * result.
     *
     */
    public void setScope(String scopeName) {
        if ("page".equals(scopeName)) {
            scope = PageContext.PAGE_SCOPE;
        }
        else if ("request".equals(scopeName)) {
            scope = PageContext.REQUEST_SCOPE;
        }
        else if ("session".equals(scopeName)) {
            scope = PageContext.SESSION_SCOPE;
        }
        else if ("application".equals(scopeName)) {
            scope = PageContext.APPLICATION_SCOPE;
        }
    }

    //*********************************************************************
    // Public utility methods

    /**
     * Called by nested parameter elements to add PreparedStatement
     * parameter values.
     */
    public void addSQLParameter(Object o) {
	if (parameters == null) {
	    parameters = new ArrayList();
	}
	parameters.add(o);
    }

    //*********************************************************************
    // Tag logic

    /**
     * Prepares for execution by setting the initial state, such as
     * getting the <code>Connection</code>
     */
    public int doStartTag() throws JspException {

        setDataSource();
	try {
	    conn = getConnection();
	}
	catch (SQLException e) {
	    throw new JspTagException(
                Resources.getMessage("ERROR_GET_CONNECTION", e.getMessage()));
	}
	return EVAL_BODY_BUFFERED;
    }

    /**
     * <p>Execute the SQL statement, set either through the <code>sql</code>
     * attribute or as the body, and save the result as a variable
     * named by the <code>var</code> attribute in the scope specified
     * by the <code>scope</code> attribute, as an object that implements
     * the Result interface.
     *
     * <p>The connection used to execute the statement comes either
     * from the <code>DataSource</code> specified by the
     * <code>dataSource</code> attribute, provided by a parent action
     * element, or is retrieved from a JSP scope  attribute
     * named <code>javax.servlet.jstl.sql.dataSource</code>.
     */
    public int doEndTag() throws JspException {
	/*
	 * Use the SQL statement specified by the sql attribute, if any,
	 * otherwise use the body as the statement.
	 */
	String sqlStatement = null;
	if (sql != null) {
	    sqlStatement = sql;
	}
	else if (bodyContent != null) {
	    sqlStatement = bodyContent.getString();
	}
	if (sqlStatement == null || sqlStatement.trim().length() == 0) {
	    throw new JspTagException(
                Resources.getMessage("SQL_NO_STATEMENT"));
	}
        /*
         * We shouldn't have a negative startRow
         */
        if (startRow < 0) {
            startRow = 0;
        }

	Result result = null;
	try {
	    PreparedStatement ps = conn.prepareStatement(sqlStatement);
            if (maxRows > 0 ) {
	        ps.setMaxRows(startRow + maxRows);
            }
	    setParameters(ps, parameters);
	    ResultSet rs = ps.executeQuery();
	    result = new ResultImpl(rs, startRow, maxRows);
	}
	catch (SQLException e) {
	    throw new JspTagException(
                Resources.getMessage("SQL_PROCESS_ERROR", e.getMessage()));
	}
	pageContext.setAttribute(var, result, scope);
	return EVAL_PAGE;
    }

    /**
     * Just rethrows the Throwable.
     */
    public void doCatch(Throwable t) throws Throwable {
	throw t;
    }

    /**
     * Close the <code>Connection</code>, unless this action is used
     * as part of a transaction.
     */
    public void doFinally() {
	if (conn != null && !isPartOfTransaction) {
	    try {
		conn.close();
	    }
	    catch (SQLException e) {} // Not much we can do
	}
	/*
	 * Reset the per-invokation state.
	 */
	parameters = null;
	isPartOfTransaction = false;
	conn = null;
        dataSource = null;
        rawDataSource = null;
    }

    //*********************************************************************
    // Private utility methods

    private void setDataSource() throws JspException {

        if (rawDataSource != null) {
            // If the 'dataSource' attribute's value resolves to a String
            // after rtexpr/EL evaluation, use the string as JNDI path to 
            // a DataSource
            if (rawDataSource instanceof String) {
                try {
                    Context ctx = new InitialContext();
                    dataSource = (DataSource) ctx.lookup((String)rawDataSource);
                } catch (NamingException ex) {
                    throw new JspTagException(ex.toString());
                }
            }
            else dataSource = (DataSource) rawDataSource;
        }
        else {
            dataSource = (DataSource) pageContext.findAttribute(DATASOURCE);
        }
    }


    private Connection getConnection() throws JspException, SQLException {
	// Fix: Add all other mechanisms
	Connection conn = null;
	TransactionTagSupport parent = (TransactionTagSupport) 
	    findAncestorWithClass(this, TransactionTagSupport.class);
	if (parent != null) {
            if (rawDataSource != null) {
                throw new JspTagException(
                    Resources.getMessage("ERROR_NESTED_DATASOURCE"));
            }
	    conn = parent.getSharedConnection();
            isPartOfTransaction = true;
	}
	else {
	    conn = dataSource.getConnection();
	}
	return conn;
    }

    private void setParameters(PreparedStatement ps, List parameters) 
        throws SQLException {
	if (parameters != null) {
	    for (int i = 0; i < parameters.size(); i++) {
		// The first parameter has index 1
		ps.setObject(i + 1, parameters.get(i));
	    }
	}
    }
}
