<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple String Support Examples</title>
</head>
<body bgcolor="#FFFFFF">
<h3>String Support</h3>

<p>The EcmaScript String object type has methods for operating on string values. Many of these methods simply make a copy of the string with HTML tags added before and after. Other methods can extract a character or substring or serach for a character in a substring. Here are examples of a few of these methods</p>

<p>Here are some examples:</p>

<% request.setAttribute("fullString", new String("Harrison Ford")); %>
<% request.setAttribute("numString", new String("7/13/1942: Happy Birthday")); %>
<% request.setAttribute("harrison", new String("Harrison")); %>
<% request.setAttribute("ford", new String("Ford")); %>
<% request.setAttribute("rocks", new String(" Rocks")); %>
<% request.setAttribute("a", new String("a")); %>
<% request.setAttribute("two", new String("2")); %>
<% request.setAttribute("five", new String("5")); %>
<% request.setAttribute("myColor", new String("00FF00")); %>
<% request.setAttribute("mySize", new Integer("5")); %>

<h4>String Support Examples</h4>
<table border="1" width="95%">
  <tr>
    <th>String Value</th>
    <th>Operation</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.anchor()</code></td>
    <td><c:expr value="$fullString.anchor(harrison)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.big()</code></td>
    <td><c:expr value="$fullString.big()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.bold()</code></td>
    <td><c:expr value="$fullString.bold()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.charAt(<c:expr value="$two"/>)</code></td>
    <td><c:expr value="$fullString.charAt(two)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.concat(<c:expr value="$rocks"/>)</code></td>
    <td><c:expr value="$fullString.concat(rocks)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.fontcolor(<c:expr value="$myColor"/>)</code></td>
    <td><c:expr value="$fullString.fontcolor(myColor)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.fontSize(<c:expr value="$mySize"/>)</code></td>
    <td><c:expr value="$fullString.fontsize(mySize)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.indexOf(<c:expr value="$ford"/>)</code></td>
    <td><c:expr value="$fullString.indexOf(ford)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.italics()</code></td>
    <td><c:expr value="$fullString.italics()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.length</code></td>
    <td><c:expr value="$fullString.length"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.link(<c:expr value="$ford"/>)</code></td>
    <td><c:expr value="$fullString.link(ford)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.slice(<c:expr value="$two"/>, <jx:expr value="$five"/>)</code></td>
    <td><c:expr value="$fullString.slice(two, five)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.small()</code></td>
    <td><c:expr value="$fullString.small()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.strike()</code></td>
    <td><c:expr value="$fullString.strike()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.sub()</code></td>
    <td><c:expr value="$fullString.sub()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.substring(<c:expr value="$two"/>, <jx:expr value="$five"/>)</code></td>
    <td><c:expr value="$fullString.substring(two, five)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.toLowerCase()</code></td>
    <td><c:expr value="$fullString.toLowerCase()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$fullString"/></td>
    <td><code>String.toUpperCase()</code></td>
    <td><c:expr value="$fullString.toUpperCase()"/></td>
  </tr>
  <tr>
    <td><c:expr value="$numString"/></td>
    <td><code>parseInt(<c:expr value="$numString"/>)</code></td>
    <td><c:expr value="$parseInt(numString)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$numString"/></td>
    <td><code>escape(<c:expr value="$numString"/>)</code></td>
    <td><c:expr value="$escape(numString)"/></td>
  </tr>
  <tr>
    <td><c:expr value="$escape(numString)"/></td>
    <td><code>unescape(<c:expr value="$escape(numString)"/>)</code></td>
    <td><c:expr value="$unescape(escape(numString))"/></td>
  </tr>
</table>

</body>
</html>
