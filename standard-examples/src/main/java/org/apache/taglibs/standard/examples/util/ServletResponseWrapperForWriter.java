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

package org.apache.taglibs.standard.examples.util;

import java.io.PrintWriter;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * ServletResponseWrapper used for the the generation of
 * semi-dynamic pages.
 * <p>This 'wrapped' response object is passed as the second argument
 * to the internal RequestDispatcher.include(). It channels
 * all output text into the PrintWriter specified in the
 * constructor (which is associated with the file where the
 * output of the JSP page has to be saved).
 *
 * @author Pierre Delisle
 */
public class ServletResponseWrapperForWriter
        extends HttpServletResponseWrapper {
    /**
     * The writer that will get all the output of the response.
     */
    PrintWriter writer;

    public ServletResponseWrapperForWriter(ServletResponse response,
                                           PrintWriter writer) {
        super((HttpServletResponse) response);
        this.writer = writer;
    }

    /**
     * Returns the Writer associated with the response.
     */
    public java.io.PrintWriter getWriter()
            throws java.io.IOException {
        return writer;
    }
}

