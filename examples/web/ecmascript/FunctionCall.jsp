<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Function Call Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Function Call</h3>

<p>In EcmaScript, the () operator is used to invoke functions. The () operator evaluates each of the operands and then invokes the function specified by the first operand</p>

<p>Here is an example:</p>

<% request.setAttribute("myString", new String ("Harrison Ford")); %>


<h4>Sample list of Function calls</h4>

<table border="1">
  <tr>
    <th>Function Call</th>
    <th>Result</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>String.italics()</td>
    <td><c:expr value="$myString.italics()"/></td>
    <td>Make the specified string italic</td>
  </tr>
  <tr>
    <td>String.bold()</td>
    <td><c:expr value="$myString.bold()"/></td>
    <td>Make the specified string bold</td>
  </tr>
  <tr>
    <td>new String("Hello there")</td>
    <td><c:expr value="$new String('Hello there')"/></td>
    <td>create a new EcmaScript string</td>
  </tr>
  <tr>
    <td>Math.randon()</td>
    <td><c:expr value="$Math.random()"/></td>
    <td>Random value</td>
  </tr>
  <tr>
    <td>Math.abs(-5)</td>
    <td><c:expr value="$Math.abs(-5)"/></td>
    <td>Absolute value</td>
  </tr>
  <tr>
    <td>Math.round(57.55)</td>
    <td><c:expr value="$Math.round(57.555)"/></td>
    <td>Round to the nearest integer</td>
  </tr>
  <tr>
    <td>Math.sin(5)</td>
    <td><c:expr value="$Math.sin(5)"/></td>
    <td>Sine value</td>
  </tr>
</table>

</body>
</html>

