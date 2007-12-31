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
