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

<c:import url="${'LocalQueryString.jsp?name=' + name + '&email=' + email}" />

<hr />
<c:urlEncode value="http://foo">
  <c:param name="foo" value="bar"/>
  <c:param name="foo2" value="bar2"/>
</c:urlEncode>

<hr />
<c:urlEncode value="http://foo?a=b">
  <c:param name="foo" value="bar"/>
</c:urlEncode>
