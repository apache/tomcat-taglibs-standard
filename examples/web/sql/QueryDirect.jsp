<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<h1>SQL Direct Query Execution</h1>
<p>This example demonstrates how the row and columns can be directly accessed using various direct mechanisms.<p>


<!-- NOTE: the sql:driver tag is for prototyping and simple applications. You should really use a DataSource object instead --!>

<sql:driver
  var="example"
  driver="$myDbDriver"
  jdbcURL="$myDbUrl"
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
    INSERT INTO mytable VALUES (2,'Timo Maas')
  </sql:update>
  <sql:update var="updateCount" dataSource="$example">
    INSERT INTO mytable VALUES (3,'Paul van Dyk')
  </sql:update>
</sql:transaction>


<sql:query var="deejays" dataSource="$example">
  SELECT * FROM mytable
</sql:query>

<hr>

<h2>Using the Row index and Column name</h2>
Row[1].get('nameid'): <c:expr value="$deejays.rows[1].get('nameid')" />
<br>
Row[1].get('name'): <c:expr value="$deejays.rows[1].get('name')" />
<br>
Row[2].get('nameid'): <c:expr value="$deejays.rows[2].get('nameid')" />
<br>
Row[2].get('name'): <c:expr value="$deejays.rows[2].get('name')" />
<br>
Row[3].get('nameid'): <c:expr value="$deejays.rows[3].get('nameid')" />
<br>
Row[3].get('name'): <c:expr value="$deejays.rows[3].get('name')" />
<br>

<hr>

<h2>Using the Row index and Column object</h2>
Row[1]Cols[1]: <c:expr value="$deejays.rows[1].columns[1]" />
<br>
Row[1]Cols[2]: <c:expr value="$deejays.rows[1].columns[2]" />
<br>
Row[2]Cols[1]: <c:expr value="$deejays.rows[2].columns[1]" />
<br>
Row[2]Cols[2]: <c:expr value="$deejays.rows[2].columns[2]" />
<br>
Row[3]Cols[1]: <c:expr value="$deejays.rows[3].columns[1]" />
<br>
Row[3]Cols[2]: <c:expr value="$deejays.rows[3].columns[2]" />
<br>

<hr>

<h2>Using the Row and Column indexes</h2>
Row[1].get(1): <c:expr value="$deejays.rows[1].get(1)" />
<br>
Row[1].get(2): <c:expr value="$deejays.rows[1].get(2)" />
<br>
Row[2].get(1): <c:expr value="$deejays.rows[2].get(1)" />
<br>
Row[2].get(2): <c:expr value="$deejays.rows[2].get(2)" />
<br>
Row[3].get(1): <c:expr value="$deejays.rows[3].get(1)" />
<br>
Row[3].get(2): <c:expr value="$deejays.rows[3].get(2)" />
<br>

<hr>

<h2>Getting the MetaData from the Column Object</h2>
Col[1]MetaData: <c:expr value="$deejays.metaData.columns[1].name" />
<br>
Col[2]MetaData: <c:expr value="$deejays.metaData.columns[2].name" />

<sql:update var="newTable" dataSource="$example">
  drop table mytable
</sql:update>

</body>
</html>
