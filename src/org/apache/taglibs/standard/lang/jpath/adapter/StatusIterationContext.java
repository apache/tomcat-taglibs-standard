/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.taglibs.standard.lang.jpath.adapter;

import javax.servlet.jsp.jstl.core.LoopTagStatus;

public class StatusIterationContext implements IterationContext {
    private LoopTagStatus status;
    public StatusIterationContext(LoopTagStatus status) {
        this.status = status;
    }
    public Object getCurrent() {
        return status.getCurrent();
    }

    public int getPosition() {
        return status.getCount();
    }

    public int getLast() {
        throw new UnsupportedOperationException("The status object does not "
                 + "support finding the size of a list");
    }
}
