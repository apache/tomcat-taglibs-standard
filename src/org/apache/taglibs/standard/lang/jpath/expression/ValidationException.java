/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

/* +===================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 *
 */ 

package org.apache.taglibs.standard.lang.jpath.expression;

import org.apache.taglibs.standard.lang.jpath.adapter.*;
import javax.servlet.jsp.*;

/**
 * The ValidationException class
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version
 */
public class ValidationException extends Exception {

    protected boolean specialConstructor;
    public String image;
    protected SimpleNode node;

    /**
     * The end of line string for this machine.
     */
    protected String eol = System.getProperty("line.separator", "\n");

    /**
     * The following constructors are for use by you for whatever
     * purpose you can think of.  Constructing the exception in this
     * manner makes the exception behave in the normal way - i.e., as
     * documented in the class "Throwable".
     */
    public ValidationException() {

        super();

        specialConstructor = false;
    }

    /**
     * Used to create an instance of the ValidationException class
     *
     *
     * @param message
     *
     */
    public ValidationException(String message) {

        super(message);

        specialConstructor = false;
    }

    /**
     * Used to create an instance of the ValidationException class
     *
     *
     * @param currentNode
     * @param message
     *
     */
    public ValidationException(SimpleNode currentNode, String message) {

        super(message);

        specialConstructor = true;
        node = currentNode;
    }

    /**
     * This method has the standard behavior when this object has been
     * created using the standard constructors.  Otherwise, it uses
     * "currentToken" to generate an evaluation
     * error message and returns it.  If this object has been created
     * due to an evaluation error, and you do not catch it (it gets thrown
     * during the evaluation), then this method is called during the printing
     * of the final stack trace, and hence the correct error message
     * gets displayed.
     *
     * @return
     */
    public String getMessage() {

        if (!specialConstructor) {
            return super.getMessage();
        }

        String expected = "";
        int maxSize = 1;
        String retval = eol + "Encountered \"";

        retval += super.getMessage();
        retval += "\"" + eol;
        retval += "at : " + eol;
        retval += node.rootOriginalString() + eol;

        for (int i = 0; i < node.firstToken.beginColumn - 1; i++) {
            retval += " ";
        }

        for (int i = 0; i
                < node.lastToken.endColumn
                    - (node.firstToken.beginColumn - 1); i++) {
            retval += "^";
        }

        retval += eol;
        retval += "beginning at column " + node.firstToken.beginColumn + "."
                + eol;
        retval += "ending at column " + node.lastToken.endColumn + "." + eol;

        return retval;
    }

    /**
     * Used to convert raw characters to their escaped version
     * when these raw version cannot be used as part of an ASCII
     * string literal.
     *
     * @param str
     *
     * @return
     */
    protected String add_escapes(String str) {

        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {

            case 0 :
                continue;
            case '\b' :
                retval.append("\\b");

                continue;
            case '\t' :
                retval.append("\\t");

                continue;
            case '\n' :
                retval.append("\\n");

                continue;
            case '\f' :
                retval.append("\\f");

                continue;
            case '\r' :
                retval.append("\\r");

                continue;
            case '\"' :
                retval.append("\\\"");

                continue;
            case '\'' :
                retval.append("\\\'");

                continue;
            case '\\' :
                retval.append("\\\\");

                continue;
            default :
                if ((ch = str.charAt(i)) < 0x20 || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);

                    retval.append("\\u"
                            + s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }

                continue;
            }
        }

        return retval.toString();
    }
}
