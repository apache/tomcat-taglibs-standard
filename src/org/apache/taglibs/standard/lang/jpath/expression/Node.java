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

package org.apache.taglibs.standard.lang.jpath.expression;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.jpath.adapter.IterationContext;

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */

/**
 * The Node interface
 *
 *
 * @author <a href='mailto:scott.hasse@isthmusgroup.com'>Scott Hasse</a>
 * @version 0.9
 */
public interface Node {

    /**
     * This method is called after the node has been made the current
     * node.  It indicates that child nodes can now be added to it. 
     */
    public void jjtOpen();

    /**
     * This method is called after all the child nodes have been
     * added. 
     */
    public void jjtClose();

    /**
     * This pair of methods are used to inform the node of its
     * parent. 
     *
     * @param n
     */
    public void jjtSetParent(Node n);

    /**
     * The jjtGetParent method
     *
     *
     * @return
     *
     */
    public Node jjtGetParent();

    /**
     * This method tells the node to add its argument to the node's
     * list of children.  
     *
     * @param n
     * @param i
     */
    public void jjtAddChild(Node n, int i);

    /**
     * This method returns a child node.  The children are numbered
     *  from zero, left to right. 
     *
     * @param i
     *
     * @return
     */
    public Node jjtGetChild(int i);

    /**
     * Return the number of children the node has. 
     *
     * @return
     */
    public int jjtGetNumChildren();

    /* Added by Scott Hasse */

    /**
     * The toNormalizedString method
     *
     *
     * @return
     *
     */
    public String toNormalizedString();

    /**
     * The evaluate method
     *
     *
     * @param pageContext
     * @param icontext
     *
     * @return
     *
     * @throws EvaluationException
     *
     */
    public Object evaluate(PageContext pageContext, IterationContext icontext)
        throws EvaluationException;

    /**
     * The validate method
     *
     *
     * @throws ValidationException
     *
     */
    public void validate() throws ValidationException;

    /**
     * The simplify method
     *
     *
     * @return
     *
     */
    public Node simplify();
}
