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

package javax.servlet.jsp.jstl.tlv;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.ValidationMessage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PermittedTaglibsTLVTest {

    private static final String URI = "http://jakarta.apache.org/taglibs/standard/permittedTaglibs";

    private PermittedTaglibsTLV tlv;
    private PageData page;
    private Map<String, Object> initParams = new HashMap<String, Object>();

    @Before
    public void createTlv() {
        tlv = new PermittedTaglibsTLV();
        page = EasyMock.createMock(PageData.class);
    }

    @Test
    public void tagPermittedWhenDeclaredAtRoot() throws UnsupportedEncodingException {
        initParams.put("permittedTaglibs", "urn:test");
        String xmlView = "<jsp:root xmlns:jsp='http://java.sun.com/JSP/Page' xmlns:x='urn:test' xmlns:p='http://jakarta.apache.org/taglibs/standard/permittedTaglibs'></jsp:root>";
        expect(page.getInputStream()).andStubReturn(new ByteArrayInputStream(xmlView.getBytes("UTF-8")));

        replay(page);
        tlv.setInitParameters(initParams);
        ValidationMessage[] messages = tlv.validate(null, URI, page);
        assertTrue(messages == null || messages.length == 0);
    }

    @Test
    public void tagNotPermittedWhenDeclaredAtRoot() throws UnsupportedEncodingException {
        initParams.put("permittedTaglibs", "urn:none");
        String xmlView = "<jsp:root xmlns:jsp='http://java.sun.com/JSP/Page' xmlns:x='urn:test' xmlns:p='http://jakarta.apache.org/taglibs/standard/permittedTaglibs'></jsp:root>";
        expect(page.getInputStream()).andStubReturn(new ByteArrayInputStream(xmlView.getBytes("UTF-8")));

        replay(page);
        tlv.setInitParameters(initParams);
        ValidationMessage[] messages = tlv.validate(null, URI, page);
        assertNotNull(messages);
        assertEquals(1, messages.length);
    }

    @Ignore("https://issues.apache.org/bugzilla/show_bug.cgi?id=57290")
    @Test
    public void tagNotPermittedWhenDeclaredInPage() throws UnsupportedEncodingException {
        initParams.put("permittedTaglibs", "urn:none");
        // In the page for this XML view, 'd' and 'x' are taglibs but 'o' is not
        String xmlView = "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                "<jsp:root version='2.0' xmlns:jsp='http://java.sun.com/JSP/Page' jsp:id='0'>\n" +
                "<jsp:directive.page jsp:id='1' pageEncoding='UTF-8' contentType='text/xml;charset=UTF-8'/>\n" +
                "<o:doc xmlns:d='urn:dump' xmlns:o='urn:out' jsp:id='2'>\n" +
                "  <x:hello xmlns:x='urn:jsptagdir:/WEB-INF/tags/test' jsp:id='3'/>\n" +
                "</o:doc>\n" +
                "</jsp:root>";
        expect(page.getInputStream()).andStubReturn(new ByteArrayInputStream(xmlView.getBytes("UTF-8")));

        replay(page);
        tlv.setInitParameters(initParams);
        ValidationMessage[] messages = tlv.validate(null, URI, page);
        assertNotNull(messages);
        assertEquals(1, messages.length);
    }


}
