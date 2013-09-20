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
