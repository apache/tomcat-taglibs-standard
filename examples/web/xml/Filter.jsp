<%@ taglib prefix="x" uri="http://java.sun.com/jstl/ea/xml" %>
<%@ taglib prefix="ex" uri="/jstl-examples-taglib" %>

<html>
<head>
  <title>JSTL: XML Support -- Parse / Filter / Expr</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parse / Filter / Expr</h3>

<ex:SPath var="spath" select="//a"/>
<x:parse var="a" filter="$spath">
 <nope>
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
 </nope>
</x:parse>

<x:out select="$a//c"/>
<x:out select="$a/a/d"/>

<hr />

</body>
</html>
