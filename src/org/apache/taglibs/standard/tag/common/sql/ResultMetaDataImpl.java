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
import javax.servlet.jsp.jstl.sql.*;

/**
 * <p>This class encapsulates all the meta data for a result set. Instances
 * of this class are used by the <code>ResultImpl</code> instances.</p>
 *
 * @author Hans Bergsten
 * @author Justyna Horwat
 */
public class ResultMetaDataImpl implements ResultMetaData {
    private ColumnMetaData[] columnMD;

    /**
     * This constructor creates a ColumnMetaData object from the ResultSetMetaData
     *
     * @param rsmd ResultSetMetaData object
     * @exception if a database error occurs
     */
    public ResultMetaDataImpl (ResultSetMetaData rsmd) throws SQLException {
	int noOfColumns = rsmd.getColumnCount();
	columnMD = new ColumnMetaData[noOfColumns];
	getMetaDataCache(rsmd);
    }

    /**
     * Returns the ColumnMetaData for the named column
     *
     * @param name the name of the column
     * @exception if a database error occurs
     *
     * @return the ColumnMetaData object of the named column
     */
    public ColumnMetaData get(String name) {
        for (int i = 0; i < columnMD.length; i++) {
            try {
	        if (name.equals(columnMD[i].getName())) {
	            return columnMD[i];
	        }
            } catch (SQLException ex) {
                // can't get the column
            }
        }
	return null;
    }

    /**
     * Returns the ColumnMetaData for the given column index
     *
     * @param index the index of the column
     *
     * @return the ColumnMetaData object of the indexed column
     */
    public ColumnMetaData get(int index) {
        if ((index >= 0) && (index < columnMD.length)) {
            return (ColumnMetaData) columnMD[index];
        }
        return null;
    }

    /**
     * Returns an array of ColumnMetaData objects
     *
     * @return an array of ColumnMetaData objects
     */
    public ColumnMetaData[] getColumns() {
	return columnMD;
    }

    /**
     * Returns an array of ColumnMetaData instances for all columns.
     * All Column instances for a specific column in all rows share
     * the same ColumnMetaData instance.
     */
    private void getMetaDataCache(ResultSetMetaData rsmd) 
        throws SQLException {
	int noOfColumns = rsmd.getColumnCount();

	for (int i = 1; i <= noOfColumns; i++) {
	    ColumnMetaData md = new ColumnMetaDataImpl();
	    try {
		((ColumnMetaDataImpl)md).setAutoIncrement(rsmd.isAutoIncrement(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setAutoIncrementException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setCaseSensitive(rsmd.isCaseSensitive(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setCaseSensitiveException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setSearchable(rsmd.isSearchable(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setSearchableException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setCurrency(rsmd.isCurrency(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setCurrencyException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setNullable(rsmd.isNullable(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setNullableException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setSigned(rsmd.isSigned(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setSignedException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setDisplaySize(rsmd.getColumnDisplaySize(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setDisplaySizeException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setLabel(rsmd.getColumnLabel(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setLabelException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setName(rsmd.getColumnName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setNameException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setSchemaName(rsmd.getSchemaName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setSchemaNameException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setPrecision(rsmd.getPrecision(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setPrecisionException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setScale(rsmd.getScale(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setScaleException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setTableName(rsmd.getTableName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setTableNameException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setCatalogName(rsmd.getCatalogName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setCatalogNameException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setType(rsmd.getColumnType(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setTypeException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setTypeName(rsmd.getColumnTypeName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setTypeNameException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setReadOnly(rsmd.isReadOnly(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setReadOnlyException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setWritable(rsmd.isWritable(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setWritableException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setDefinitelyWritable(rsmd.isDefinitelyWritable(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setDefinitelyWritableException(e);
	    }
	    try {
		((ColumnMetaDataImpl)md).setClassName(rsmd.getColumnClassName(i));
	    }
	    catch (SQLException e) {
		((ColumnMetaDataImpl)md).setClassNameException(e);
	    }
            // 0-based indexing to be consistent w/JSTL
	    columnMD[i-1] = md;
	}
	return;
    }
}
