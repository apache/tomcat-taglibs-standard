<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- MessageFormat Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>MessageFormat</h3>

<fmt:formatDate type="both" var="currentDateString"/>
<fmt:parseDate value="$currentDateString" type="both" var="currentDate"/>

<ul>
 <li> String argument:<br>
  <fmt:messageFormat value="Current time: {0}">
   <fmt:messageArg value="$currentDateString"/>
  </fmt:messageFormat>
 
 <li> <tt>java.util.Date</tt> argument:<br>
  <fmt:messageFormat value="Current time: {0}">
   <fmt:messageArg value="$currentDate"/>
  </fmt:messageFormat>

 <li> Using &lt;messageArg&gt; body:<br>
  <fmt:messageFormat value="Current time: {0{">
   <fmt:messageArg>
    <fmt:formatDate type="both"/>
   </fmt:messageArg>
  </fmt:messageFormat>
</ul>

</ul>
</body>
</html>
