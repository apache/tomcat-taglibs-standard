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

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.jstl.sql.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.resources.Resources;


/**
 * <p>Tag handler for &lt;Driver&gt; in JSTL, used to create
 * a simple DataSource for prototyping.</p>
 * 
 * @author Hans Bergsten
 */
public class DriverTagSupport extends TagSupport {
    private static final String DRIVER_CLASS_NAME =
	"javax.servlet.jsp.jstl.sql.driver";
    private static final String JDBC_URL =
	"javax.servlet.jsp.jstl.sql.url";
    private static final String USER_NAME =
	"javax.servlet.jsp.jstl.sql.user";
    private static final String PASSWORD =
	"javax.servlet.jsp.jstl.sql.password";

    protected String driverClassName;
    protected String jdbcURL;
    protected String userName;

    private int scope = PageContext.PAGE_SCOPE;
    private String var;

    //*********************************************************************
    // Accessor methods

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

    public void setVar(String var) {
	this.var = var;
    }

    //*********************************************************************
    // Tag logic

    public int doStartTag() throws JspException {
	DataSourceWrapper ds = new DataSourceWrapper();
	try {
	ds.setDriverClassName(getDriverClassName());
	}
	catch (Exception e) {
	    throw new JspTagException(
                Resources.getMessage("DRIVER_INVALID_CLASS", e.getMessage()));
	}
	ds.setJdbcURL(getJdbcURL());
	ds.setUserName(getUserName());
	ds.setPassword(getPassword());
	pageContext.setAttribute(var, ds, scope);
	return SKIP_BODY;
    }


    //*********************************************************************
    // Private utility methods

    private String getDriverClassName() {
	if (driverClassName != null) {
	    return driverClassName;
	}
	ServletContext application = pageContext.getServletContext();
	return application.getInitParameter(DRIVER_CLASS_NAME);
    }

    private String getJdbcURL() {
	if (jdbcURL != null) {
	    return jdbcURL;
	}
	ServletContext application = pageContext.getServletContext();
	return application.getInitParameter(JDBC_URL);
    }

    private String getUserName() {
	if (userName != null) {
	    return userName;
	}
	ServletContext application = pageContext.getServletContext();
	return application.getInitParameter(USER_NAME);
    }

    private String getPassword() {
	ServletContext application = pageContext.getServletContext();
	return application.getInitParameter(PASSWORD);
    }
}
