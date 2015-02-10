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

package org.apache.taglibs.standard.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XmlUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void anyProtocolAllowedForAll() {
        XmlUtil.checkProtocol("all", "http://example.com/foo.xml");
    }

    @Test
    public void standardSchemesAllowed() {
        XmlUtil.checkProtocol("http,jar:file,file", "http://example.com/foo.xml");
        XmlUtil.checkProtocol("http,jar:file,file", "file:///tmp/file");
        XmlUtil.checkProtocol("http,jar:file,file", "jar:file:///tmp/file.jar!/entry.xml");
    }

    @Test
    public void notAllowedForEmptyString() {
        thrown.expect(SecurityException.class);
        XmlUtil.checkProtocol("", "http://example.com/foo.xml");
    }

    @Test
    public void notAllowed() {
        thrown.expect(SecurityException.class);
        XmlUtil.checkProtocol("http,file", "https://example.com/foo.xml");
    }
}
