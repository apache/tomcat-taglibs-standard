<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Comparison Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Comparison Operators</h3>

<p>The Comparison operators compare values for equality and identity and return a boolean value depending on the result of the comparison. Numbers, strings, and boolean values are compared by value. Objects, arrays, and functions are compared by reference.</p>

<p>Here are some examples:</p>

<% request.setAttribute("myString", new String("Harrison Ford")); %>
<% request.setAttribute("a", new Integer(4)); %>
<% request.setAttribute("b", new Integer(7)); %>
<% request.setAttribute("c", new Integer(0)); %>

<h4>Operator: == (Equality)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> == <c:expr value="$b"/></code></td>
    <td><c:expr value="$a == b"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$myString"/> == <c:expr value="$myString"/></code></td>
    <td><c:expr value="$myString == myString"/></td>
  </tr>
</table>

<h4>Operator: != (Inequality)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> != <c:expr value="$b"/></code></td>
    <td><c:expr value="$a != b"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$myString"/> != <c:expr value="$myString"/></code></td>
    <td><c:expr value="$myString != myString"/></td>
  </tr>
</table>

<h4>Operator: === (Identity)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> === <c:expr value="$b"/></code></td>
    <td><c:expr value="$a === b"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$myString"/> === <c:expr value="$myString"/></code></td>
    <td><c:expr value="$myString === myString"/></td>
  </tr>
</table>

<h4>Operator: !== (Non-Identity)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> !== <c:expr value="$b"/></code></td>
    <td><c:expr value="$a !== b"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$myString"/> !== <c:expr value="$myString"/></code></td>
    <td><c:expr value="$myString !== myString"/></td>
  </tr>
</table>

<h4>Operator: < (Less Than)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> < <c:expr value="$b"/></code></td>
    <td><c:expr value="$a < b"/></td>
  </tr>
</table>

<h4>Operator: > (Greater Than)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> > <c:expr value="$b"/></code></td>
    <td><c:expr value="$a > b"/></td>
  </tr>
</table>

<h4>Operator: <= (Less Than or Equal)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> <= <c:expr value="$b"/></code></td>
    <td><c:expr value="$a <= b"/></td>
  </tr>
</table>

<h4>Operator: >= (Greater Than or Equal)</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> >= <c:expr value="$b"/></code></td>
    <td><c:expr value="$a >= b"/></td>
  </tr>
</table>

</body>
</html>
