<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example with Query String</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding with Query String</h3>

<h4>&lt;urlEncode&gt;</h4>

<c:urlEncode var="name" value="horwat"/>
<c:urlEncode var="email" value="horwatMail"/>
<c:urlEncode var="action" value="Submit"/>

<c:import url="$'/jsp/cal/cal1.jsp?name=' + name + '&email=' + email + '&action=' + action" context="/examples"/>

