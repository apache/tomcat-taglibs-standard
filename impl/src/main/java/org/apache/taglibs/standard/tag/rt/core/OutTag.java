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

package org.apache.taglibs.standard.tag.rt.core;

import org.apache.taglibs.standard.tag.common.core.OutSupport;

/**
 * <p>Tag handler for &lt;out&gt; in JSTL's rtexprvalue library.</p>
 *
 * @author Shawn Bayern
 */

public class OutTag extends OutSupport {

    private Object value;
    private String def;
    private boolean escapeXml = true;

    //*********************************************************************
    // Accessors

    @Override
    public void release() {
        value = null;
        def = null;
        escapeXml = false;
        super.release();
    }

    // for tag attribute

    public void setValue(Object value) {
        this.value = value;
    }

    // for tag attribute

    public void setDefault(String def) {
        this.def = def;
    }

    // for tag attribute

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @Override
    protected Object evalValue() {
        return value;
    }

    @Override
    protected String evalDefault() {
        return def;
    }

    @Override
    protected boolean evalEscapeXml() {
        return escapeXml;
    }
}
