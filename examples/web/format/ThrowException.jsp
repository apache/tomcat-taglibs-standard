<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>Throw Exception</title>
</head>
<body bgcolor="#FFFFFF">

<%@ page errorPage="Exception.jsp" %>
<%-- throws java.lang.ArithmeticException: / by zero --%>
<%
int i = 5/0;
%>

</body>
</html>
