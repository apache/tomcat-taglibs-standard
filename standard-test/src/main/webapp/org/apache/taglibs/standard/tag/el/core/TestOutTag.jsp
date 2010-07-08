<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
Start
<c:out value="Hello World"/>
<c:out value="cats & dogs"/>
<c:out value="cats & dogs" escapeXml="false"/>
<c:out value="${5}"/>
<c:out value="${null}"/>
<c:out value="${null}" default="default"/>
<c:out value="${null}"><%-- this will be trimmed --%>
    Default from Body
</c:out>
<c:out value="${null}" escapeXml="true"><b>cats & dogs</b></c:out>
<c:out value="${null}" escapeXml="false"><b>cats & dogs</b></c:out>
Reader
<c:out value="${cats}"/>
<c:out value="${dogs}" escapeXml="false"/>
End
