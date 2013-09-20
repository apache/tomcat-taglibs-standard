<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
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
