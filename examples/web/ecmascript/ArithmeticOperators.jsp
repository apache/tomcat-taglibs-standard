<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Arithmetic Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Arithmetic Operators</h3>

<p>The Arithmetic operators perform various operations on either numeric or string operands.</p>

<p>Here are some examples:</p>

<% request.setAttribute("a", new Integer(4)); %>
<% request.setAttribute("b", new Integer(7)); %>
<% request.setAttribute("c", new Integer(0)); %>

<h4>Operator: + (Addition)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> + <c:expr value="$b"/></code></td>
    <td><c:expr value="$a + b"/></td>
  </tr>
</table>

<h4>Operator: - (Subtraction)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> - <c:expr value="$b"/></code></td>
    <td><c:expr value="$a - b"/></td>
  </tr>
</table>

<h4>Operator: * (Multiplication)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> * <c:expr value="$b"/></code></td>
    <td><c:expr value="$a * b"/></td>
  </tr>
</table>

<h4>Operator: / (Division)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> / <c:expr value="$b"/></code></td>
    <td><c:expr value="$a / b"/></td>
  </tr>
</table>

<h4>Operator: % (Modulo)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> % <c:expr value="$b"/></code></td>
    <td><c:expr value="$a % b"/></td>
  </tr>
</table>

<h4>Operator: - (Unary Negation)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>-<c:expr value="$a"/></code></td>
    <td><c:expr value="$-a"/></td>
  </tr>
</table>

<h4>Operator: ++ (Increment)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>++<c:expr value="$a"/></code></td>
    <td><c:expr value="$++a"/></td>
  </tr>
</table>

<h4>Operator: -- (Decrement)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>--<c:expr value="$a"/></code></td>
    <td><c:expr value="$--a"/></td>
  </tr>
</table>

</body>
</html>
