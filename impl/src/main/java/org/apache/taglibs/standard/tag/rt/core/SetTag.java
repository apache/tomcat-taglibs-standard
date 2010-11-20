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

import org.apache.taglibs.standard.tag.common.core.SetSupport;

/**
 * JSTL 1.1 compatible version of &lt;set&gt; that accepts expression results for attribute values.
 *
 * @author Shawn Bayern
 */

public class SetTag extends SetSupport {
    private boolean valueSpecified;
    private Object value;
    private Object target;
    private String property;

    public SetTag() {
    }

    public void setValue(Object value) {
        this.value = value;
        this.valueSpecified = true;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public void release() {
        value = null;
        target = null;
        property = null;
        valueSpecified = false;
        super.release();
    }

    @Override
    protected boolean isValueSpecified() {
        return valueSpecified;
    }

    @Override
    protected Object evalValue() {
        return value;
    }

    @Override
    protected Object evalTarget() {
        return target;
    }

    @Override
    protected String evalProperty() {
        return property;
    }
}
