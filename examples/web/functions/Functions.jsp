<%@ taglib prefix="c" uri="http://java.sun.com/jstl-el/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jstl-el/functions" %>

<html>
<head>
  <title>JSTL: Functions</title>
</head>
<body bgcolor="#FFFFFF">

<c:set var="s1" value="Tanenbaum, David"/>

<h4>fn:toLowerCase</h4>
Before: ${s1} <br>
After:  ${fn:toLowerCase(s1)}

<h4>fn:toUpperCase</h4>
Before: ${s1} <br>
After:  ${fn:toUpperCase(s1)}

</body>
</html>
