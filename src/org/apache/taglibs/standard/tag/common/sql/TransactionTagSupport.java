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
import javax.servlet.jsp.jstl.core.Config;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.taglibs.standard.resources.Resources;


/**
 * <p>Tag handler for &lt;Transaction&gt; in JSTL.  
 * 
 * @author Hans Bergsten
 */

public abstract class TransactionTagSupport extends TagSupport 
    implements TryCatchFinally {

    private static final int DEFAULT_ISOLATION = -1;

    private int isolation = DEFAULT_ISOLATION;

    /*
     * The following properties take expression values, so the
     * setter methods are implemented by the expression type
     * specific subclasses.
     */
    protected Object rawDataSource;
    protected DataSource dataSource;

    /*
     * Instance variables that are not for attributes
     */
    private Connection conn;
    private int origIsolation;
    private DataSourceUtil dsUtil;

    //*********************************************************************
    // Public utility methods

    /**
     * Setter method for the transaction isolation level.
     */
    public void setIsolation(String isolation) 
	throws JspTagException {

	if ("TRANSACTION_READ_COMMITTED".equals(isolation)) {
	    this.isolation = Connection.TRANSACTION_READ_COMMITTED;
	}
	if ("TRANSACTION_READ_UNCOMMITTED".equals(isolation)) {
	    this.isolation = Connection.TRANSACTION_READ_UNCOMMITTED;
	}
	if ("TRANSACTION_REPEATABLE_READ".equals(isolation)) {
	    this.isolation = Connection.TRANSACTION_REPEATABLE_READ;
	}
	if ("TRANSACTION_SERIALIZABLE".equals(isolation)) {
	    this.isolation = Connection.TRANSACTION_SERIALIZABLE;
	}
	else {
	    throw new JspTagException(
                Resources.getMessage("TRANSACTION_INVALID_ISOLATION"));
	}
    }

    /**
     * Called by nested parameter elements to get a reference to
     * the Connection.
     */
    public Connection getSharedConnection() {
	return conn;
    }

    //*********************************************************************
    // Tag logic

    /**
     * Prepares for execution by setting the initial state, such as
     * getting the <code>Connection</code> and preparing it for
     * the transaction.
     */
    public int doStartTag() throws JspException {

        dsUtil = new DataSourceUtil();
        dsUtil.setDataSource(rawDataSource, pageContext);

        dataSource = dsUtil.getDataSource();

	try {
	    conn = getConnection();
	    int origIsolation = conn.getTransactionIsolation();
	    if (origIsolation == Connection.TRANSACTION_NONE) {
		throw new JspTagException(
                    Resources.getMessage("TRANSACTION_NO_SUPPORT"));
	    }
	    if (isolation != DEFAULT_ISOLATION &&
		origIsolation != isolation) {
		conn.setTransactionIsolation(isolation);
	    }
	    conn.setAutoCommit(false);
	}
	catch (SQLException e) {
	    throw new JspTagException(
                Resources.getMessage("ERROR_GET_CONNECTION", e.getMessage()));
	} 

	return EVAL_BODY_INCLUDE;
    }

    /**
     * Commits the transaction.
     */
    public int doEndTag() throws JspException {
	try {
	    conn.commit();
	}
	catch (SQLException e) {
	    throw new JspTagException(
                Resources.getMessage("TRANSACTION_COMMIT_ERROR", e.getMessage()));
	}
	return EVAL_PAGE;
    }

    /**
     * Rollbacks the transaction and rethrows the Throwable.
     */
    public void doCatch(Throwable t) throws Throwable {
	if (conn != null) {
	    try {
		conn.rollback();
	    }
	    catch (SQLException e) {} // Ignore to not hide orignal exception
	}
	throw t;
    }

    /**
     * Restores the <code>Connection</code> to its initial state and closes 
     * it.
     */
    public void doFinally() {
	if (conn != null) {
	    try {
		if (isolation != DEFAULT_ISOLATION &&
		    origIsolation != isolation) {
		    conn.setTransactionIsolation(origIsolation);
		}
		conn.setAutoCommit(true);
		conn.close();
	    }
	    catch (SQLException e) {} // Not much we can do
	}
	conn = null;
    }

    //*********************************************************************
    // Private utility methods

    private Connection getConnection() throws SQLException {
	// Fix: Add all other mechanisms
	return dataSource.getConnection();
    }
}
