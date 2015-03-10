---------------------------------------------------------------------------
Apache Standard Tag Library 1.2.5 -- BINARY DISTRIBUTION
---------------------------------------------------------------------------
Thanks for downloading this release of the Standard tag library, 
an implementation of the JavaServer Pages(tm)(JSP) 
Standard Tag Library (JSTL).

This code is licensed to you by the Apache Software Foundation and its
contributors under the terms of the Apache License V2.0;
please see the included NOTICE and LICENSE files for details.

JSTL is an effort of the Java Community Process (JCP) and
comes out of the JSR-052 expert group. For more information on JSTL,
please go to http://jcp.org/en/jsr/detail?id=52 .

---------------------------------------------------------------------------
LIBRARY DEPENDENCIES

This version of the Standard Tag Library has the following runtime
dependencies:

   1. Dependencies provided by a JSP 2.1 container:
      - Java 1.5 or later
      - Servlet 2.5 or later
      - JSP 2.1 or later

   2. Additional dependencies
      - The XML tag library requires Apache Xalan 2.7.1 or later

---
Apache Xalan 2.7.1

To address performance issues with XSLT processing, this version relies on
implementation specific functionality from Apache Xalan. The following
libraries should be included in the classpath for your application:
   - xalan-2.7.1.jar
   - serializer-2.7.1.jar

---------------------------------------------------------------------------
ADD DEPENDENCIES TO A WEB APPLICATION

To use this distribution with your own web applications, add the following JAR
files to the '/WEB-INF/lib' directory of your application:
   - taglibs-standard-spec-1.2.5.jar
   - taglibs-standard-impl-1.2.5.jar
   - taglibs-standard-jstlel-1.2.5.jar
   - xalan-2.7.1.jar
   - serializer-2.7.1.jar

If you do not use JSTL 1.0 tags then the "taglibs-standard-jstlel" JAR may be
omitted. If you do not use the XML library, then the Apache Xalan dependencies
may also be omitted.

If you build you application with Maven, add the following dependencies to
your pom.xml file:

    <dependency>
      <groupId>org.apache.taglibs</groupId>
      <artifactId>taglibs-standard-spec</artifactId>
      <version>1.2.5</version>
    </dependency>
    <dependency>
      <groupId>org.apache.taglibs</groupId>
      <artifactId>taglibs-standard-impl</artifactId>
      <version>1.2.5</version>
    </dependency>

---------------------------------------------------------------------------
USING JSTL TAGS FROM A JSP

The JSTL tag library can be imported into your pages with the following directives:

  CORE LIBRARY
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  XML LIBRARY
    <%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

  FMT LIBRARY 
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  SQL LIBRARY
    <%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

  FUNCTIONS LIBRARY
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

---------------------------------------------------------------------------
COMPATIBILITY

The 1.2 version of the Standard Taglib has been tested using Tomcat 7.0.57
and should work in any compliant JSP 2.1 (or later) container.

In version 1.2.3 and later, the XML libraries enable FEATURE_SECURE_PROCESSING
when parsing and transforming. The system property

  org.apache.taglibs.standard.xml.accessExternalEntity

can be used to further restrict the protocols over which external entities can
be resolved. When a SecurityManager is enabled this will, by default, allow
access to no protocols. Permission must be granted to the taglibs-standard-impl
library to read this property.

  permission java.util.PropertyPermission "org.apache.taglibs.standard.xml.accessExternalEntity", "read";

---------------------------------------------------------------------------
COMMENTS AND QUESTIONS

Please join the taglibs-user@tomcat.apache.org mailing list if you have
general usage questions about Apache Taglibs.

Comments about the JSTL specification itself should be sent to
jsr-52-comments@jcp.org.

Enjoy!

