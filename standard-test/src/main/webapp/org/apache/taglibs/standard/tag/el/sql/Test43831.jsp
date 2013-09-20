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
    SELECT name as wonka FROM Bug43831
  </sql:query>

  <c:set var="bug43831Label" value="" scope="application"/>
  <c:set var="bug43831Name" value="" scope="application"/>
  <c:forEach var="row" items="${db.rows}">
      <c:set var="bug43831Label" value="${row.wonka}" scope="application"/>
      <c:set var="bug43831Name" value="${row.name}" scope="application"/>
  </c:forEach>

  <c:forEach var="columnName" items="${db.columnNames}">
      <c:set var="bug43831" value="${columnName}" scope="application"/>
  </c:forEach>
