<!--
  Displays the content of the file specified in request
  parameter "filename".
-->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<%@ taglib prefix="ex" uri="/jstl-examples-taglib" %>

<% pageContext.setAttribute("filepath",
     "file:" + application.getRealPath(request.getParameter("filename"))); %>
<% pageContext.setAttribute("filename", request.getParameter("filename")); %>

<html>
<head>
  <title>JSTL: Source code for <c:expr value="$filename"/></title>
</head>
<body bgcolor="#FFFFFF">
<h3>Source code for:&nbsp; <c:expr value="$filename"/></h3>

<hr>

<%-- <c:expr value="$filepath"/> --%>
<c:import varReader="reader" url="$filepath">
<%-- <ex:file id="reader" file="$param:filename"> --%>
  <ex:escapeHtml reader="$reader"/>
<%-- </ex:file> --%>
</c:import>
<hr>
</body>
</html>
