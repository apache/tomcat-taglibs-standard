<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<html>
<head>
  <title>JSTL: Expression Language Support -- Expr Example</title>
</head>
<body bgcolor="#FFFFFF">

<h3>&lt;expr&gt;</h3>

<table border="1">
  <c:forEach var="customer" items="${customers}">
    <tr>
	  <td><c:out value="${customer.lastName}"/></td>
	  <td><c:out value="${customer.phoneHome}" default="no home phone specified"/></td>
	  <td>
	    <c:out value="${customer.phoneCell}">
		  <font color="red">no cell phone specified</font>
		</c:out>
      </td>
	</tr>
  </c:forEach>
</table>
</body>
</html>
