<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<hr>
<h3> This is the output of the imported file </h3>

<table border="0">
  <tr>
    <td>Getting "name" query parameter:</td>
    <td>
      <div style="color: red">
        <c:out value="${param:name}"/><br>
      </div>
    </td>
  </tr>
  <tr>
    <td>Getting "email" query parameter:</td>
    <td>
      <div style="color: red">
        <c:out value="${param:email}"/>
      </div>
    </td>
  </tr>
</table>

<hr>
