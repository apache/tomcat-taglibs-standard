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
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:import url="/org/apache/taglibs/standard/tag/el/core/Test37466.xml" varReader="xmlSource">
<%
	java.io.StringReader o = (java.io.StringReader)pageContext.getAttribute("xmlSource");
	System.out.println("o: " + o);
	char[] buf = new char[1];
	while (o.read(buf) > 0)
	{
		System.out.print(buf);
	}
	System.out.println("");
	System.out.println("------");
	o.reset();
%>
	<x:parse xml="${xmlSource}" var="xmldoc" />
</c:import>
<%
	System.out.println("XX parsed ok");
%>

worked: ${xmldoc}
