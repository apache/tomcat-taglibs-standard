<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<h1>SQL Transactions</h1>


<!-- NOTE: the sql:driver tag is for prototyping and simple applications. You should really use a DataSource object instead --!>

<sql:driver
  var="example"
  driver="$myDbDriver"
  jdbcURL="$myDbUrl"
/>

<p>You can group transactions together using the &lt;sql:transaction&gt; tag.</p>

<h2>Dropping table and creating table using a transaction</h2>

<sql:transaction dataSource="$example">
  <sql:update var="newTable" dataSource="$example">
    create table mytable (
      nameid int not null,
      name varchar(80) null,
      constraint pk_mytable primary key (nameid)
    )
  </sql:update>
</sql:transaction>

<p>DONE: Dropping table and creating table using a transaction</p>

<hr>

<h2>Populating table in one transaction</h2>

<sql:transaction dataSource="$example">
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  </sql:update>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (2,'Timo Maas')
  </sql:update>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (3,'Paul van Dyk')
  </sql:update>
</sql:transaction>

<p>DONE: Populating table in one transaction</p>

<sql:update var="newTable" dataSource="$example">
  drop table mytable
</sql:update>

</body>
</html>
