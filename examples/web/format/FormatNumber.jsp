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
  <fmt:formatNumber value="123456789" type="percent"/>

 <li> Format as currency, parse, and print parsed result: 
  <fmt:formatNumber value="123456789" type="currency" var="cur"/>
  <fmt:parseNumber value="$cur" type="currency"/>
</ul>

</body>
</html>
