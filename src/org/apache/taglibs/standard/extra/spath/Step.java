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

package standard.src.org.apache.taglibs.standard.extra.spath;

import java.util.List;

/**
 * <p>Represents a 'step' in an SPath expression.</p>
 *
 * @author Shawn Bayern
 */
public class Step {

    private boolean depthUnlimited;
    private String name;
    private List predicates;

    // record a few things for for efficiency...
    private String uri, localPart;

    /**
     * Constructs a new Step object, given a name and a (possibly null)
     * list of predicates.  A boolean is also passed, indicating
     * whether this particular Step is relative to the 'descendent-or-self'
     * axis of the node courrently under consideration.  If true, it is;
     * if false, then this Step is rooted as a direct child of the node
     * under consideration.
     */
    public Step(boolean depthUnlimited, String name, List predicates) {
	if (name == null)
	    throw new IllegalArgumentException("non-null name required");
	this.depthUnlimited = depthUnlimited;
	this.name = name;
	this.predicates = predicates;
    }

    /**
     * Returns true if the given name matches the Step object's
     * name, taking into account the Step object's wildcards; returns
     * false otherwise.
     */
    public boolean isMatchingName(String uri, String localPart) {
	// check and normalize arguments
	if (localPart == null)
	    throw new IllegalArgumentException("need non-null localPart");
	if (uri != null && uri.equals(""))
	    uri = null;

	// split name into uri/localPart if we haven't done so already
	if (this.localPart == null && this.uri == null)
	    parseStepName();

	// generic wildcard
	if (this.uri == null && this.localPart.equals("*"))
	    return true;

	// match will null namespace
	if (uri == null && this.uri == null
		&& localPart.equals(this.localPart))
	    return true;

	if (uri != null && this.uri != null && uri.equals(this.uri)) {
	    // exact match
	    if (localPart.equals(this.localPart))
		return true;

	    // namespace-specific wildcard
	    if (this.localPart.equals("*"))
		return true;
	}

	// no match
	return false;
    }

    /** Returns true if the Step's depth is unlimited, false otherwise. */
    public boolean isDepthUnlimited() {
	return depthUnlimited;
    }

    /** Returns the Step's node name. */
    public String getName() {
	return name;
    }

    /** Returns a list of this Step object's predicates. */
    public List getPredicates() {
	return predicates;
    }

    /** Lazily computes some information about our name. */
    private void parseStepName() {
	String prefix;
	int colonIndex = name.indexOf(":");

	if (colonIndex == -1) {
	    // no colon, so localpart is simply name (even if it's "*")
	    prefix = null;
	    localPart = name;
	} else {
	    prefix = name.substring(0, colonIndex);
	    localPart = name.substring(colonIndex + 1);
	}

	uri = mapPrefix(prefix);
    }

    /** Returns a URI for the given prefix, given our mappings. */
    private String mapPrefix(String prefix) {
	// ability to specify a mapping is, as of yet, unimplemented
	if (prefix == null)
	    return null;
	else
	    throw new IllegalArgumentException(
		"unknown prefix '" + prefix + "'");
    }
}
