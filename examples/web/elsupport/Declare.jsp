<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>
<%@ taglib prefix="ex" uri="/jstl-examples-taglib" %>

<html>
<head>
  <title>JSTL: Expression Language Support -- Declare Example</title>
</head>
<body bgcolor="#FFFFFF">

<h3>&lt;declare&gt;</h3>

<h4>Using custom tag that requires an rtexprvalue (&lt;customerFmt&gt; with short format)</h4>
<c:forEach var="customer" items="$customers">
  <c:declare id="customer" type="org.apache.taglibs.standard.examples.beans.Customer"/>
  <ex:customerFmt customer="<%= customer%>" fmt="short"/><br>
</c:forEach>

<p>

<h4>Using custom tag that requires an rtexprvalue (&lt;customerFmt&gt; with long format)</h4>
<c:forEach var="customer" items="$customers">
  <c:declare id="customer" type="org.apache.taglibs.standard.examples.beans.Customer"/>
  <ex:customerFmt customer="<%= customer%>" fmt="long"/><br>
</c:forEach>
</body>
</html>
