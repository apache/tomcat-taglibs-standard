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
import java.util.*;

/**
 * <p>This class creates a cached version of a <code>ResultSet</code>.
 * It's represented as a <code>Result</code> implementation, capable of 
 * returing an array of <code>Row</code> objects containing a <code>Column</code> 
 * instance for each column in the row. 
 *
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public class ResultImpl implements Result {
    private List rowMap;
    private List rowByIndex;
    private ResultMetaDataUtil resultMD;
    private boolean isLimited;

    /**
     * This constructor reads the ResultSet and saves a cached
     * copy.
     *
     * @param rs an open <code>ResultSet</code>, positioned before the 
     *   first row
     * @param startRow, beginning row to be cached
     * @param maxRows, query maximum rows limit
     * @exception if a database error occurs
     */
    public ResultImpl(ResultSet rs, int startRow, int maxRows) throws SQLException {
	rowMap = new ArrayList();
	rowByIndex = new ArrayList();

	ResultSetMetaData rsmd = rs.getMetaData();
        resultMD = new ResultMetaDataUtil(rsmd);

	int noOfColumns = rsmd.getColumnCount();
        int beginRow = 0;

	while (rs.next()) {
            if ((maxRows < 0) || (beginRow < maxRows)) {
                if (beginRow >= startRow) {
                    Object[] columns = new Object[noOfColumns];
                    Map columnMap = new HashMap();

	            // JDBC uses 1 as the lowest index!
	            for (int i = 1; i <= noOfColumns; i++) {
		        Object value =  rs.getObject(i);
		        if (rs.wasNull()) {
		            value = null;
		        }
                        // 0-based indexing to be consistent w/JSTL 
                        columns[i-1] = value;
                        columnMap.put((resultMD.get(i-1)).getName(),value);
	            }
                rowMap.add(columnMap);
                rowByIndex.add(columns);
                }
            beginRow++;
            }
	}

        if (maxRows > 0) { 
            isLimited = true; 
        } else { 
            isLimited = false; 
        }
    }

    /**
     * Returns an array of Map objects. The Map object key is
     * the ColumnName and the value is the ColumnValue.
     *
     * @return an array of Map, or null if there are no rows
     */
    public Map[] getRows() {
        if (rowMap == null) {
            return null;
        }

        //should just be able to return Map[] object
        return (Map []) rowMap.toArray(new Map[0]);
    }


    /**
     * Returns an array of Object[] objects. The first index
     * designates the Row, the second the Column. The array
     * stores the value at the specified row and column.
     *
     * @return an array of Object[], or null if there are no rows
     */
    public Object[][] getRowsByIndex() {
        if (rowByIndex == null) {
            return null;
        }

        //should just be able to return Object[][] object
        return (Object [][])rowByIndex.toArray(new Object[0][0]);
    }

    /**
     * Returns the number of rows in the cached ResultSet
     *
     * @return the number of cached rows, or -1 if the Result could
     *    not be initialized due to SQLExceptions
     */
    public int getRowsCount() {
	if (rowMap == null) {
	    return -1;
	}
	return rowMap.size();
    }

    /**
     * Returns the ColumnMetaData array object of the cached ResultSet
     *
     * @return the ColumnMetaData array object
     */
    public ColumnMetaData[] getMetaData() {
        return resultMD.getColumns();
    }

    /**
     * Returns true of the query was limited by a maximum row setting
     *
     * @return true if the query was limited by a MaxRows attribute
     */
    public boolean isLimitedByMaxRows() {
        return isLimited;
    }

    /**
     * <p>This class encapsulates all the meta data for a result set. Instances
     * of this class are used by the <code>ResultImpl</code> instances.</p>
     *
     * @author Hans Bergsten
     * @author Justyna Horwat
     */
    private class ResultMetaDataUtil {
        private ColumnMetaData[] columnMD;

        /**
         * This constructor creates a ColumnMetaData object from the ResultSetMetaData
         *
         * @param rsmd ResultSetMetaData object
         * @exception if a database error occurs
         */
        public ResultMetaDataUtil (ResultSetMetaData rsmd) throws SQLException {
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
}
