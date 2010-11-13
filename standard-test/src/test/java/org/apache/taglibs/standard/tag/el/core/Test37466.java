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

package org.apache.taglibs.standard.tag.el.core;

import java.net.*;
import javax.servlet.http.*;
import org.apache.cactus.*;
import org.apache.taglibs.standard.testutil.TestUtil;

public class Test37466 extends JspTestCase {

    public Test37466(String name) {
	super(name);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void test37466() throws Exception {
	String serverName = pageContext.getRequest().getServerName();
	int serverPort = pageContext.getRequest().getServerPort();
	String contextPath = ( (HttpServletRequest) pageContext.getRequest() ).getContextPath();
	String jspPath = TestUtil.getTestJsp(this);
	String testPath = "http://" + serverName + ":" + serverPort + contextPath + jspPath;;

	URL url = new URL(testPath);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setRequestMethod("HEAD");
	
	String responseCode = Integer.toString(connection.getResponseCode());
	assertEquals(testPath + "The response code should be 200", "200", responseCode);
    }
}
