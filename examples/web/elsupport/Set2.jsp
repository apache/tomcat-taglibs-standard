<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<html>
<head>
  <title>JSTL: Expression Language Support -- Set 2 Example</title>
</head>
<body bgcolor="#FFFFFF">

<h3>&lt;set&gt;</h3>

<h4>Using "customerTable" application scope attribute defined in Set.jsp a first time</h4>
<c:out value="${customerTable}"/>

<h4>Using "customerTable" application scope attribute defined in Set.jsp a second time</h4>
<c:out value="${customerTable}"/>
</body>
</html>
