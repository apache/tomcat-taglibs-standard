<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Conditional Operator Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Conditional Operators</h3>

<p>The Conditional operator ?: can be used as a short cut for an if/else statement. A JSTL conditional tag can also be used to verify the test condition.</p>

<% request.setAttribute("true", "true"); %>
<% request.setAttribute("false", "false"); %>
<% request.setAttribute("ifResult", "IF result"); %>
<% request.setAttribute("elseResult", "ELSE result"); %>
<% request.setAttribute("myDate", new Date()); %>

<p>Here are some examples:</p>

<h4>Conditional Operators:</h4>
<table border="1">
  <tr>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code><c:expr value="$true"/> ? <jx:expr value="$ifResult"/> : <jx:expr value="$elseResult"/> </code></td>
    <td><c:expr value="$true ? ifResult : elseResult"/></td>
  </tr>
  <tr>
    <td><code><c:expr value="$false"/> ? <jx:expr value="$ifResult"/> : <jx:expr value="$elseResult"/> </code></td>
    <td><c:expr value="$false ? ifResult : elseResult"/></td>
  </tr>
  <tr>
    <td><code>&lt;c:if test="$true == true"&gt; IF result &lt;/jx:if&gt; </code></td>
    <td><c:if test="$true == true"> IF result</jx:if> </td>
  </tr>
  <tr>
    <td><code>&lt;c:if test="$false == true"&gt; IF result &lt;/jx:if&gt; </code></td>
    <td><c:if test="$false == true"> IF result </jx:if> </td>
  </tr>
  <tr>
    <td><code>&lt;c:if test="$myDate.year == 101"&gt; 1900 + 101 = 2001 &lt;/jx:if&gt; </code></td>
    <td><c:if test="$myDate.year == 101"> 1900 + 101 = 2001</jx:if> </td>
  </tr>
</table>

</body>
</html>
