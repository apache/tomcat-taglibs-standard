<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Logical Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Logical Operators</h3>

<p>The Logical operators are used to perform boolean algebra</p>

<p>Here are some examples:</p>

<% request.setAttribute("a", "true"); %>
<% request.setAttribute("b", "false"); %>

<h4>Operator: && (Logical AND)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> && <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a && b"/></td>
  </tr>
</table>

<h4>Operator: || (Logical OR)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> || <jx:expr value="$b"/> </code></td>
    <td><c:expr value="$a || b"/></td>
  </tr>
</table>

<h4>Operator: ! (Logical NOT)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>!<c:expr value="$a"/></code></td>
    <td><c:expr value="$!a"/></td>
  </tr>
</table>

</body>
</html>
