<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
  <title>JSTL: I/O Support -- URL Encoding Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>URL Encoding</h3>

<h4>&lt;c:url&gt;</h4>

<table border="1" bgcolor="#dddddd">
 <tr>
  <td>"base", param=ABC</td>
  <td><c:url value="base"><c:param name="param" value="ABC"/></c:url></td>
 </tr>
 <tr>
  <td>"base", param=123</td>
  <td><c:url value="base"><c:param name="param" value="123"/></c:url></td>
 </tr>
 <tr>
  <td>"base", param=&</td>
  <td><c:url value="base"><c:param name="param" value="&"/></c:url></td>
 </tr>
 <tr>
  <td>"base", param="JSTL is fun"</td>
  <td><c:url value="base"><c:param name="param" value="JSTL is fun"/></c:url></td>
 </tr>
</table>
