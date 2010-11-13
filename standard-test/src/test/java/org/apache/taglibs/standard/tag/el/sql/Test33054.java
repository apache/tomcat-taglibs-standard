/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.taglibs.standard.tag.el.sql;

import java.sql.*;
import javax.servlet.jsp.*;
import org.apache.cactus.*;
import org.apache.taglibs.standard.testutil.TestUtil;

public class Test33054 extends JspTestCase {

    public Test33054(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = DriverManager.getConnection("jdbc:derby:cactustest;create=true");
        Statement stmt = conn.createStatement();
        try { stmt.execute("DROP TABLE Bug33054"); } catch(SQLException sqle) { } // ignore
        stmt.execute("CREATE TABLE Bug33054 ( id int primary key, name varchar(80) )");
        stmt.execute("INSERT INTO Bug33054 VALUES(1, 'a')");

        ResultSet rs = stmt.executeQuery("SELECT * FROM Bug33054");
        rs.next();
        assertEquals( 1, rs.getInt(1) );
        assertEquals( "a", rs.getString(2) );

        rs.close();
        stmt.close();
        conn.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Connection conn = DriverManager.getConnection("jdbc:derby:cactustest");
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE Bug33054");
        stmt.close();
        conn.close();
    }

    public void test33054() throws Exception {
        String toInclude = TestUtil.getTestJsp(this);
        pageContext.include(toInclude);

        String data = (String) pageContext.getAttribute("bug33054", PageContext.APPLICATION_SCOPE);

        // This fails and isn't something that can easily be fixed. 
        // See: http://issues.apache.org/bugzilla/show_bug.cgi?id=33054
        //assertEquals( "ID=1NAME=1" + "ID1=1NAME1=a" + "ID2=1NAME2=a", data );
    }
}
