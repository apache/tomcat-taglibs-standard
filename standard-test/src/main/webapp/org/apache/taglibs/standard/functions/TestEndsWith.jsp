<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="var1" value="${fm:endsWith('abcda','a')}" scope="application"/>

