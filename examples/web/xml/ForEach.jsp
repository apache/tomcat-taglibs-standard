<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>

<html>
<head>
  <title>JSTL: XML Support -- Parse / ForEach</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parse / ForEach</h3>

<x:parse var="document">
 <doc>
  <a>
   <b foo="foo">foo 1</b>
  </a>
  <a>
   <c foo="bar">bar 2</c>
  </a>
  <a>
   <d foo="bar">bar 3</d>
  </a>
  <a>
   <d foo="foo">foo 4</d>
  </a>
 </doc>
</x:parse>

<x:forEach select="$document//a">
  -> <x:out select="."/>
  <br />
</x:forEach>

<hr />

<x:forEach select="$document//a">
  -> 
  <x:if select=".//d">
    &lt;d&gt; element present
  </x:if>
  <br />
</x:forEach>

</body>
</html>
