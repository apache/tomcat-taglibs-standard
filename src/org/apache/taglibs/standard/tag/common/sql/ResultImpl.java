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
 * <p>This class creates a cached version of a <code>ResultSet</code>.
 * It's represented as a <code>Result</code> implementation, capable of 
 * returing an array of <code>Row</code> objects containing a <code>Column</code> 
 * instance for each column in the row. 
 *
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public class ResultImpl implements Result {
    private List rows;
    private ResultMetaData resultMD;
    private boolean isLimited;

    /**
     * This constructor reads the ResultSet and saves a cached
     * copy.
     *
     * @param rs an open <code>ResultSet</code>, positioned before the 
     *   first row
     * @param startRow, beginning row to be cached
     * @param isLimited, whether the query had a MaxRows limit
     * @exception if a database error occurs
     */
    public ResultImpl(ResultSet rs, int startRow, boolean isLimited) throws SQLException {
        this.isLimited = isLimited;

	ResultSetMetaData rsmd = rs.getMetaData();
	int noOfColumns = rsmd.getColumnCount();

        resultMD = new ResultMetaDataImpl(rsmd);

	rows = new ArrayList();
        int beginRow = 0;
	while (rs.next()) {
            beginRow++;
            if (beginRow >= startRow) {
                Column[] columns = new ColumnImpl[noOfColumns];

	        // JDBC uses 1 as the lowest index!
	        for (int i = 1; i <= noOfColumns; i++) {
		    Object value =  rs.getObject(i);
		    if (rs.wasNull()) {
		        value = null;
		    }
                    // 0-based indexing to be consistent w/JSTL 
                    columns[i-1] = new ColumnImpl(value, resultMD.get(i-1));
	        }
            Row currentRow = new RowImpl(columns);
            rows.add(currentRow);
            }
	}

    }

    /**
     * Returns an array of Row objects.
     *
     * @return an array of Rows, or null if there are no rows
     */
    public Row[] getRows() {
        if (rows == null) {
            return null;
        }

        Row[] rowArray = new Row[rows.size()];
        int index = 0;
	Iterator i = rows.iterator();
	while (i.hasNext()) {
            // 0-based indexing to be consistent w/JSTL 
            rowArray[index] = (Row) i.next();
            index++;
	}
        return rowArray;
    }

    /**
     * Returns the number of rows in the cached ResultSet
     *
     * @return the number of cached rows, or -1 if the Result could
     *    not be initialized due to SQLExceptions
     */
    public int getSize() {
	if (rows == null) {
	    return -1;
	}
	return rows.size();
    }

    /**
     * Returns the ResultMetaData object of the cached ResultSet
     *
     * @return the ResultMetaData object
     */
    public ResultMetaData getMetaData() {
        return resultMD;
    }

    /**
     * Returns true of the query was limited by a maximum row setting
     *
     * @return true if the query was limited by a MaxRows attribute
     */
    public boolean isLimitedByMaxRows() {
        return isLimited;
    }

}
