<?xml version="1.0" encoding="ISO-8859-1"?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<x:parse var="sample">
<sample>
  <link href="/org/apache/taglibs/standard/tag/el/xml/Test30068.xml"/>
</sample>
</x:parse>

<x:forEach select="$sample/sample/link">
  <c:set var="link">
    <x:out select="@href"/>
  </c:set>

  <c:import url="${link}" varReader="r2">
    <x:parse var="sample2" doc="${r2}"/>
  </c:import>

  <!-- *** this does not work *** -->
  <c:set var="correct" scope="application"><x:out select="$sample2/sample/text"/></c:set>

  <!-- this does work, but is not correct -->
  <c:set var="incorrect" scope="application"><x:out select="$sample2/text"/></c:set>

</x:forEach>

<!-- for manual testing -->
Correct: ${correct}<br/>
Incorrect: ${incorrect}
