<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Parametric Replacement Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parametric Replacement</h3>

<fmt:locale value="de"/>
<fmt:bundle basename="org.apache.taglibs.standard.examples.i18n.Resources" var="deBundle"/>
<fmt:formatDate type="both" var="currentDateString"/>
<fmt:parseDate value="$currentDateString" type="both" var="currentDate"/>
<ul>
 <li> String argument:<br>
  <fmt:message key="currentTime" bundle="$deBundle">
   <fmt:messageArg value="$currentDateString"/>
  </fmt:message>
 
 <li> <tt>java.util.Date</tt> argument:<br>
  <fmt:message key="currentTime" bundle="$deBundle">
   <fmt:messageArg value="$currentDate"/>
  </fmt:message>

 <li> Using &lt;messageArg&gt; body:<br>
  <fmt:message key="currentTime" bundle="$deBundle">
   <fmt:messageArg>
    <fmt:formatDate type="both"/>
   </fmt:messageArg>
  </fmt:message>

 <li> Using 'messageArgs' attribute:<br>
  <fmt:message key="serverInfo" bundle="$deBundle" messageArgs="$messageArgs"/>
</ul>

</body>
</html>
