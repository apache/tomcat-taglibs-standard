<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/ea/sql" %>

<html>
<head>
  <title>JSTL: SQL action examples</title>
</head>
<body bgcolor="#FFFFFF">

<h1>SQL Driver Setup Example</h1>

<code>
<pre>
&lt;sql:driver
  var="example"
  driver="RmiJdbc.RJDriver"
  jdbcURL="jdbc:rmi://gibson:1099/jdbc:cloudscape:CloudscapeDB"
/&gt;
</pre>
</code>

</body>
</html>
