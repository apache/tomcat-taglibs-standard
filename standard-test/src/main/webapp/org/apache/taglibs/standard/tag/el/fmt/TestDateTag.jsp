<!-- Use this via the web browser to show that things are fine             -->
<!-- Unfortunately with Cactus the server isn't getting the correct locale -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="date" class="java.util.Date" />

<fmt:formatDate value="${date}" var="varDate" scope="application" pattern="yyyy-MM-dd"/>
<c:out value="${varDate}"/>

<fmt:formatDate value="${date}" var="varTime" scope="application" pattern="HH:mm:ss" type="time"/>
<c:out value="${varTime}"/>

<fmt:formatDate value="${date}" dateStyle="short" var="varDate2" scope="application"/>
<c:out value="${varDate2}"/>

<fmt:formatDate value="${date}" timeStyle="short" var="varTime2" scope="application" type="time"/>
<c:out value="${varTime2}"/>
