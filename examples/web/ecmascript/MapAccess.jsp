<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Map Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Map Access</h3>

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
<br>

<hr>

<p>
  There are also provisions to access values in a Map by explicitly specifying
  the key using the index operator. If the keys are strings, this will even
  work using standard property access syntax, i.e. <code>$map.key</code>
</p>
<p>
  Examples:
</p>
<table border="1">
  <tr>
    <th>Expression</th>
    <th>Value</th>
  </tr>
  <tr>
    <td><code>$numberMap[7]</code></td>
    <td><c:expr value="$numberMap[7]"/></td>
  </tr>
  <tr>
    <td><code>$stringMap['eight']</code></td>
    <td><c:expr value="$stringMap['eight']"/></td>
  </tr>
  <tr>
    <td><code>$stringMap.nine</code></td>
    <td><c:expr value="$stringMap.nine"/></td>
  </tr>
  <tr>
    <td><code>$stringMap[variable]</code></td>
    <c:set var="variable" value="one"/>
    <td><c:expr value="$stringMap[variable]"/></td>
  </tr>
</table>
</p>


</body>
</html>

