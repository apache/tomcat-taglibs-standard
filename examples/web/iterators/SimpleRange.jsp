<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: Iterator Support -- Simple Range Iteration Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Simple Range Iteration</h3>

<h4>1 to 10</h4>

<c:forEach var="i" begin="1" end="10">
  <c:expr value="$i"/> &#149;
</c:forEach>
</body>
</html>
