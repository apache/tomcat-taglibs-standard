<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding</h3>

<h4>&lt;urlEncode&gt;</h4>

<c:urlEncode var="name" value="horwat"/>
<c:urlEncode var="email" value="horwatMail"/>
<c:urlEncode var="action" value="Submit"/>

<c:import url="/jsp/cal/cal1.jsp" context="/examples">
    <c:param name="name" value="$name"/>
    <c:param name="email" value="$email"/>
    <c:param name="action" value="$action"/>
</c:import>

<%--
--%>
