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

package org.apache.taglibs.standard.lang.jpath.encoding;

/**
 *<p>The <tt>OutputEncoder</tt> provides methods to encode output
 */
public class OutputEncoder {

    public static int HTML = 1;

    /**
     * <p>Encode the given text with html.</p>
     *
     * @param string the text to encode
     * @return the encoded string
     *
     */
    public static String encode(String string, int encodingMethod) {
        String encoded = null;
        if (encodingMethod == OutputEncoder.HTML) {
            encoded = HtmlEncoder.encode(string); 
        }
        return encoded;
    }

    public static int getEncodingMethod(String encodingMethodText) {
        int encodingMethod;
        if (encodingMethodText.equals("html")) {
            encodingMethod = OutputEncoder.HTML;
        } else {
            encodingMethod = OutputEncoder.HTML;
        }
        return encodingMethod;
    }

}

