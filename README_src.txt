---------------------------------------------------------------------------
Standard Tag Library 1.1 -- SOURCE DISTRIBUTION
---------------------------------------------------------------------------
Thanks for downloading the source code of the Standard tag library, 
an implementation of the JavaServer Pages(tm)(JSP) 
Standard Tag Library (JSTL).

JSTL is an effort of the Java Community Process (JCP) and
comes out of the JSR-052 expert group. For more information on JSTL,
please go to http://java.sun.com/products/jstl.

---------------------------------------------------------------------------
BUILD ENVIRONMENT SETUP

---
J2SE

Download and install version 1.4.2 or higher of J2SE (Java 2 Standard Edition)
for your operating system platform. J2SE can be found at 
http://java.sun.com/j2se.

J2SE 1.4.2 (and higher) includes many of the libraries that standard 1.1 depends
on. However, please note that standard 1.1 will compile and run on previous
versions of J2SE (from 1.3 up to 1.4.1) as long as the standard 1.1 dependencies
not included in these versions of the J2SE are properly setup.
See section 'LIBRARY DEPENDENCIES' for details.

  - Set a JAVA_HOME environment variable to point at the directory 
    where J2SE is installed.
  - Add the JAVA_HOME/bin directory to your PATH.

---
Ant

Download and install version 1.5 or higher of the Jakarta Ant Project
distribution. Ant can be fount at http://ant.apache.org.

  - Set the ANT_HOME environment variable to point at your Ant 
    distribution directory 
  - Add the ANT_HOME/bin directory to your PATH.

---------------------------------------------------------------------------
LIBRARY DEPENDENCIES

This version of the Standard Tag Library has the following compile-time
dependencies:

   1. Dependencies not included in J2SE:
      - Servlet 2.4
      - JSP 2.0

   2. Dependencies included in newer J2SEs (1.4.2 and higher)
      - JAXP 1.2 
      - Xalan 2.5 
      - JDBC Standard Extension 2.0

Since all of the dependencies in (2) are included in J2SE 1.4.2 and higher, 
this is therefore the J2SE version of choice to compile and run the 
standard tag library.

---
build.properties

- Copy the file standard/build_sample_standard.properties to build.properties.

- Edit build.properties and make the following modifications:
    - Set the "base.dir" property in build.properties to the base directory
      of your 'standard' distribution. It must be an absolute path.
    - Set the jar file properties to the absolute path and filename 
      for the jar files required to build the standard tag library
      (see below).

---
Servlet 2.4 and JSP 2.0

Download and install the Servlet 2.4 and JSP 2.0 APIs.
The jar files for these APIs may be found in a distribution
of Tomcat 5 available at http://jakarta.apache.org/tomcat.

Set the following properties in build.properties to the
file paths of the jars:
  - servlet24.jar
  - jsp20.jar

---
JAXP 1.2
[required only if building with J2SE 1.3]

The JAXP 1.2 jar files can be obtained in the Java Web Services
Developer Pack (JWSDP) available at 
http://java.sun.com/products/jwsdp.

Set the following properties in build.properties to the
file paths of the jars:
  - jaxp-api.jar
  - dom.jar
  - sax.jar
  - xercesImpl.jar

---
Xalan 2.5
[required only if building with J2SE 1.3 up to J2SE 1.4.1]

The Xalan jar file can be obtained in the Java Web Services
Developer Pack (JWSDP) available at 
http://java.sun.com/products/jwsdp, as well as from 
Apache at http://xml.apache.org/xalan-j.

Set the "xalan.jar" property in build.properties to the
jar file of Xalan.

If using jdk 1.3, put xalan.jar in the lib directory
of ant so XSLT transformations of documentation can be 
properly done.

---
JDBC Standard Extension 2.0
[required only if building with J2SE 1.3]

The JDBC 2.0 Optional Package can be obtained from:
http://java.sun.com/products/jdbc/download.html

Set the "jdbc2_0-stdext.jar" property in build.properties to the
JDBC 2.0 Standard Extensions jar file path.

---------------------------------------------------------------------------
Building the Standard tag library

To build the distribution set your current directory to the 'standard' 
directory into which you unpacked the distribution.

Build 'standard' by executing ant in a shell. Some common build targets
include:

       > ant         <-- builds the intermediate form of the library,
                         documentation, and example targets
       > ant dist    <-- builds all the distribution targets
       > ant clean   <-- deletes intermediate results so that target can
                         be rebuilt from scratch.

Two directory hierarchies are created to contain the results of the
build:

{base.dir}/
    build/           <-- Contains intermediate form results of
                         building standard custom library
    dist/            <-- Contains the files that will be included
                         in the binary distribution of the
                         standard project

The following directory and files are created when doing a build:

   * build/standard - Location of all directories and files built for the 
     standard taglib.
   * build/standard/standard - Results of the build process
     (classes, jar files, tlds)
   * build/standard/standard-doc/ - Files used to create the
     standard-doc.war file
   * build/standard/standard-examples/ - Files used to create the 
     standard-examples.war file.

The following directory and files are created when doing a distribution
build:

   * dist/standard/ - Location of all files built for a binary
     distribution of the taglib.
   * dist/standard/README - Information to use the binary distribution
     of the standard tablib.
   * dist/standard/javadoc/ - The javadocs
   * dist/standard/lib/ - The standard jar files: jstl.jar and
     standard.jar
   * dist/standard/tld/ - Directory with the Tag Lib Descriptors for 
     the tag library.
   * dist/standard/standard-doc.war - Tag Library documentation
     war file.
   * dist/standard/standard-examples.war - Tag Library examples
     war file.
   * dist/standard/tld - Directory with the Tag Lib Descriptors for 
     the tag library.

---------------------------------------------------------------------------
USING THE STANDARD TAG LIBRARY

See the README file of the binary distribution you have built with these
instructions.

---------------------------------------------------------------------------