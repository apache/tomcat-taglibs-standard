<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding</h3>

<h4>&lt;urlEncode&gt;</h4>

<c:urlEncode var="param1" value="$_paramValue1"/>
<c:urlEncode var="param2" value="$_paramValue2"/>
<c:urlEncode var="param3" value="$_paramValue3"/>

<c:import url="$_contextUrl" context="$_contextName">
    <c:param name="$_paramName1" value="$param1"/>
    <c:param name="$_paramName2" value="$param2"/>
    <c:param name="$_paramName3" value="$param3"/>
</c:import>
