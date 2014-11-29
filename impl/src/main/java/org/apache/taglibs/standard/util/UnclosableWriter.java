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

import java.io.IOException;
import java.io.Writer;

/**
 * A Writer based on a wrapped Writer but ignoring requests to
 * close() and flush() it.  (Someone must have wrapped the
 * toilet in my office similarly...)
 */
public class UnclosableWriter extends Writer {
    // TODO: shouldn't we be delegating all methods?
    private Writer w;

    public UnclosableWriter(Writer w) {
        this.w = w;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        w.write(cbuf, off, len);
    }
}
