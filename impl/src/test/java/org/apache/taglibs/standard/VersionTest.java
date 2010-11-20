/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.taglibs.standard;

import junit.framework.TestCase;

public class VersionTest
        extends TestCase {
    private Version version = null;

    public VersionTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        version = new Version();
    }

    @Override
    protected void tearDown() throws Exception {
        version = null;
        super.tearDown();
    }

    public void testVersion() {
        version = new Version();
    }

    public void testGetDevelopmentVersionNum() {
        int expectedReturn = 0;
        int actualReturn = version.getDevelopmentVersionNum();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetMaintenanceVersionNum() {
        int expectedReturn = 0;
        int actualReturn = version.getMaintenanceVersionNum();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetMajorVersionNum() {
        int expectedReturn = 1;
        int actualReturn = version.getMajorVersionNum();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetProduct() {
        String expectedReturn = "standard-taglib";
        String actualReturn = version.getProduct();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetReleaseVersionNum() {
        int expectedReturn = 2;
        int actualReturn = version.getReleaseVersionNum();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetVersion() {
        String expectedReturn = "standard-taglib 1.2.0";
        String actualReturn = version.getVersion();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testMain() {
        String[] argv = null;
        version.main(argv);
    }

}
