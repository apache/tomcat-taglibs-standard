<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Number, Currency, and Percent Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Formatting Numbers, Currencies, and Percentages</h3>

<ul>
 <li> Format as number: 
  <fmt:formatNumber value="123456789"/>

 <li> Format as currency: 
  <fmt:formatNumber value="123456789" type="currency"/>

 <li> Format as percentage: 
  <fmt:formatNumber type="percent">
   123456789
  </fmt:formatNumber>

 <li> Format as currency, parse, and print parsed result: 
  <fmt:formatNumber value="123456789" type="currency" var="cur"/>
  <fmt:parseNumber value="$cur" type="currency"/>

 <li> Parse numeric string (using default &quot;en&quot; locale) and format as currency:
  <fmt:locale value="de-de"/>
  <fmt:formatNumber value="12345.67" type="currency"/>

 <li> Parse numeric string (using 'parseLocale' locale) and format as currency:
  <fmt:locale value="de-de"/>
  <fmt:formatNumber parseLocale="de" type="currency">
   12345.67
  </fmt:formatNumber>
</ul>

</body>
</html>
