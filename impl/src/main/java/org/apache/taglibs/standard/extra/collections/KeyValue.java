/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.taglibs.standard.extra.commons.collections;

/**
 * Defines a simple key value pair.
 * <p>
 * A Map Entry has considerable additional semantics over and above a simple
 * key-value pair. This interface defines the minimum key value, with just the
 * two get methods.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 218351 $ $Date: 2004-10-20 17:58:23 -0700 (Wed, 20 Oct 2004) $
 * 
 * @author Stephen Colebourne
 */
public interface KeyValue {

    /**
     * Gets the key from the pair.
     *
     * @return the key 
     */
    Object getKey();

    /**
     * Gets the value from the pair.
     *
     * @return the value
     */
    Object getValue();

}
