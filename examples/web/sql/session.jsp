<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
   <title>Jakarta DBTAGS Taglib Example</title>
</head>
<body bgcolor="white">

<%
  String _url = request.getParameter("dbUrl");
  session.setAttribute("myDbUrl", _url);
  String _driver = request.getParameter("dbDriver");
  session.setAttribute("myDbDriver", _driver);
%>

<%@ include file="links.html" %>

</body>
</html>
