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
import javax.servlet.jsp.jstl.sql.ColumnMetaData;

/**
 * <p>This class encapsulates all meta data for a column in a database query 
 * result. Instances of this class are used by the <code>ColumnImpl</code>
 * instances representing specific column values.
 * 
 * <p>All methods may throw an SQLException. In this case, the exception 
 * instance may be the same instance as thrown by the corresponding
 * <code>java.sql.ResultSetMetaData</code> method when the Column instance
 * was created.</p>
 *
 * <p>Note! Currently this class contains methods corresponding to all
 * <code>ResultSetMetaData</code> methods. We may want to remove some of them
 * that don't make much sense in the context where this class is used.</p>
 *
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public class ColumnMetaDataImpl implements ColumnMetaData {

    private boolean isAutoIncrement;
    private SQLException isAutoIncrementException;
    private boolean isCaseSensitive;
    private SQLException isCaseSensitiveException;
    private boolean isSearchable;
    private SQLException isSearchableException;
    private boolean isCurrency;
    private SQLException isCurrencyException;
    private int isNullable;
    private SQLException isNullableException;
    private boolean isSigned;
    private SQLException isSignedException;
    private int displaySize;
    private SQLException displaySizeException;
    private String label;
    private SQLException labelException;
    private String name;
    private SQLException nameException;
    private String schemaName;
    private SQLException schemaNameException;
    private int precision;
    private SQLException precisionException;
    private int scale;
    private SQLException scaleException;
    private String tableName;
    private SQLException tableNameException;
    private String catalogName;
    private SQLException catalogNameException;
    private int type;
    private SQLException typeException;
    private String typeName;
    private SQLException typeNameException;
    private boolean isReadOnly;
    private SQLException isReadOnlyException;
    private boolean isWritable;
    private SQLException isWritableException;
    private boolean isDefinitelyWritable;
    private SQLException isDefinitelyWritableException;
    private String className;
    private SQLException classNameException;


    /**
     * Indicates whether the column is automatically numbered, 
     * thus read-only.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isAutoIncrement() throws SQLException {
	if (isAutoIncrementException != null) {
	    throw isAutoIncrementException;
	}
	return isAutoIncrement;
    }

    public void setAutoIncrement(boolean value) {
	isAutoIncrement = value;
    }

    public void setAutoIncrementException(SQLException e) {
	isAutoIncrementException = e;
    }

    /**
     * Indicates whether a column's case matters.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isCaseSensitive() throws SQLException {
	if (isCaseSensitiveException != null) {
	    throw isCaseSensitiveException;
	}
	return isCaseSensitive;
    }

    void setCaseSensitive(boolean value) {
	isCaseSensitive = value;
    }

    void setCaseSensitiveException(SQLException e) {
	isCaseSensitiveException = e;
    }

    /**
     * Indicates whether the column can be used in a where clause.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isSearchable() throws SQLException {
	if (isSearchableException != null) {
	    throw isSearchableException;
	}
	return isSearchable;
    }

    void setSearchable(boolean value) {
	isSearchable = value;
    }

    void setSearchableException(SQLException e) {
	isSearchableException = e;
    }

    /**
     * Indicates whether the column is a cash value.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isCurrency() throws SQLException {
	if (isCurrencyException != null) {
	    throw isCurrencyException;
	}
	return isCurrency;
    }

    void setCurrency(boolean value) {
	isCurrency = value;
    }

    void setCurrencyException(SQLException e) {
	isCurrencyException = e;
    }

    /**
     * Indicates the nullability of values in the column.
     *
     * @return the nullability status of the given column
     *
     * @throws SQLException if a database access error occurs
     */
    public int isNullable() throws SQLException {
	if (isNullableException != null) {
	    throw isNullableException;
	}
	return isNullable;
    }

    void setNullable(int value) {
	isNullable = value;
    }

    void setNullableException(SQLException e) {
	isNullableException = e;
    }

    /**
     * Indicates whether values in the column are signed numbers.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isSigned() throws SQLException {
	if (isSignedException != null) {
	    throw isSignedException;
	}
	return isSigned;
    }

    void setSigned(boolean value) {
	isSigned = value;
    }

    void setSignedException(SQLException e) {
	isSignedException = e;
    }

    /**
     * Indicates the column's normal maximum width in characters.
     *
     * @return the normal maximum number of characters allowed as
     * the width of the designated column
     *
     * @throws SQLException if a database access error occurs
     */
    public int getDisplaySize() throws SQLException {
	if (displaySizeException != null) {
	    throw displaySizeException;
	}
	return displaySize;
    }

    void setDisplaySize(int value) {
	displaySize = value;
    }

    void setDisplaySizeException(SQLException e) {
	displaySizeException = e;
    }

    /**
     * Gets the column's suggested title for use in printouts and displays.
     *
     * @return the column's suggested title
     *
     * @throws SQLException if a database access error occurs
     */
    public String getLabel() throws SQLException {
	if (labelException != null) {
	    throw labelException;
	}
	return label;
    }

    void setLabel(String value) {
	label = value;
    }

    void setLabelException(SQLException e) {
	labelException = e;
    }

    /**
     * Get the designated column's name.
     *
     * @return the column name
     *
     * @throws SQLException if a database access error occurs
     */
    public String getName() throws SQLException {
	if (nameException != null) {
	    throw nameException;
	}
	return name;
    }

    void setName(String value) {
	name = value;
    }

    void setNameException(SQLException e) {
	nameException = e;
    }

    /**
     * Get the column's table's schema.
     *
     * @return the schema name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getSchemaName() throws SQLException {
	if (schemaNameException != null) {
	    throw schemaNameException;
	}
	return schemaName;
    }

    void setSchemaName(String value) {
	schemaName = value;
    }

    void setSchemaNameException(SQLException e) {
	schemaNameException = e;
    }

    /**
     * Get the column's number of decimal digits.
     *
     * @return the column's number of decimal digits
     *
     * @throws SQLException if a database access error occurs
     */
    public int getPrecision() throws SQLException {
	if (precisionException != null) {
	    throw precisionException;
	}
	return precision;
    }

    void setPrecision(int value) {
	precision = value;
    }

    void setPrecisionException(SQLException e) {
	precisionException = e;
    }

    /**
     * Gets the column's number of digits to the right of the decimal point.
     *
     * @return the column's number of digits to the right of the decimal
     * point
     *
     * @throws SQLException if a database access error occurs
     */
    public int getScale() throws SQLException {
	if (scaleException != null) {
	    throw scaleException;
	}
	return scale;
    }

    void setScale(int value) {
	scale = value;
    }

    void setScaleException(SQLException e) {
	scaleException = e;
    }

    /**
     * Gets the designated column's table name.
     *
     * @return the table name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getTableName() throws SQLException {
	if (tableNameException != null) {
	    throw tableNameException;
	}
	return tableName;
    }

    void setTableName(String value) {
	tableName = value;
    }

    void setTableNameException(SQLException e) {
	tableNameException = e;
    }

    /**
     * Gets the column's table's catalog name.
     *
     * @return the catalog name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getCatalogName() throws SQLException {
	if (catalogNameException != null) {
	    throw catalogNameException;
	}
	return catalogName;
    }

    void setCatalogName(String value) {
	catalogName = value;
    }

    void setCatalogNameException(SQLException e) {
	catalogNameException = e;
    }

    /**
     * Retrieves the column's SQL type.
     *
     * <p>Fix: maybe this method should return a String instead, with
     * the name of the type based on the java.sql.Types statics names.</p>
     *
     * @return the column's SQL type
     *
     * @throws SQLException if a database access error occurs
     */
    public int getType() throws SQLException {
	if (typeException != null) {
	    throw typeException;
	}
	return type;
    }

    void setType(int value) {
	type = value;
    }

    void setTypeException(SQLException e) {
	typeException = e;
    }

    /**
     * Retrieves the column's database-specific type name.
     *
     * @return the column's database-specific type name
     *
     * @throws SQLException if a database access error occurs
     */
    public String getTypeName() throws SQLException {
	if (typeNameException != null) {
	    throw typeNameException;
	}
	return typeName;
    }

    void setTypeName(String value) {
	typeName = value;
    }

    void setTypeNameException(SQLException e) {
	typeNameException = e;
    }

    /**
     * Indicates whether the column is definitely not writable.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isReadOnly() throws SQLException {
	if (isReadOnlyException != null) {
	    throw isReadOnlyException;
	}
	return isReadOnly;
    }

    void setReadOnly(boolean value) {
	isReadOnly = value;
    }

    void setReadOnlyException(SQLException e) {
	isReadOnlyException = e;
    }

    /**
     * Indicates whether it is possible for a write on the column to succeed.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isWritable() throws SQLException {
	if (isWritableException != null) {
	    throw isWritableException;
	}
	return isWritable;
    }

    void setWritable(boolean value) {
	isWritable = value;
    }

    void setWritableException(SQLException e) {
	isWritableException = e;
    }

    /**
     * Indicates whether a write on the column will definitely succeed.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isDefinitelyWritable() throws SQLException {
	if (isDefinitelyWritableException != null) {
	    throw isDefinitelyWritableException;
	}
	return isDefinitelyWritable;
    }

    void setDefinitelyWritable(boolean value) {
	isDefinitelyWritable = value;
    }

    void setDefinitelyWritableException(SQLException e) {
	isDefinitelyWritableException = e;
    }

    /**
     * Returns the fully-qualified name of the Java class whose instances 
     * are manufactured if the method <code>ResultSet.getObject</code> is 
     * called to retrieve a value from the column. 
     * <code>ResultSet.getObject</code> may return a subclass of the class 
     * returned by this method.
     *
     * @return the fully-qualified name of the class in the Java
     * programming language that would be used by the method
     * <code>ResultSet.getObject</code> to retrieve the value in the
     * specified column. This is the class name used for custom mapping.
     *
     * @throws SQLException if a database access error occurs
     */
    public String getClassName() throws SQLException {
	if (classNameException != null) {
	    throw classNameException;
	}
	return className;
    }

    void setClassName(String value) {
	className = value;
    }

    void setClassNameException(SQLException e) {
	classNameException = e;
    }
}
