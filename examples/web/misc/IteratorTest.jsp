<%@ taglib prefix="c" uri="http://java.sun.com/jstl-el/core" %>

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
  <c:out value="${i}"/> &#149; 
</c:forEach>
--%>

<h4>Iteration with only begin specified (no items): begin="10"</h4>
(illegal)
<%--
<c:forEach var="i" begin="10">
  ${i} &#149; 
</c:forEach>
--%>

<h4>Iteration with only begin specified (with items): begin="2"</h4>

<c:forEach var="i" items="${customers}" begin="2" varStatus="status">
  index: ${status.index} &#149; 
  count: ${status.count} &#149; 
  item: ${i}<br>
</c:forEach>

<h4>Iteration with only end specified (with items): end="1"</h4>

<c:forEach var="i" items="${customers}" end="1" varStatus="status">
  index: ${status.index} &#149; 
  count: ${status.count} &#149; 
  item:  ${i}><br>
</c:forEach>
</body>
</html>
