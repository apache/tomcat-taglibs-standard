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

/**
 * This class provides an abstract implementation of the <tt>Node</tt>
 * iterface.  All actual nodes should extend this class to take advantage
 * of the default implementations of many of the methods.
 */
public abstract class SimpleNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Parser parser;
    protected String image;
    protected int beginColumn;
    protected int endColumn;
    protected int beginLine;
    protected int endLine;
    protected Token firstToken;
    protected Token lastToken;
    protected Object val;

    /**
     * Used to create an instance of the SimpleNode class
     *
     *
     * @param i
     *
     */
    public SimpleNode(int i) {
        id = i;
    }

    /**
     * Used to create an instance of the SimpleNode class
     *
     *
     * @param p
     * @param i
     *
     */
    public SimpleNode(Parser p, int i) {

        this(i);

        parser = p;
    }

    /**
     * The jjtOpen method
     *
     *
     */
    public void jjtOpen() {}

    /**
     * The jjtClose method
     *
     *
     */
    public void jjtClose() {}

    /**
     * The jjtSetParent method
     *
     *
     * @param n
     *
     */
    public void jjtSetParent(Node n) {
        parent = n;
    }

    /**
     * The jjtGetParent method
     *
     *
     * @return
     *
     */
    public Node jjtGetParent() {
        return parent;
    }

    /**
     * The jjtAddChild method
     *
     *
     * @param n
     * @param i
     *
     */
    public void jjtAddChild(Node n, int i) {

        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];

            System.arraycopy(children, 0, c, 0, children.length);

            children = c;
        }

        children[i] = n;
    }

    /**
     * The jjtGetChild method
     *
     *
     * @param i
     *
     * @return
     *
     */
    public Node jjtGetChild(int i) {
        return children[i];
    }

    /**
     * The jjtGetNumChildren method
     *
     *
     * @return
     *
     */
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /**
     * Provides a method to print the name of the node.  You can override
     * this method in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped.  If
     * your output uses more than one line you should override
     * toString(String), otherwise overriding toString() is probably all
     * you need to do.
     *
     * @return
     */
    public String toString() {
        return ParserTreeConstants.jjtNodeName[id];
    }

    /**
     * Provides a method to print the name of the node with a prefix string.
     * You can override
     * this method in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped.  If
     * your output uses more than one line you should override
     * toString(String), otherwise overriding toString() is probably all
     * you need to do.
     *
     * @param prefix
     *
     * @return
     */
    public String toString(String prefix) {
        return prefix + toString();
    }

    /**
     * Provides a method to dump the entire subtree of nodes to
     * <tt>System.out</tt>.  You should
     * override this method if you want to customize how the node dumps
     * out its children.
     *
     * @param prefix
     */
    public void dump(String prefix) {

        System.out.println(toString(prefix));

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    /**
     * Provides a method to dump the entire subtree of nodes to
     * <tt>System.out</tt> with a prefix string.  You should
     * override this method if you want to customize how the node dumps
     * out its children.
     *
     * @param prefix
     *
     * @return
     */
    public String stringDump(String prefix) {

        String result = prefix + toString() + "\n";

        System.out.println(toString(prefix));

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    result = result + n.stringDump(prefix + "     ");
                }
            }
        }

        return result;
    }

    /**
     * Provides a method to print a normalized version of the original
     * expression.  The normalized version has standardized spacing and
     * parenthesis, and can be used to compare expressions formatted
     * in different ways to see if they are actually the same expression.
     *
     * @returns The normalized version of the original expression
     *
     * @return
     */

    /**
     * Provides a method to print a normalized version of the original
     * expression.  The normalized version has standardized spacing and
     * parenthesis, and can be used to compare expressions formatted
     * in different ways to see if they are actually the same expression.
     *
     *
     * @return The normalized version of the original expression
     *
     */
    public String toNormalizedString() {

        String normalized = "toNormalizedString() not implemented in ["
                                + ParserTreeConstants.jjtNodeName[id] + "]";

        return normalized;
    }

    /**
     * Provides a method to print the original version of the
     * expression from this node downward in the tree.
     * The original version is an accurate representation
     * of the original expression as it was received.  This can be useful
     * when formatting error messages.
     *
     * @returns The original version of the expression, from this node downward
     *
     * @return
     */
    public String toOriginalString() {

        String result = "";
        Token t = firstToken;
        boolean finished = false;

        while (!finished) {
            result += getSpecialTokenString(t);

            if (t.image != null) {
                while (result.length() < t.beginColumn - 1) {
                    result += " ";
                }

                result += (t.image);
            }

            if (t == lastToken) {
                finished = true;
            } else {
                t = t.next;
            }
        }

        return result;
    }

    /**
     * Provides a method to print the original version of the
     * entire expression.
     * The original version is an accurate representation
     * of the original expression as it was received.  This can be useful
     * when formatting error messages.
     *
     * @returns The original version of the entire expression
     *
     * @return
     */
    public String rootOriginalString() {

        SimpleNode current = this;

        while (current.parent != null) {
            current = (SimpleNode) current.parent;
        }

        return current.toOriginalString();
    }

    /**
     * This method evaluates this node of the expression and all child nodes.
     * It returns the result of the
     * evaluation as an <tt>Object</tt>.  If any problems are encountered
     * during the evaluation, an <tt>EvaluationException</tt> is thrown.
     *
     * @param pageContext the current JSP PageContext
     *
     * @param icontext the Iteration Context of the expression.  If there is
     *         no interation context, this should be null.
     *
     * @returns the result of the expression evaluation as an object
     *
     *
     * @return
     * @throws EvaluationException if a problem is encountered during the
     *         evaluation
     */
    public Object evaluate(PageContext pageContext, IterationContext icontext)
            throws EvaluationException {

        throw new EvaluationException(this,
                "evaluate not supported in  ["
                + ParserTreeConstants.jjtNodeName[id] + "]");
    }

    /**
     * The getTokenImage method
     *
     *
     * @param tokenNumber
     *
     * @return
     *
     */
    public String getTokenImage(int tokenNumber) {

        String tokenImage = ParserConstants.tokenImage[tokenNumber];

        return tokenImage.substring(1, tokenImage.length() - 1);
    }

    /**
     * The getSpecialTokenString method
     *
     *
     * @param t
     *
     * @return
     *
     */
    private String getSpecialTokenString(Token t) {

        String result;

        if (t.specialToken == null) {
            result = "";
        } else {
            result = getSpecialTokenString(t) + t.image;
        }

        return result;
    }

    /**
     * Provides a mechanism to validate an expression.  This method
     * validates this node and all child nodes.  The validation is done
     * without any knowldege of the PageContext, and therefore
     * even after validation, problems may still be encountered during
     * evaluation.  However, many problems, such as invalid literals,
     * can be caught during validation.
     *
     * @throws ValidationException if a problem is encountered during the
     */
    public void validate() throws ValidationException {

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    n.validate();
                }
            }
        }
    }

    /**
     * Provides a mechanism for simplication of the expression.  For instance,
     * large mathematical expressions with number literals can be simplified
     * for faster subsequent evaluation.  Other simplification may be possible
     * with certain boolean logic expressions.  This mechansim may be used in
     * conjunction with an expression caching strategy.
     *
     * @returns the simplified node
     *
     *
     * @return
     */
    public Node simplify() {

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    n = (SimpleNode) n.simplify();
                }
            }
        }

        // Any custom simplification should be done here
        return this;
    }
}
