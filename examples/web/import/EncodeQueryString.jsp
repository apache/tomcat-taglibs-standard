<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example with Query String</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding with Query String</h3>

<h4>Encoding paramaters with import tag</h4>

<c:urlEncode var="name" value="horwat"/>
<c:urlEncode var="email" value="horwat@fakeaddress.com"/>

<c:import url="$'LocalQueryString.jsp?name=' + name + '&email=' + email" />
