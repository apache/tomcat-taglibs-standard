<html><!-- #BeginTemplate "/examples/web/Templates/ExamplesTemplate.dwt" -->
<head>
<!-- #BeginEditable "doctitle" --> 
<title>JSTL: Import Tags Examples</title>
<!-- #EndEditable -->
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../global.css" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<table width="100%" border="0" cellpadding="5">
  <tr>
    <td height="0"><font size="-1" color="#000099"><b>JSTL Early Access</b></font></td>
    <td align="center" height="0"><font size="-1" color="#000099"> <b>Beware &#151; 
      API and Tags may/will change</b></font></td>
    <td align="right" height="0"><font size="-1" color="#003399"><a href="mailto:taglibs-user@jakarta.apache.org"><b>support</b></a> 
      &nbsp;&nbsp;<b><a href="mailto:taglibs-dev@jakarta.apache.org">development</a>&nbsp;&nbsp; 
      <a href="mailto:jsr052-comments@sun.com">comments to JSR052 EG</a></b></font></td>
  </tr>
  <tr> 
    <td colspan="3" bgcolor="#CCCCFF">JSTL Examples &nbsp;&nbsp;&nbsp;&nbsp;<a href="../index.html">Introduction</a> 
      &#149; <a href="../elsupport/index.html">EL Support </a> &#149; <a href="../conditionals/index.html">Conditionals</a> 
      &#149; <a href="../iterators/index.html">Iterators</a> &#149; <a href="index.jsp">Import</a> 
      &#149; <a href="../format/index.html">I18N & Formatting</a> &#149; <a href="../xml/index.html">XML</a> 
      &#149; <a href="../sql/index.jsp">SQL</a> &#149; <a href="../misc/index.html">Misc.</a></td>
  </tr>
</table>
<!-- #BeginEditable "body" --> 
<%@ include file="links.html" %>

<h2>Context Relative Examples</h2>
<p>For the context relative examples you will need to supply an available relative context name and url before executing the tags.</p>

<form name="myform" action="session.jsp" method="get" >
  <table width="90%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td>Context</td>
      <td>
        <input type="text" size="40" name="contextName" value="/examples">
      </td>
    </tr>
    <tr>
      <td>Url</td>
      <td>
        <input type="text" size="40" name="contextUrl" value="/jsp/simpletag/foo.jsp">
      </td>
    </tr>
    <tr>
      <td>Param name:</td>
      <td><input type="text" size="40" name="paramName1"></td>
      <td>Param value:</td>
      <td><input type="text" size="40" name="paramValue1"></td>
    </tr>
    <tr>
      <td>Param name:</td>
      <td><input type="text" size="40" name="paramName2"></td>
      <td>Param value:</td>
      <td><input type="text" size="40" name="paramValue2"></td>
    </tr>
    <tr>
      <td>Param name:</td>
      <td><input type="text" size="40" name="paramName3"></td>
      <td>Param value:</td>
      <td><input type="text" size="40" name="paramValue3"></td>
    </tr>
  </table>
  <p>
    <input type="submit" name="Submit" value="Submit">
  </p>
</form>

<!-- #EndEditable -->
<hr noshade color="#000099">
<table width="100%" border="0" cellpadding="5">
  <tr> 
    <td height="24"><font size="-1" color="#000099"><b>JSTL Early Access</b></font></td>
    <td align="center" height="24"><font size="-1" color="#000099"> <b>Beware 
      &#151; API and Tags may/will change</b></font></td>
    <td align="right" height="24"><font size="-1" color="#003399"><a href="mailto:taglibs-user@jakarta.apache.org"><b>support</b></a> 
      <b>&nbsp;&nbsp; <a href="mailto:taglibs-dev@jakarta.apache.org">development</a> 
      &nbsp;&nbsp;<a href="mailto:jsr052-comments@sun.com">comments to JSR052 
      EG</a></b></font></td>
  </tr>
</table>
</body>
<!-- #EndTemplate --></html>
