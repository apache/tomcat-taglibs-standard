<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<% request.setAttribute("newName", new String("Paul van Dyk")); %>

<h1>SQL Query Execution using parameters</h1>
<p>Using parameter marker's to insert values in the SQL statements</p>



<!-- NOTE: the sql:driver tag is for prototyping and simple applications. You should really use a DataSource object instead --!>

<sql:driver
  var="example"
  driver="$myDbDriver"
  url="$myDbUrl"
/>

<sql:transaction dataSource="$example">

  <sql:update var="newTable" dataSource="$example">
    create table mytable (
      nameid int not null,
      name varchar(80) null,
      constraint pk_mytable primary key (nameid)
    )
  </sql:update>

  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  </sql:update>

  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (?,'Timo Maas')
      <sql:param value="2"/>
  </sql:update>

  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (?,?)
      <sql:param value="3"/>
      <sql:param value="$newName"/>
  </sql:update>

<sql:query var="deejay" dataSource="$example">
  SELECT * FROM mytable
</sql:query>

</sql:transaction>

<table border="1">
  <c:forEach var="rows" items="$deejay.rows">
    <tr>
      <td><c:expr value="$rows.get('nameid')"/></td>
      <td><c:expr value="$rows.get('name')"/></td>
    </tr>
    </c:forEach>
</table>

<sql:update var="newTable" dataSource="$example">
  drop table mytable
</sql:update>


</body>
</html>
