<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Exception Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Localized Error Page Exception</h3>

<%@ page isErrorPage="true" %>
Looking up exception's fully qualified class name in resource bundle:<br>
<fmt:locale value="de"/>
<fmt:bundle basename="org.apache.taglibs.standard.examples.i18n.Resources" var="deBundle"/>
<ul>
 <li> Localized exception message:<br>
  <fmt:exception bundle="$deBundle"/>
 <li> Localized exception message plus stack trace:<br>
  <fmt:exception bundle="$deBundle" stackTrace="true"/>
</ul>

</body>
</html>
