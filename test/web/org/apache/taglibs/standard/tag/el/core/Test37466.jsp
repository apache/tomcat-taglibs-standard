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
