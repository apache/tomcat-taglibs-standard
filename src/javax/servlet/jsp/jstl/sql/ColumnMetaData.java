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
 * <p>This interface provides access to the meta data associated
 * with a column</p>
 *
 * @author Justyna Horwat
 */
public interface ColumnMetaData {

    /**
     * Get the designated column's name.
     *
     * @return the column name
     *
     * @throws SQLException if a database access error occurs
     */
    public String getName() throws SQLException;

    /**
     * Retrieves the column's SQL type.
     *
     * @return the column's SQL type
     *
     * @throws SQLException if a database access error occurs
     */
    public int getType() throws SQLException;

    /**
     * Retrieves the column's database-specific type name.
     *
     * @return the column's database-specific type name
     *
     * @throws SQLException if a database access error occurs
     */
    public String getTypeName() throws SQLException;

    /**
     * Indicates whether the column is automatically numbered, 
     * thus read-only.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isAutoIncrement() throws SQLException;

    /**
     * Indicates whether a column's case matters.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isCaseSensitive() throws SQLException;

    /**
     * Indicates whether the column can be used in a where clause.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isSearchable() throws SQLException;

    /**
     * Indicates whether the column is a cash value.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isCurrency() throws SQLException;

    /**
     * Indicates the nullability of values in the column.
     *
     * @return the nullability status of the given column
     *
     * @throws SQLException if a database access error occurs
     */
    public int isNullable() throws SQLException;

    /**
     * Indicates whether values in the column are signed numbers.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isSigned() throws SQLException;

    /**
     * Indicates the column's normal maximum width in characters.
     *
     * @return the normal maximum number of characters allowed as 
     * the width of the designated column
     *
     * @throws SQLException if a database access error occurs
     */
    public int getDisplaySize() throws SQLException;

    /**
     * Gets the column's suggested title for use in printouts and displays.
     *
     * @return the column's suggested title
     *
     * @throws SQLException if a database access error occurs
     */
    public String getLabel() throws SQLException;

    /**
     * Get the column's table's schema.
     *
     * @return the schema name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getSchemaName() throws SQLException;

    /**
     * Get the column's number of decimal digits.
     *
     * @return the column's number of decimal digits
     *
     * @throws SQLException if a database access error occurs
     */
    public int getPrecision() throws SQLException;

    /**
     * Gets the column's number of digits to the right of the decimal point.
     *
     * @return the column's number of digits to the right of the decimal
     * point
     *
     * @throws SQLException if a database access error occurs
     */
    public int getScale() throws SQLException;

    /**
     * Gets the designated column's table name.
     *
     * @return the table name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getTableName() throws SQLException;

    /**
     * Gets the column's table's catalog name.
     *
     * @return the catalog name or "" if not applicable
     *
     * @throws SQLException if a database access error occurs
     */
    public String getCatalogName() throws SQLException;

    /**
     * Indicates whether the column is definitely not writable.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isReadOnly() throws SQLException;

    /**
     * Indicates whether it is possible for a write on the column to succeed.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isWritable() throws SQLException;

    /**
     * Indicates whether a write on the column will definitely succeed.
     *
     * @return <tt>true</tt> if so; <tt>false</tt> otherwise
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean isDefinitelyWritable() throws SQLException;

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
    public String getClassName() throws SQLException;

}
