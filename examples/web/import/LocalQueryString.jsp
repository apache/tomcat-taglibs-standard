<%@ taglib prefix="c" uri="http://java.sun.com/jstl/ea/core" %>

<hr>
<h3> This is the output of the imported file </h3>

<c:expressionLanguage 
  evaluator="org.apache.taglibs.standard.lang.spel.Evaluator"> 

<table border="0">
  <tr>
    <td>Getting "name" query parameter:</td>
    <td>
      <div style="color: red">
        <c:expr value="${param:name}"/><br>
      </div>
    </td>
  </tr>
  <tr>
    <td>Getting "email" query parameter:</td>
    <td>
      <div style="color: red">
        <c:expr value="${param:email}"/>
      </div>
    </td>
  </tr>
</table>

</c:expressionLanguage>

<hr>
