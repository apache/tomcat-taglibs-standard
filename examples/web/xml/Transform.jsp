<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/ea/xml" %>

<html>
<head>
  <title>JSTL: XML Support -- Transform</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parse / Expr</h3>

<c:set var="xml">
  <a><b>header!</b></a>
</c:set>

<c:set var="xsl">
  <?xml version="1.0"?>
  <xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="text()">
    <h1><xsl:value-of select="."/></h1>
  </xsl:template>

  </xsl:stylesheet>
</c:set>

Prints "header" as a header:<br />
<x:transform source="$xml" xslt="$xsl"/>

<hr />

Prints "header" in normal size:<br />
<x:transform source="$xml" xslt="$xsl" var="doc"/>
<x:expr select="$doc//h1"/>

<hr />

Prints "header" as a header again:
<x:transformer xslt="$xsl" var="transformer"/>
<x:transform source="$xml" transformer="$transformer"/>

<hr />

Prints "Second header" as a header:
<x:transform transformer="$transformer">
  <blah><blah><blah>Second header</blah></blah></blah>
</x:transform>

</body>
</html>
