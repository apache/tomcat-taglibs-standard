<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Date and Time Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Formatting Dates and Times</h3>

<ul>
 <li> Formatting current date as &quot;GMT&quot;:<br>
  <fmt:timeZone value="GMT">
   <fmt:formatDate type="both" dateStyle="full" timeStyle="full">
   </fmt:formatDate>
  </fmt:timeZone>

 <li> Formatting current date as &quot;GMT+1:00&quot;, and parsing
      its date and time components:<br>
  <fmt:timeZone value="GMT+1:00">
   <fmt:formatDate type="both" dateStyle="full" timeStyle="full" var="formattedDateTime"/>
   <fmt:parseDate value="${formattedDateTime}" type="both" dateStyle="full" timeStyle="full" timeZone="PST" var="parsedDateTime"/>
   Parsed date: <fmt:formatDate value="${parsedDateTime}" type="date" dateStyle="full"/><br>
   Parsed time: <fmt:formatDate value="${parsedDateTime}" type="time" timeStyle="full"/>
  </fmt:timeZone>

 <li> Parsing SHORT version of current time in different time zones:<br>
  <fmt:formatDate type="both" timeStyle="short" var="formattedDateTime"/>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="short" timeZone="GMT"/> (parsed in &quot;GMT&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="short" timeZone="GMT+1:00"/> (parsed in &quot;GMT+1:00&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="short" timeZone="GMT+3:00"/> (parsed in &quot;GMT+3:00&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="short" timeZone="PST"/> (parsed in &quot;PST&quot;)

 <li> Parsing FULL version of current time in different time zones (should not be affected):<br>
  <fmt:formatDate type="both" timeStyle="full" var="formattedDateTime"/>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="full" timeZone="GMT"/> (parsed in &quot;GMT&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="full" timeZone="GMT+1:00"/> (parsed in &quot;GMT+1:00&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="full" timeZone="GMT+3:00"/> (parsed in &quot;GMT+3:00&quot;)<br>
  <fmt:parseDate value="${formattedDateTime}" type="both" timeStyle="full" timeZone="PST"/> (parsed in &quot;PST&quot;)
</ul>

</body>
</html>
