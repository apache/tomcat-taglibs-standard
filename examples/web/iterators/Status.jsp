<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: Iterator Support -- Iteration Status Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Iteration Status</h3>

<h4>Using status information: current, index, count, first, last</h4>
<table border="1">
  <tr>
    <th>index</th>
    <th>count</th>
    <th>last name</th>
    <th>first name</th>
    <th>first?</th>
    <th>last?</th>
  </tr>
  <c:forEach var="customer" items="$customers" varStatus="status">
    <tr>
      <td><c:expr value="$status.index"/></td>
      <td><c:expr value="$status.count"/></td>
      <td><c:expr value="$status.current.lastName"/></td>
      <td><c:expr value="$status.current.firstName"/></td>
      <td><c:expr value="$status.first"/></td>
      <td><c:expr value="$status.last"/></td>
    </tr>
    <c:if test="$status.last">
      <c:set var="count" value="$status.count"/>
    </c:if>  
  </c:forEach>
</table>
<p>There are <c:expr value="$count"/> customers in the list.

<p>

<h4>Iteration using range attributes</h4>
<c:forEach var="i" begin="100" end="200" step="5" varStatus="status">
  <c:if test="$status.first">
    begin:<c:expr value="$status.begin">begin</c:expr> &nbsp; &nbsp; 
      end:<c:expr value="$status.end">end</c:expr> &nbsp; &nbsp; 
     step:<c:expr value="$status.step">step</c:expr><br>
    sequence: 
  </c:if>  
  <c:expr value="$i"/> 
  <c:if test="$status.last">
    <br>There are <c:expr value="$status.count"/> numbers in the list.
  </c:if>  
</c:forEach>
<p>
</body>
</html>
