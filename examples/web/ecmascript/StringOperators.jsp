<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple String Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>String Operators</h3>

<p>EcmaScript has several operators that produce special effects when used with Strings. For instance, the + operator concatenates strings. The comparison operators compare two strings using alphabetical order. Note that all capitol letters come before all lowercase letters.</p>

<p>Here are some examples:</p>

<% request.setAttribute("strArray", new String[] {"Harrison", "Ford", "abc", "ABC", "1", "2"}); %>

<%--
<table border="1">
  <tr>
    <th>Name</th>
    <th>Value</th>
  </tr>
  <c:forEach var="i" begin="0" end="5" varStatus="status">
    <c:declare id="status" type="javax.servlet.jsp.jstl.core.IteratorTagStatus"/>
    <tr>
      <td>strArray[<c:expr value="$i"/>]</td>
      <td><c:expr value="$strArray[status.index]"/></td>
    </tr>
  </c:forEach>
</table>
--%>

<h4>Operator: +</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[0]"/> + " " + <c:expr value="$strArray[1]"/></code></td>
    <td><c:expr value="$strArray[0] + ' ' + strArray[1]"/></td>
  </tr>
</table>

<h4>Operator: &lt;</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[3]"/> &lt; <c:expr value="$strArray[2]"/></code></td>
    <td><c:expr value="$strArray[3] < strArray[2]"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[4]"/> &lt; <c:expr value="$strArray[5]"/></code></td>
    <td><c:expr value="$strArray[4] < strArray[5]"/></td>
  </tr>
</table>

<h4>Operator: &lt;=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[2]"/> &lt;= <c:expr value="$strArray[2]"/> </code></td>
    <td><c:expr value="$strArray[2] <= strArray[2]"/></td>
  </tr>
</table>

<h4>Operator: &gt;</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[3]"/> &gt; <c:expr value="$strArray[2]"/></code></td>
    <td><c:expr value="$strArray[3] > strArray[2]"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[4]"/> &gt; <c:expr value="$strArray[5]"/></code></td>
    <td><c:expr value="$strArray[4] > strArray[5]"/></td>
  </tr>
</table>

<h4>Operator: &gt;=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[2]"/> &gt;= <c:expr value="$strArray[3]"/></code></td>
    <td><c:expr value="$strArray[2] >= strArray[3]"/></td>
  </tr>
</table>

<h4>Operator: ==</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[0]"/> == <c:expr value="$strArray[0]"/></code></td>
    <td><c:expr value="$strArray[0] == strArray[0]"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[0]"/> == <c:expr value="$strArray[1]"/></code></td>
    <td><c:expr value="$strArray[0] == strArray[1]"/></td>
  </tr>
</table>

<h4>Operator: !=</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[0]"/> != <c:expr value="$strArray[1]"/></code></td>
    <td><c:expr value="$strArray[0] != strArray[1]"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$strArray[0]"/> != <c:expr value="$strArray[0]"/></code></td>
    <td><c:expr value="$strArray[0] != strArray[0]"/></td>
  </tr>
</table>


</body>
</html>
