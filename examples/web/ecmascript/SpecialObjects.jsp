<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Special Objects Example</title>
</head>
<body bgcolor="#FFFFFF">
<h1>Special Objects</h1>
<p>In the context of JSTL, some special predefined objects are made available.</p>

<br>
<hr>

<h3>Request Parameters (<code>params</code>)</h3>

<p>
  Request parameters can be accessed through a Map object called 
  &quot;params&quot;. See the <a href="MapAccess.jsp">Map Access</a> page for 
  information on how to conveniently access Map objects. As request parameters 
  can have multiple values, the map entries are actually arrays, so the index 
  operator needs to be used to access the actual string value of the parameter.
</p>
<p>
  Enter a value for the parameter &quot;test&quot;, which will be displayed 
  below using this technique.
</p>
<form>
  <table border="0" cellspacing="0" cellpadding="10">
    <tr> 
      <td> 
        <input type="text" size="40" name="test" value="">
      </td>
      <td>
        <input type="submit" name="Submit" value="Submit">
      </td>
    </tr>
  </table>
</form>
<c:if test="$params.test != null">
  <p>Here is the parameter value:</p>
  <table border="1">
    <tr>
      <th>Expression</th>
      <th>Output</th>
    </tr>
    <tr>
      <td><code>$params['test'][0]</code></td>
      <td><c:expr value="$params['test'][0]"/></td>
    </tr>
    <tr>
      <td><code>$params.test[0]</code></td>
      <td><c:expr value="$params.test[0]"/></td>
    </tr>
  </table>
  </p>
</c:if>

<br>
<hr>

<h3>Cookies (<code>cookies</code>)</h3>

<p>
  Cookies can be accessed through a Map object called &quot;cookies&quot;.
  See the <a href="MapAccess.jsp">Map Access</a> page for information on how 
  to conveniently access Map objects.
</p>

<p>Here are the cookies:</p>
<table border="1">
  <tr>
    <th><code>$cookie.name</code></th>
    <th><code>$cookie.value</code></th>
    <th><code>cookies[cookie.value.name].value</code></th>
  </tr>
  <c:forEach var="cookie" items="$cookies">
    <tr>
      <td><c:expr value="$cookie.value.name" default="oops"/></td>
      <td><c:expr value="$cookie.value.value" default="oops"/></td>
      <td><c:expr value="$cookies[cookie.value.name].value" default="oops"/></td>
    </tr>
  </c:forEach>
</table>

<br>
<hr>

<h3>Request Headers (<code>headers</code>)</h3>

<p>
  Request headers can be accessed through a Map object called 
  &quot;headers&quot;. See the <a href="MapAccess.jsp">Map Access</a> page for 
  information on how to conveniently access Map objects.
</p>

<p>Accessing specific headers:</p>
<table border="1">
  <tr>
    <th>Expression</th>
    <th>Result</th>
  </tr>
  <tr>
    <td><code>$headers.host</code></td>
    <td><c:expr value="$headers.host" default="(No Host)"/></td>
  </tr>
  <tr>
    <td><code>$headers['user-agent']</code></td>
    <td><c:expr value="$headers['user-agent']" default="(No User Agent)"/></td>
  </tr>
</table>

<p>Iteration over the headers:</p>
<table border="1">
  <tr>
    <th><code>$header.key</code></th>
    <th><code>$header.value</code></th>
  </tr>
  <c:forEach var="header" items="$headers">
    <tr>
      <td><c:expr value="$header.key" default="oops"/></td>
      <td><c:expr value="$header.value" default="oops"/></td>
    </tr>
  </c:forEach>
</table>

<br>
<hr>

<h3>Webapp Initialization Parameters (<code>initParams</code>)</h3>

<p>
  Init parameters can be accessed through a Map object called 
  &quot;initParams&quot;. See the <a href="MapAccess.jsp">Map Access</a> page 
  for information on how to conveniently access Map objects.
</p>

<p>Iteration over the init parameters:</p>
<table border="1">
  <tr>
    <th><code>$initParam.key</code></th>
    <th><code>$initParam.value</code></th>
  </tr>
  <c:forEach var="initParam" items="$initParams">
    <tr>
      <td><c:expr value="$initParam.key" default="oops"/></td>
      <td><c:expr value="$initParam.value" default="oops"/></td>
    </tr>
  </c:forEach>
</table>

<br>
<hr>

</body>
</html>
