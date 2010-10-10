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
package org.apache.taglibs.standard.resources;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ResourcesTest {

    @Test
    public void testMessageNoArguments() {
        assertEquals("test no arguments", Resources.getMessage("TEST_NO_ARGUMENTS"));
    }

    @Test
    public void testMessageOneArgument() {
        assertEquals("test argument one", Resources.getMessage("TEST_ONE_ARGUMENT", "one"));
    }

    @Test
    public void testMessageMultipleArguments() {
        assertEquals("test argument one 2", Resources.getMessage("TEST_MULTIPLE_ARGUMENT", "one", 2));
    }

}
