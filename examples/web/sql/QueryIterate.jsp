<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<h1>SQL Query Execution using an iterator</h1>


<!-- NOTE: the sql:driver tag is for prototyping and simple applications. You should really use a DataSource object instead --!>

<sql:driver
  var="example"
  driver="$myDbDriver"
  url="$myDbUrl"
/>

<sql:transaction dataSource="$example">

  <sql:update var="newTable">
    create table mytable (
      nameid int primary key,
      name varchar(80)
    )
  </sql:update>

  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  </sql:update>
  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (2,'Timo Maas')
  </sql:update>
  <sql:update var="updateCount">
    INSERT INTO mytable VALUES (3,'Paul van Dyk')
  </sql:update>


  <sql:query var="deejays">
    SELECT * FROM mytable
  </sql:query>

</sql:transaction>

<hr>

<h2>Iterating on each Row using the MetaData</h2>

<table border="1">
  <c:forEach var="rows" items="$deejays.rows">
    <tr>
      <td> id: <c:out value="$rows.get('nameid')"/> </td>
      <td> name: <c:out value="$rows.get('name')"/> </td>
    </tr>
  </c:forEach>
</table>

<hr>

<h2>Iterating on each Column getting the MetaData</h2>

<c:forEach var="metaData" items="$deejays.metaData.columns">
  metaData: <c:out value="$metaData.name"/> <br>
</c:forEach>

<hr>

<h2>Iterating over each Row of the result</h2>

<table border="1">
  <c:forEach var="rows" items="$deejays.rows">
    <tr>
      <c:forEach var="column" items="$rows.columns">
        <td><c:out value="$column"/></td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>

<hr>

<h2>Iterating over Columns without knowing the name or index</h2>

<table border="1">
  <c:forEach var="rows" items="$deejays.rows">
      <c:forEach var="column" items="$rows.columns">
  <tr>
        <td>Name: <c:out value="$column.name"/></td>
        <td>Value: <c:out value="$column"/></td>
  </tr>
      </c:forEach>
  </c:forEach>
</table>

<hr>

<h2>Putting it all together</h2>

<table border="1">
  <tr>
    <c:forEach var="metaData" items="$deejays.metaData.columns">
      <th><c:out value="$metaData.name"/> </th>
    </c:forEach>
  </tr>
  <c:forEach var="rows" items="$deejays.rows">
    <tr>
      <c:forEach var="column" items="$rows.columns">
        <td><c:out value="$column"/></td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>

<sql:update var="newTable" dataSource="$example">
  drop table mytable
</sql:update>


</body>
</html>
