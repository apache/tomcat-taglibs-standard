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
  driver="$myDbDriver"
  jdbcURL="$myDbUrl"
/>

<hr>

<sql:transaction dataSource="$example">
  <sql:update var="newTable" dataSource="$example">
    create table mytable (
      nameid int not null,
      name varchar(80) null,
      constraint pk_mytable primary key (nameid)
    )
  </sql:update>
</sql:transaction>

<h2>Inserting three rows into table</h2>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  </sql:update>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (2,'Timo Maas')
  </sql:update>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (3,'Paul van Dyk')
  </sql:update>

<p>DONE: Inserting three rows into table</p>


<sql:query var="deejays" dataSource="$example">
  SELECT * FROM mytable
</sql:query>


<table border="1">
  <tr>
    <c:forEach var="metaData" begin="1" items="$deejays.metaData.columns">
      <th><c:expr value="$metaData.name"/> </th>
    </c:forEach>
  </tr>
  <c:forEach var="rows" begin="1" items="$deejays.rows">
    <tr>
      <c:forEach var="column" begin="1" items="$rows.columns">
        <td><c:expr value="$column"/></td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>


<h2>Deleting second row from table</h2>

  <sql:update var="updateCount" dataSource="$example">
    DELETE FROM mytable WHERE nameid=2
  </sql:update>

<p>DONE: Deleting second row from table</p>

<sql:query var="deejays" dataSource="$example">
  SELECT * FROM mytable
</sql:query>


<table border="1">
  <tr>
    <c:forEach var="metaData" begin="1" items="$deejays.metaData.columns">
      <th><c:expr value="$metaData.name"/> </th>
    </c:forEach>
  </tr>
  <c:forEach var="rows" begin="1" items="$deejays.rows">
    <tr>
      <c:forEach var="column" begin="1" items="$rows.columns">
        <td><c:expr value="$column"/></td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>



<sql:update var="newTable" dataSource="$example">
  drop table mytable
</sql:update>


</body>
</html>
