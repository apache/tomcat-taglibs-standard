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

package javax.servlet.jsp.jstl.sql;

import java.sql.*;

/**
 * <p>A class implementing this interface encapsulates the meta data for 
 * a column in a database query result. 
 * 
 * <p>Most methods may throw an SQLException. In this case, the exception 
 * instance may be the same instance as thrown by the corresponding
 * <code>java.sql.ResultSetMetaData</code> method when the Column instance
 * was created.</p>
 *
 * <p>Note! Currently this class contains methods corresponding to all
 * <code>ResultSetMetaData</code> methods. We may want to remove some of them
 * that don't make much sense in the context where this class is used.</p>
 *
 */

public interface ColumnMetaData {

    /**
     * Get the designated column's name.
     */
    public String getName() throws SQLException;

    /**
     * Retrieves the column's SQL type.
     *
     *<p>Fix: maybe this method should return a String instead, with
     * the name of the type based on the java.sql.Types statics names.
     */
    public int getType() throws SQLException;

    /**
     * Retrieves the column's database-specific type name.
     */
    public String getTypeName() throws SQLException;

    /**
     * Indicates whether the column is automatically numbered, 
     * thus read-only.
     */
    public boolean isAutoIncrement() throws SQLException;

    /**
     * Indicates whether a column's case matters.
     */
    public boolean isCaseSensitive() throws SQLException;

    /**
     * Indicates whether the column can be used in a where clause.
     */
    public boolean isSearchable() throws SQLException;

    /**
     * Indicates whether the column is a cash value.
     */
    public boolean isCurrency() throws SQLException;

    /**
     * Indicates the nullability of values in the column.
     */
    public int isNullable() throws SQLException;

    /**
     * Indicates whether values in the column are signed numbers.
     */
    public boolean isSigned() throws SQLException;

    /**
     * Indicates the column's normal maximum width in characters.
     */
    public int getDisplaySize() throws SQLException;

    /**
     * Gets the column's suggested title for use in printouts and displays.
     */
    public String getLabel() throws SQLException;

    /**
     * Get the column's table's schema.
     */
    public String getSchemaName() throws SQLException;

    /**
     * Get the column's number of decimal digits.
     */
    public int getPrecision() throws SQLException;

    /**
     * Gets the column's number of digits to right of the decimal point.
     */
    public int getScale() throws SQLException;

    /**
     * Gets the designated column's table name.
     */
    public String getTableName() throws SQLException;

    /**
     * Gets the column's table's catalog name.
     */
    public String getCatalogName() throws SQLException;

    /**
     * Indicates whether the column is definitely not writable.
     */
    public boolean isReadOnly() throws SQLException;

    /**
     * Indicates whether it is possible for a write on the column to succeed.
     */
    public boolean isWritable() throws SQLException;

    /**
     * Indicates whether a write on the column will definitely succeed.
     */
    public boolean isDefinitelyWritable() throws SQLException;

    /**
     * Returns the fully-qualified name of the Java class whose instances 
     * are manufactured if the method <code>ResultSet.getObject</code> is 
     * called to retrieve a value from the column. 
     * <code>ResultSet.getObject</code> may return a subclass of the class 
     * returned by this method.
     */
    public String getClassName() throws SQLException;

}
