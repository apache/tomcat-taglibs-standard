<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Map.Entry Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Map.Entry Access</h3>

<p>In EcmaScript you can access the key and corresponding value of the Map.Entry object directly.</p>

<p>Here is an example:</p>

<h4>Customer list of system properties</h4>

<table border="1">
  <tr>
    <th>Map.Entry Object</th>
    <th>Value</th>
    <th>Key</th>
  </tr>
<c:forEach var="prop" items="$numberMap" begin="1" end="5">
  <tr>
    <td>prop</td>
    <td><c:expr value="$prop.value"/></td>
    <td><c:expr value="$prop.key"/></td>
  </tr>
</c:forEach>
</table>

</body>
</html>

