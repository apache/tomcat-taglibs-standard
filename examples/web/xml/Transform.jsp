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
<x:transform xmlText="$xml" xsltText="$xsl"/>

<hr />

Prints "header" in normal size:<br />
<x:transform xmlText="$xml" xsltText="$xsl" var="doc"/>
<x:out select="$doc//h1"/>

<%--
<hr />
  Prints "header" as a header again:
  <x:transformer xslt="$xsl" var="transformer"/>
  <x:transform xmlText="$xml" transformer="$transformer"/>
<hr />

Prints "Second header" as a header:
<x:transform transformer="$transformer">
  <blah><blah><blah>Second header</blah></blah></blah>
</x:transform>

--%>

<hr size="5" />

<h3>Transformations using URLs</h3>

<c:set var="xslt">
  <?xml version="1.0"?>
  <xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="text()">
    <p><xsl:value-of select="."/></p>
  </xsl:template>

  </xsl:stylesheet>
</c:set>

<x:transform xsltText="$xslt" xmlUrl="http://www.cnn.com/cnn.rss" />

<hr />
<h3>Transformations using output from XPath expressions</h3>

<x:parse var="xml" xmlText="$xml" />
<x:set var="miniDoc" select="$xml//b" />
<x:transform xsltText="$xslt" xmlText="$miniDoc" />
<hr />

<h3>Inline transformations</h3>

<x:transform xsltText="$xslt">
  <a>
   <b>
    <c>Paragraph one!</c>
    <c>Paragraph foo!</c>
   </b>
  </a>
</x:transform>

</body>
</html>
