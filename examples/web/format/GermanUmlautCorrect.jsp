<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Request Encoding Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>German Umlaut characters decoded correctly:</h3>

<fmt:requestEncoding value="UTF-8"/>

<ul>
 <li>a umlaut: <%= request.getParameter("a_umlaut") %>
 <li>o umlaut: <%= request.getParameter("o_umlaut") %>
 <li>u umlaut: <%= request.getParameter("u_umlaut") %>
</ul>

</body>
</html>