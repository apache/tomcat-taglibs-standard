<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<hr>
<h3> This is the output of the imported file </h3>

<c:outessionLanguage 
  evaluator="org.apache.taglibs.standard.lang.spel.Evaluator"> 

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

</c:outessionLanguage>

<hr>
