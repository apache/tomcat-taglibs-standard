<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <title>JSTL: Functions</title>
</head>
<body bgcolor="#FFFFFF">


<c:set var="s1" value="There is a castle on a cloud"/>

<h4>fn:contains</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:contains(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>CASTLE</td>
    <td>${fn:contains(s1, "CASTLE")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:contains(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:contains(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>${fn:contains(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>${fn:contains(undefined, "")}</td>
  </tr>
</table>

<h4>fn:containsIgnoreCase</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:containsIgnoreCase(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>CASTLE</td>
    <td>${fn:containsIgnoreCase(s1, "CASTLE")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>CaStLe</td>
    <td>${fn:containsIgnoreCase(s1, "CaStLe")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:containsIgnoreCase(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:containsIgnoreCase(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>${fn:containsIgnoreCase(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>${fn:containsIgnoreCase(undefined, "")}</td>
  </tr>
</table>

<h4>fn:startsWith</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:startsWith(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>There is</td>
    <td>${fn:startsWith(s1, "There is")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:startsWith(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:startsWith(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>${fn:startsWith(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>${fn:startsWith(undefined, "")}</td>
  </tr>
</table>

<h4>fn:endsWith</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:endsWith(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>cloud</td>
    <td>${fn:endsWith(s1, "cloud")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:endsWith(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:endsWith(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>${fn:endsWith(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>${fn:endsWith(undefined, "")}</td>
  </tr>
</table>

<h4>fn:indexOf</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:indexOf(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>cloud</td>
    <td>${fn:indexOf(s1, "cloud")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:indexOf(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:indexOf(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>${fn:indexOf(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>${fn:indexOf(undefined, "")}</td>
  </tr>
</table>

<h4>fn:substring</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>beginIndex</th>
    <th>endIndex</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>11</td>
    <td>17</td>
    <td>${fn:substring(s1, 11, 17)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>23</td>
    <td>-1</td>
    <td>${fn:substring(s1, 23, -1)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>-1</td>
    <td>-1</td>
    <td>${fn:substring(s1, -1, -1)}</td>
  </tr>
</table>

<h4>fn:substringAfter</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>There</td>
    <td>${fn:substringAfter(s1, "There")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>on a</td>
    <td>${fn:substringAfter(s1, "on a")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>${fn:substringAfter(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>${fn:substringAfter(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>&nbsp;${fn:substringAfter(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>&nbsp;${fn:substringAfter(undefined, "")}</td>
  </tr>
</table>

<h4>fn:substringBefore</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>substring</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>on a</td>
    <td>${fn:substringBefore(s1, "on a")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>castle</td>
    <td>${fn:substringBefore(s1, "castle")}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>null</td>
    <td>&nbsp;${fn:substringBefore(s1, undefined)}</td>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>empty</td>
    <td>&nbsp;${fn:substringBefore(s1, "")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>castle</td>
    <td>&nbsp;${fn:substringBefore(undefined, "castle")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>empty</td>
    <td>&nbsp;${fn:substringBefore(undefined, "")}</td>
  </tr>
</table>

<h4>fn:escapeXml</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>${fn:escapeXml(s1)}</td>
  </tr>
  <tr>
    <td><foo>body of foo</foo></td>
    <td>${fn:escapeXml("<foo>body of foo</foo>")}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>&nbsp;${fn:escapeXml(undefined)}</td>
  </tr>
  <tr>
    <td>empty</td>
    <td>&nbsp;${fn:escapeXml("")}</td>
  </tr>
</table>

<h4>fn:trim</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>${fn:trim(s1)}</td>
  </tr>
  <tr>
    <td><pre>    3 spaces before and after   </pre></td>
    <td><pre>${fn:trim("    3 spaces before and after   ")}</pre></td>
  </tr>
  <tr>
    <td>null</td>
    <td>&nbsp;${fn:trim(undefined)}</td>
  </tr>
  <tr>
    <td>empty</td>
    <td>&nbsp;${fn:trim("")}</td>
  </tr>
</table>

<h4>fn:length</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>${fn:length(s1)}</td>
  </tr>
  <tr>
    <td>${customers}</td>
    <td>${fn:length(customers)}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>${fn:length(undefined)}</td>
  </tr>
  <tr>
    <td>empty</td>
    <td>${fn:length("")}</td>
  </tr>
</table>

<h4>fn:toLowerCase</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>${fn:toLowerCase(s1)}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>&nbsp;${fn:toLowerCase(undefined)}</td>
  </tr>
  <tr>
    <td>empty</td>
    <td>&nbsp;${fn:toLowerCase("")}</td>
  </tr>
</table>

<h4>fn:toUpperCase</h4>
<table cellpadding="5" border="1">
  <tr>
    <th align="left">Input String</th>
    <th>Result</th>
  </tr>
  <tr>
    <td>${s1}</td>
    <td>${fn:toUpperCase(s1)}</td>
  </tr>
  <tr>
    <td>null</td>
    <td>&nbsp;${fn:toUpperCase(undefined)}</td>
  </tr>
  <tr>
    <td>empty</td>
    <td>&nbsp;${fn:toUpperCase("")}</td>
  </tr>
</table>

</body>
</html>
