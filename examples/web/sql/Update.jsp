<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<h1>SQL Update Execution</h1>


<!-- NOTE: the sql:driver tag is for prototyping and simple applications. You should really use a DataSource object instead --!>

<sql:driver
  var="example"
  driver="${myDbDriver}"
  url="${myDbUrl}"
/>

<hr>

<sql:transaction dataSource="${example}">

  <sql:update var="newTable">
    create table mytable (
      nameid int primary key,
      name varchar(80)
    )
  </sql:update>

<h2>Inserting three rows into table</h2>
  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  </sql:update>
  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (2,'Timo Maas')
  </sql:update>
  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (3,'Paul van Dyk')
  </sql:update>

<p>DONE: Inserting three rows into table</p>


  <sql:query var="deejays">
    SELECT * FROM mytable
  </sql:query>

</sql:transaction>


<table border="1">
  <c:forEach var="row" items="${deejays.rows}" varStatus="status">
    <%-- Get the column names for the header of the table --%>
    <c:choose>
      <c:when test="${status.count == 1}">
        <%-- Each row is a Map object key'd by the column name --%>
        <tr>
        <c:forEach var="metaData" items="${row}">
          <th><c:out value="${metaData.key}"/></th>
        </c:forEach>
        </tr>
      </c:when>
    </c:choose>
    <tr>
    <c:forEach var="column" items="${row}">
      <%-- Get the value of each column while iterating over rows --%>
      <td><c:out value="${column.value}"/></td>
    </c:forEach>
  </tr>
  </c:forEach>
</table>


<h2>Deleting second row from table</h2>

  <sql:update var="updateCount" dataSource="${example}">
    DELETE FROM mytable WHERE nameid=2
  </sql:update>

<p>DONE: Deleting second row from table</p>

<sql:query var="deejays" dataSource="${example}">
  SELECT * FROM mytable
</sql:query>


<table border="1">
  <c:forEach var="row" items="${deejays.rows}" varStatus="status">
    <%-- Get the column names for the header of the table --%>
    <c:choose>
      <c:when test="${status.count == 1}">
        <%-- Each row is a Map object key'd by the column name --%>
        <tr>
        <c:forEach var="metaData" items="${row}">
          <th><c:out value="${metaData.key}"/></th>
        </c:forEach>
        </tr>
      </c:when>
    </c:choose>
    <tr>
    <c:forEach var="column" items="${row}">
      <%-- Get the value of each column while iterating over rows --%>
      <td><c:out value="${column.value}"/></td>
    </c:forEach>
  </tr>
  </c:forEach>
</table>


<sql:update var="newTable" dataSource="${example}">
  drop table mytable
</sql:update>


</body>
</html>
