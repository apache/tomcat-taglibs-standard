<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Assignment Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Assignment Operators</h3>

<p>The Assignment operator has right to left associativity. Each assignment expression has a value that is the value of the right hand side</p>

<p>Here are some examples:</p>

<% request.setAttribute("a", new Integer(4)); %>
<% request.setAttribute("b", new Integer(7)); %>
<% request.setAttribute("c", new Integer(0)); %>

<h4>Operator: =</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> = <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a = b"/></td>
  </tr>
</table>

<h4>Operator: a = b == c</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> = <jx:expr value="$b"/> == <jx:expr value="$c"/></code></td>
    <td><c:expr value="$a = b == c"/></td>
  </tr>
</table>

<h4>Operator: +=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> += <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a += b"/></td>
  </tr>
</table>

<h4>Operator: -=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> -= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a -= b"/></td>
  </tr>
</table>

<h4>Operator: *=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> *= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a *= b"/></td>
  </tr>
</table>

<h4>Operator: /=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> /= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a /= b"/></td>
  </tr>
</table>

<h4>Operator: %=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> %= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a %= b"/></td>
  </tr>
</table>

<h4>Operator: <<=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> <<= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a <<= b"/></td>
  </tr>
</table>

<h4>Operator: >>=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> >>= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a >>= b"/></td>
  </tr>
</table>

<h4>Operator: >>>=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> >>>= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a >>>= b"/></td>
  </tr>
</table>

<h4>Operator: &=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> &= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a &= b"/></td>
  </tr>
</table>

<h4>Operator: |=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> |= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a |= b"/></td>
  </tr>
</table>

<h4>Operator: ^=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$a"/> ^= <jx:expr value="$b"/></code></td>
    <td><c:expr value="$a ^= b"/></td>
  </tr>
</table>


</body>
</html>
