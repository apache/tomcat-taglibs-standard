<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Array Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Array Access</h3>

<p>EcmaScript uses the [] notation to access array elements. If the first operand is an array then the second operand (the one that goes between the brackets) should be an expression that evaluates to an integer. If the first operand is a reference to an object then the second operand should be an expression that evaluates to a string that names a property of the object.

<p>Here is an example:</p>

<h4>Favorite Actor and Role</h4>

<% request.setAttribute("myArray", new String[] {"Harrison", "Ford", "Indiana", "Jones"}); %>

<table border="1">
  <tr>
    <th>Array Index</th>
    <th>Value</th>
  </tr>
  <c:forEach var="i" begin="0" end="3" status="status">
    <c:declare id="status" type="javax.servlet.jsp.jstl.core.IteratorTagStatus"/>
    <tr>
        <%-- demonstrating how to use expression to get index --%>
      <td>Array[<c:expr value="$i"/>]</td>
        <%-- demonstrating how to use status object to get index --%>
      <td><c:expr value="$myArray[status.index]"/></td>
    </tr>
  </c:forEach>
</table>

</body>
</html>
