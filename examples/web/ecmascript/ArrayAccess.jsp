<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Array and List Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Array and List Access</h3>

<p>EcmaScript uses the [] notation to access array elements. If the first operand is an array then the second operand (the one that goes between the brackets) should be an expression that evaluates to an integer. If the first operand is a reference to an object then the second operand should be an expression that evaluates to a string that names a property of the object. The same principles work for accessing List objects (Vectors, for example).</p>

<p>Here is an example:</p>

<h4>Favorite Actor and Role</h4>

<%
   String[] array =  new String[] {"Harrison", "Ford", "Indiana", "Jones"};
   request.setAttribute("myArray", array);
   List list = new Vector();
   for (int i = 0; i < array.length; i++) {
      list.add(array[i]);
   }
   request.setAttribute("myList", list);
%>


<table border="1">
  <tr>
    <th>Index</th>
    <th>Array Value</th>
    <th>List Value</th>
  </tr>
  <c:forEach var="i" begin="0" end="3" status="status">
    <tr>
      <%-- demonstrating how to use expression to get index --%>
      <td><c:expr value="$i"/></td>
      <%-- demonstrating how to use status object to get index --%>
      <td><c:expr value="$myArray[status.index]"/></td>
      <td><c:expr value="$myList[status.index]"/></td>
    </tr>
  </c:forEach>
</table>

</body>
</html>
