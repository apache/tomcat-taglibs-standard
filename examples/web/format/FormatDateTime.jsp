<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting Support -- Date and Time Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Formatting Dates and Times</h3>

<ul>
 <li> Formatting current date as &quot;GMT&quot;: 
  <fmt:timeZone value="GMT">
   <fmt:formatDate type="both"/>
  </fmt:timeZone>

 <li> Formatting current date as &quot;GMT+1:00&quot;, and parse
      its date and time components:<br>
  <fmt:timeZone value="GMT+1:00"/>
  <fmt:formatDate type="both" dateStyle="long" timeStyle="long" var="formattedDate"/>
  <fmt:parseDate value="$formattedDate" type="both" var="parsedDate"/>
  Parsed date: <fmt:formatDate value="$parsedDate" type="date"/><br>
  Parsed time: <fmt:formatDate value="$parsedDate" type="time"/>
</ul>

</body>
</html>
