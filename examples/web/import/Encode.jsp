<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding</h3>

<h4>&lt;urlEncode&gt;</h4>

<table border="1" bgcolor="#dddddd">
 <tr>
  <td>ABC</td>
  <td><c:urlEncode value="abc"/></td>
 </tr>
 <tr>
  <td>123</td>
  <td><c:urlEncode value="123"/></td>
 </tr>
 <tr>
  <td>&</td>
  <td><c:urlEncode value="&"/></td>
 </tr>
 <tr>
  <td>JSTL is fun</td>
  <td><c:urlEncode value="JSTL is fun"/></td>
 </tr>
 <tr>
  <td>a=b</td>
  <td><c:urlEncode>a=b</c:urlEncode></td>        <%-- bodies work too --%>
 </tr>
</table>
