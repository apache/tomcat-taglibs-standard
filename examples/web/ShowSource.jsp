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

<% pageContext.setAttribute("filepath",
     "file:" + application.getRealPath(request.getParameter("filename"))); %>
<%-- <c:expr value="$filepath"/> --%>
<c:import varReader="reader" url="$filepath">
<%-- <ex:file id="reader" file="$param:filename"> --%>
  <ex:escapeHtml reader="$reader"/>
<%-- </ex:file> --%>
</c:import>
<hr>
</body>
</html>
