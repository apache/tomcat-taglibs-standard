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

package org.apache.taglibs.standard.lang.spel;

import java.util.*;

/**
 * <p>An indexer that stores and applies array-like indexes:
 *    <tt>[a][b][c]...</tt></p>
 * 
 * @author Shawn Bayern
 */
class LiteralIndexes implements Indexer {

    //*********************************************************************
    // Private data

    /** The actual indexes we store. */
    private int[] indexes;


    //*********************************************************************
    // Constructor

    /** Constructs a new Indexer representing literal indexes. */
    public LiteralIndexes(List l) {
	indexes = new int[l.size()];
	for (int i = 0; i < indexes.length; i++) {
	    Object item = l.get(i);
	    if (!(item instanceof Integer))
		throw new IllegalArgumentException();
	    indexes[i] = ((Integer) item).intValue();
	}
    }

    /** Constructs a new Indexer representing literal indexes. */
    public LiteralIndexes(int[] i) {
	indexes = i;
    }

    //*********************************************************************
    // Implementation of 'Indexer' contract

    /**
     * Applies this instance's indexes to the given base object.
     * For example, if the base object is 'a', the first index is 0,
     * and the second index is 1, then we return a[0][1], throwing
     * an IllegalArgumentException if 'a' does not support the
     * requisite indexing.
     */
    public Object index(Object a) {
	for (int i = 0; i < indexes.length; i++) {
	    // For the moment, we just support simple arrays.
	    // If the array is of primitives, "promote" referents to boxed 
            // types.  (Could do this with introspection, but there's something
	    // comfortingly concrete about explicit enumeration.)
	    if (!a.getClass().isArray())
		throw new IllegalArgumentException(Constants.CANT_INDEX);
	    else if (a instanceof Object[]) {
		a = ((Object[]) a)[indexes[i]];
	    } else if (a instanceof boolean[]) {
		a = new Boolean(((boolean[]) a)[indexes[i]]);
	    } else if (a instanceof char[]) {
		a = new Character(((char[]) a)[indexes[i]]);
	    } else if (a instanceof byte[]) {
		a = new Byte(((byte[]) a)[indexes[i]]);
	    } else if (a instanceof short[]) {
		a = new Short(((short[]) a)[indexes[i]]);
	    } else if (a instanceof int[]) {
		a = new Integer(((int[]) a)[indexes[i]]);
	    } else if (a instanceof long[]) {
		a = new Long(((long[]) a)[indexes[i]]);
	    } else if (a instanceof float[]) {
		a = new Float(((float[]) a)[indexes[i]]);
	    } else if (a instanceof double[]) {
		a = new Double(((double[]) a)[indexes[i]]);
	    }
	}
	return a;
    }
}
