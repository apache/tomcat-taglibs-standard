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

package org.apache.taglibs.standard.testutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Helper class for Cactus tests
 */
public class TestUtil {
    
    private TestUtil() {}

    /**
     * Gets the corresponding test jsp for this test case.  As a convention,
     * test class and test jsp should have the same package and directory
     * structure, and the same base name.
     * @return a context-relative path to the test jsp.
     */
     public static String getTestJsp(Object obj) {
         String className = obj.getClass().getName();
         String baseName = className.replace('.', '/');
         return "/" + baseName + ".jsp";
     }

    public static String loadResource(Object obj) throws IOException {
        Class clazz = obj.getClass();
        InputStream is = clazz.getResourceAsStream(clazz.getSimpleName() + ".txt");
        Reader reader = new InputStreamReader(is);
        try {
            char[] buffer = new char[1024];
            StringBuilder s = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) > 0) {
                s.append(buffer, 0, count);
            }
            return s.toString();        
        } finally {
            reader.close();
        }
    }
}
