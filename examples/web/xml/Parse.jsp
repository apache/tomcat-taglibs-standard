<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/ea/xml" %>

<html>
<head>
  <title>JSTL: XML Support -- Parse from Objects and URLs</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parse from Objects and URLs</h3>

<c:set var="xmlText">
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
</c:set>    

<x:parse var="a" xmlText="$xmlText" />

<x:expr select="$a//c"/>
<x:expr select="$a/a/d"/>

<hr />

<x:parse var="a" xmlUrl="http://www.cnn.com/cnn.rss" />

Title of news feed: "<x:expr select="$a//title"/>"

</body>
</html>
