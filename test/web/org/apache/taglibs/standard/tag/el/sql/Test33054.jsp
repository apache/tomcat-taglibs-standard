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
