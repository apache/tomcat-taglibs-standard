<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: Miscellaneous -- Various Iterator Tests Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Various tests for Iterator tags</h3>

<h4>Iteration with only end specified (no items): end="10"</h4>
(illegal)
<%--
<c:forEach var="i" end="10">
  <c:expr value="$i"/> &#149; 
</c:forEach>
--%>

<h4>Iteration with only begin specified (no items): begin="10"</h4>
(illegal)
<%--
<c:forEach var="i" begin="10">
  <c:expr value="$i"/> &#149; 
</c:forEach>
--%>

<h4>Iteration with only begin specified (with items): begin="2"</h4>

<c:forEach var="i" items="$customers" begin="2" status="status">
  index: <c:expr value="$status.index"/> &#149; 
  count: <c:expr value="$status.count"/> &#149; 
  item: <c:expr value="$i"/><br>
</c:forEach>

<h4>Iteration with only end specified (with items): end="1"</h4>

<c:forEach var="i" items="$customers" end="1" status="status">
  index: <c:expr value="$status.index"/> &#149; 
  count: <c:expr value="$status.count"/> &#149; 
  item: <c:expr value="$i"/><br>
</c:forEach>
</body>
</html>
