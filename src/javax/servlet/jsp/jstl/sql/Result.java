/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

import java.util.SortedMap;

/**
 * <p>This interface represents the result of a &lt;sql:query&gt;
 * action. It provides access to the following information in the
 * query result:</p>
 *
 * <ul>
 * <li> The result rows (<tt>getRows()</tt> and <tt>getRowsByIndex()</tt>)
 * <li> The column names (<tt>getColumnNames()</tt>)
 * <li> The number of rows in the result (<tt>getRowCount()</tt>)
 * <li> An indication whether the rows returned represent the complete result 
 *      or just a subset that is limited by a maximum row setting
 *      (<tt>isLimitedByMaxRows()</tt>)
 * </ul>
 *
 * <p>An implementation of the <tt>Result</tt> interface provides a
 * <i>disconnected</i> view into the result of a query.
 *
 * @author Justyna Horwat
 *
 */
public interface Result {

    /**
     * <p>Returns the result of the query as an array of <code>SortedMap</code> objects. 
     * Each item of the array represents a specific row in the query result.</p>
     *
     * <p>A row is structured as a <code>SortedMap</code> object where the key is the column name, 
     * and where the value is the value associated with the column identified by 
     * the key. The column value is an Object of the Java type corresponding 
     * to the mapping between column types and Java types defined by the JDBC 
     * specification when the <code>ResultSet.getObject()</code> method is used.</p>
     *
     * <p>The <code>SortedMap</code> must use the <code>Comparator</code> 
     * <code>java.util.String.CASE_INSENSITIVE_ORDER</code>. 
     * This makes it possible to access the key as a case insensitive representation 
     * of a column name. This method will therefore work regardless of the case of 
     * the column name returned by the database.</p>
     *
     * @return The result rows as an array of <code>SortedMap</code> objects
     */
    public SortedMap[] getRows();

    /**
     * Returns the result of the query as an array of arrays. 
     * The first array dimension represents a specific row in the query result. 
     * The array elements for each row are Object instances of the Java type 
     * corresponding to the mapping between column types and Java types defined 
     * by the JDBC specification when the <code>ResultSet.getObject()</code> method is used.
     *
     * @return the result rows as an array of <code>Object[]</code> objects
     */
    public Object[][] getRowsByIndex();

    /**
     * Returns the names of the columns in the result. The order of the names in the array 
     * matches the order in which columns are returned in method getRowsByIndex().
     *
     * @return the column names as an array of <code>String</code> objects
     */
    public String[] getColumnNames();

    /**
     * Returns the number of rows in the cached ResultSet
     *
     * @return the number of rows in the result
     */
    public int getRowCount();

    /**
     * Returns true if the query was limited by a maximum row setting
     *
     * @return <tt>true</tt> if the query was limited by a maximum
     * row setting
     */
    public boolean isLimitedByMaxRows();
}
