<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Property Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Property Access</h3>

<p>EcmaScript uses the dot (.) notation to access elements of an object. The . operator expects an object as its left operand and the name of an object property as its right operand. The right operand should not be a string or a variable that contains a string but should be the literal name of the property without quotes.</p>

<p>Here is an example:</p>

<% request.setAttribute("myDate", new Date()); %>

<h4>Customer list of system properties</h4>

<table border="1">
  <tr>
    <th>Object</th>
    <th>Property</th>
    <th>Value</th>
  </tr>
<c:forEach var="prop" items="$systemProperties" begin="1" end="5">
  <tr>
    <td>prop</td>
    <td>prop.getKey()</td>
    <td><c:expr value="$prop.key"/></td>
  </tr>
</c:forEach>
  <tr>
    <td>Date</td>
    <td>Date.toString()</td>
    <td><c:expr value="$myDate.toString()"/></td>
  </tr>
  <tr>
    <td>Date</td>
    <td>Date.getYear()</td>
    <td><c:expr value="$myDate.year"/></td>
  </tr>
</table>

</body>
</html>

