<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<?xml version="1.0" encoding="ISO-8859-1"?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>


  <sql:setDataSource url="jdbc:derby:cactustest" driver="org.apache.derby.jdbc.EmbeddedDriver"/>

  <sql:query var="db">
    SELECT id, name, id as id1, name as name1, id as id2, name as name2 FROM Bug33054
  </sql:query>

  <c:set var="bug33054" value="" scope="application"/>
  <c:forEach var="row" items="${db.rows}">
      <c:forEach var="column" items="${row}">
        <c:set var="bug33054" value="${bug33054}${column}" scope="application"/>
      </c:forEach>
  </c:forEach>
