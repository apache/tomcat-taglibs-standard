<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/ea/fmt" %>

<html>
<head>
  <title>JSTL: Formatting/I18N Support -- Number, Currency, and Percent Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Formatting Numbers, Currencies, and Percentages</h3>

<ul>
 <li> Format &quot;123456789&quot; as number:<br>
  <fmt:formatNumber value="123456789"/>

 <li> Format &quot;123456789&quot; as currency (using browser locale):<br>
  <fmt:formatNumber value="123456789" type="currency"/>

 <li> Format &quot;123456789&quot; as currency with &quot;EUR&quot; currency
      code:<br>
  <fmt:formatNumber value="123456789" type="currency" currencyCode="EUR"/>

 <li> Format &quot;123456789&quot; as currency (using browser locale), with
      grouping turned off, the maximum number of digits in the integer portion
      limited to 4, and no fraction portion:<br>
  <fmt:formatNumber value="123456789" type="currency" groupingUsed="false" maxIntegerDigits="4" maxFractionDigits="0"/>

 <li> Format &quot;123456789&quot; as percentage:<br>
  <fmt:formatNumber type="percent">
   123456789
  </fmt:formatNumber>

 <li> Format &quot;123456789&quot; as currency (using browser locale):<br>
  <fmt:formatNumber value="123456789" type="currency"/><br>
      then parse it back in and print the parsed result:<br> 
  <fmt:formatNumber value="123456789" type="currency" var="cur"/>
  <fmt:parseNumber value="$cur" type="currency"/>

 <li> Format &quot;12345.67&quot; as US Dollar:<br>
  <fmt:locale value="en-US"/>
  <fmt:formatNumber value="12345.67" type="currency"/><br>
      then parse its integer portion only and output the result:<br>
  <fmt:formatNumber value="12345.67" type="currency" var="cur"/>
  <fmt:parseNumber value="$cur" type="currency" integerOnly="true"/>

 <li> Format &quot;12345.67&quot; as German currency (given string is
      parsed using default &quot;en&quot; locale before it is formatted):<br>
  <fmt:locale value="de-DE"/>
  <fmt:formatNumber value="12345.67" type="currency"/>

 <li> Format &quot;12345.67&quot; as German currency (given string is parsed
      using &quot;de&quot; locale before it is formatted):<br>
  <fmt:locale value="de-DE"/>
  <fmt:formatNumber parseLocale="de" type="currency">
   12345.67
  </fmt:formatNumber>
 </ul>

</body>
</html>
