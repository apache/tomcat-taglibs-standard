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
 *<p>The <tt>HtmlEncoder</tt> provides methods to encode output into html
 */
public class HtmlEncoder {

    private static final String[] htmlCode = new String[256];

    static {
                for (int i = 0; i < 10; i++) {
                        htmlCode[i] = "&#00" + i + ";";
                }

                for (int i = 10; i < 32; i++) {
                        htmlCode[i] = "&#0" + i + ";";
                }

                for (int i = 32; i < 128; i++) {
                        htmlCode[i] = String.valueOf((char)i);
                }

                // Special characters
                htmlCode['\n'] = "<BR>\n";
                htmlCode['\"'] = "&quot;"; // double quote
                htmlCode['&'] = "&amp;"; // ampersand
                htmlCode['<'] = "&lt;"; // lower than
                htmlCode['>'] = "&gt;"; // greater than

                for (int i = 128; i < 256; i++) {
                        htmlCode[i] = "&#" + i + ";";
                }
    }

    /**
     * <p>Encode the given text into html.</p>
     *
     * @param string the text to encode
     * @return the encoded string
     *
     */
    public static String encode(String string) {
                int n = string.length();
                char character;
                StringBuffer buffer = new StringBuffer();
        // loop over all the characters of the String.
                for (int i = 0; i < n; i++) {
                        character = string.charAt(i);
                        // the Htmlcode of these characters are added to a StringBuffer one by one
                        try {
                                buffer.append(htmlCode[character]);
                        }
                        catch(ArrayIndexOutOfBoundsException aioobe) {
                                buffer.append(character);
                        }
            }
        return buffer.toString();
    }

}

