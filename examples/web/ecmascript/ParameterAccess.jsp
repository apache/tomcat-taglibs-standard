<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<html>
<head>
  <title>JSTL: EcmaScript EL Support -- Simple Parameter Access Example</title>
</head>
<body bgcolor="#FFFFFF">
<h3>Parameter Access</h3>

<p>
  Request parameters can be accessed through a Map object called 
  &quot;params&quot;. See the <a href="MapAccess.jsp">Map Access</a> page for 
  information on how to conveniently access Map objects. As request parameters 
  can have multiple values, the map entries are actually arrays, so the index 
  operator needs to be used to access the actual string value of the parameter.
</p>

<hr>

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
  <hr>
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

</body>
</html>

