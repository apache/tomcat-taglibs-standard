/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

package org.apache.taglibs.standard.lang.javascript.adapter;

import java.util.List;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/**
 * Wrapper class than enables the JavascriptExpressionEvaluator to handle List
 * objects like native arrays.
 * 
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * @version $Revision$
 */
public class NativeJavaList
    extends NativeJavaObject {
    
    // ---------------------------------------------------- Instance Variables
    
    /**
     * The actual List object we wrap around.
     */
    private List list = null;
    
    // ---------------------------------------------------------- Constructors
    
    /**
     * Constructor.
     */
    public NativeJavaList(Scriptable scope,
                          List list) {
        super(scope, list, List.class);
        this.list = list;
    }
    
    // ---------------------------------------Scriptable Object Implementation
    
    /**
     * Implemented to provide a JavaScript class name.
     * 
     * @see org.mozilla.javascript.Scriptable#getClassName()
     */
    public String getClassName() {
        return "JavaList";
    }
    
    /**
     * First tries to retrieve an object from the list using the given index. 
     * If no such element is found, delegates to the super class.
     * 
     * @see org.mozilla.javascript.Scriptable#get(int,Scriptable)
     */
    public Object get(int index, Scriptable start) {
        Object o = list.get(index);
        // Should we check IndexOutOfBounds here ?
        if (o == null) {
            o = new Undefined();
        }
        return o;
    }
    
}
