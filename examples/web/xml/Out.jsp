<%@ taglib prefix="x" uri="http://java.sun.com/jstl-el/xml" %>

<html>
<head>
  <title>JSTL: XML Support -- Parse / Out</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parse / Out</h3>

<x:parse var="a">
  <a>
   <b>
    <c>
     foo
    </c>
   </b>
   <d>
     bar
   </d>
  </a>
</x:parse>

<x:out select="$a//c"/>
<x:out select="$a/a/d"/>

<hr />

</body>
</html>
