<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Bitwise Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Bitwise Operators</h3>

<p>The Bitwise operators perform boolean algebra on the individual bits of the operands or shift bits left and right. It is used to manipulate the binary numbers and binary representation of decimal integers. The operators return Nan if the operands are not integers or are too large to fit in a 32-bit integer representation.</p>

<p>Here are some examples:</p>

<% request.setAttribute("a", new Integer(13)); %>
<% request.setAttribute("b", new Integer(1)); %>
<% request.setAttribute("c", new Integer(0)); %>

<h4>Operator: & (Bitwise And)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> & <c:expr value="$b"/></code></td>
    <td><c:expr value="$a & b"/></td>
  </tr>
</table>

<h4>Operator: | (Bitwise Or)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> | <c:expr value="$b"/></code></td>
    <td><c:expr value="$a | b"/></td>
  </tr>
</table>

<h4>Operator: ^ (Bitwise Xor)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> ^ <c:expr value="$b"/></code></td>
    <td><c:expr value="$a ^ b"/></td>
  </tr>
</table>

<h4>Operator: ~ (Bitwise Not)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>~<c:expr value="$a"/></code></td>
    <td><c:expr value="$~a"/></td>
  </tr>
</table>

<h4>Operator: << (Shift Left)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> << <c:expr value="$b"/></code></td>
    <td><c:expr value="$a << b"/></td>
  </tr>
</table>

<h4>Operator: >> (Shift Right with Sign)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> >> <c:expr value="$b"/></code></td>
    <td><c:expr value="$a >> b"/></td>
  </tr>
</table>

<h4>Operator: >>> (Shift Right Zero Fill)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> >>> <c:expr value="$b"/></code></td>
    <td><c:expr value="$a >>> b"/></td>
  </tr>
</table>

</body>
</html>
