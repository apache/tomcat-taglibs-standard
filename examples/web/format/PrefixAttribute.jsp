<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl-el/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Prefix Attribute Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Prefix Attribute</h3>

<fmt:setLocale value="de"/>
<fmt:bundle basename="org.apache.taglibs.standard.examples.i18n.Resources" prefix="com.acme.labels.">
 <fmt:message key="cancel"/>
</fmt:bundle>

</body>
</html>
