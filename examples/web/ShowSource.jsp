<!--
  Displays the content of the file specified in request
  parameter "filename".
-->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<%@ taglib prefix="ex" uri="/jstl-examples-taglib" %>
<html>
<head>
  <title>JSTL: Source code for <c:expr value="$param:filename"/></title>
</head>
<body bgcolor="#FFFFFF">
<h3>Source code for:&nbsp; <c:expr value="$param:filename"/></h3>

<hr>

<% pageContext.setAttribute("filePath", "file:/" + application.getRealPath(request.getParameter("filename"))); %>

<c:import varReader="reader" url="$filePath">
  <ex:escapeHtml reader="$reader"/>
</c:import>

<hr>

</body>
</html>
