<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<x:parse xml="<content/>" var="doc" scope="request" />

<x:forEach select="$doc//*">
    foo
</x:forEach>
