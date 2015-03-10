---------------------------------------------------------------------------
Apache Standard Tag Library 1.2.5 -- SOURCE DISTRIBUTION
---------------------------------------------------------------------------
Thanks for downloading the source code for the Apache Software Foundation's
implementation of the JavaServer Pages(tm)(JSP) Standard Tag Library (JSTL)
specification. This code is licensed to you by the Apache Software
Foundation and its contributors under the terms of the Apache License V2.0;
please see the included NOTICE and LICENSE files for details.

---------------------------------------------------------------------------
BUILD ENVIRONMENT SETUP

For the 1.2 release, the project migrated to the Apache Maven build system.
Download and install version 3.0 or higher from http://maven.apache.org

The build requires a Java Development Kit Version 5 or higher.

---------------------------------------------------------------------------
BUILDING

From the 'standard' directory, the entire project can be built with the
normal Maven goals:

    $ mvn install   <-- builds all targets and installs in local repository
    $ mvn clean     <-- removes all build artifacts

A typical build will use the 'install' goal that compiles all classes, runs
all the unit tests, creates the target bundles, and installs them in the
local Maven repository.

All library dependencies will be downloaded from the central Maven
repositories. You should be online when building.

Information about the project can be found in the 'pom.xml' project
descriptor.

For information about performing a release at Apache, please refer to
"Publishing Maven Artifacts" at http://www.apache.org/dev/publishing-maven-artifacts.html
To rebuild the released artifacts locally from this source distribution
or from a SVN tag, run:
    $ mvn -Papache-release install

---------------------------------------------------------------------------
PROJECT MODULES

There are three primary sub-modules:

    spec            <-- contains Apache's implementation of the API classes
    impl            <-- contains the implementation of tags from the 1.1
                        namespace http://java.sun.com/jsp/jstl/*
    jstlel          <-- contains the implementation of tags from the 1.0
                        namespace http://java.sun.com/jstl/* and uses the
                        original JSTL 1.0 version of EL

In addition, the following modules provide supporting functionality
    build-tools     <-- build support such as checkstyle rules
    compat          <-- contains the implementation of tags from the 1.0
                        namespace but uses the JSP container's implementation
                        of EL (which will be 2.1 or later).
