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

package org.apache.taglibs.standard.resources;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Provides locale-neutral access to string resources.  Only the
 * documentation and code are in English. :-)
 *
 * <p>The major goal, aside from globalization, is convenience.
 * Access to resources with no parameters is made in the form:</p>
 * <pre>
 *     Resources.getMessage(MESSAGE_NAME);
 * </pre>
 *
 * <p>Access to resources with one parameter works like</p>
 * <pre>
 *     Resources.getMessage(MESSAGE_NAME, arg1);
 * </pre>
 *
 * <p>... and so on.</p>
 *
 * @author Shawn Bayern
 */
public class Resources {

    /**
     * Our class-wide ResourceBundle.
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle(Resources.class.getName());


    //*********************************************************************
    // Public static methods

    /**
     * Retrieves a message with no arguments.
     *
     * @param name the name of the message
     * @return the localized message text
     * @throws MissingResourceException if the message does not exist
     */
    public static String getMessage(String name) throws MissingResourceException {
        return rb.getString(name);
    }

    /**
     * Retrieves a message with arbitrarily many arguments.
     *
     * @param name the name of the message
     * @param a    arguments to be substituted into the message text
     * @return the localized message text
     * @throws MissingResourceException if the message does not exist
     */
    public static String getMessage(String name, Object... a) throws MissingResourceException {
        String res = rb.getString(name);
        return MessageFormat.format(res, a);
    }
}
