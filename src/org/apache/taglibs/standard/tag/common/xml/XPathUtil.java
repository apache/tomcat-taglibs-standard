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

package org.apache.taglibs.standard.tag.common.xml;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;
import org.apache.xpath.*;
import org.apache.xpath.objects.*;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.xml.utils.QName;

/**
 * <p>Support for tag handlers that evaluate XPath expressions.</p>
 *
 * @author Shawn Bayern
 */
// would ideally be a base class, but some of our user handlers already
// have their own parents
public class XPathUtil {
    
    //*********************************************************************
    // Constructor
    
    /**
     * Constructs a new XPathUtil object associated with the given
     * PageContext.
     */
    public XPathUtil(PageContext pc) {
        pageContext = pc;
    }    
    
    //*********************************************************************
    // Support for JSTL variable resolution
    
    // The URLs
    private static final String PAGE_NS_URL
    = "http://java.sun.com/jstl/xpath/page";
    private static final String REQUEST_NS_URL
    = "http://java.sun.com/jstl/xpath/request";
    private static final String SESSION_NS_URL
    = "http://java.sun.com/jstl/xpath/session";
    private static final String APP_NS_URL
    = "http://java.sun.com/jstl/xpath/app";
    private static final String PARAM_NS_URL
    = "http://java.sun.com/jstl/xpath/param";
    private static final String INITPARAM_NS_URL
    = "http://java.sun.com/jstl/xpath/initParam";
    private static final String COOKIE_NS_URL
    = "http://java.sun.com/jstl/xpath/cookie";
    private static final String HEADER_NS_URL
    = "http://java.sun.com/jstl/xpath/header";
    
    // The prefixes
    private static final String PAGE_P = "pageScope";
    private static final String REQUEST_P = "requestScope";
    private static final String SESSION_P = "sessionScope";
    private static final String APP_P = "applicationScope";
    private static final String PARAM_P = "param";
    private static final String INITPARAM_P = "initParam";
    private static final String COOKIE_P = "cookie";
    private static final String HEADER_P = "header";
    
    /**
     * org.apache.xpath.VariableStack defines a class to keep track of a stack 
     * for template arguments and variables. 
     * JstlVariableContext customizes it so it handles JSTL custom
     * variable-mapping rules.
     */
    protected class JstlVariableContext extends org.apache.xpath.VariableStack {
        
        public JstlVariableContext( ) {
            super();
        }
        
        /**
         * Get a variable as an XPath object based on it's qualified name.
         * We override the base class method so JSTL's custom variable-mapping 
         * rules can be applied.
         *
         * @param xctxt The XPath context. @@@ we don't use it...
         *  (from xalan: which must be passed in order to lazy evaluate variables.)
         * @param qname The qualified name of the variable.
         */
        public XObject getVariableOrParam(
        XPathContext xctxt, 
        org.apache.xml.utils.QName qname)
        throws javax.xml.transform.TransformerException, UnresolvableException
        {
            String namespace = qname.getNamespaceURI();
            String prefix = qname.getPrefix();
            String localName = qname.getLocalName();
            
            Object obj = getVariableValue(namespace, prefix, localName);
            return new XObject(obj);
        }
        
        /**
         * Retrieve an XPath's variable value using JSTL's custom 
         * variable-mapping rules
         */
        public Object getVariableValue(
        String namespace, 
        String prefix,
        String localName) 
        throws UnresolvableException 
        {
            //p("resolving: " + namespace + "/" + prefix + "/" + localName);
            // I'd prefer to match on namespace, but this doesn't appear
            // to work in Jaxen
            if (prefix == null || prefix.equals("")) {
                return notNull(
                pageContext.findAttribute(localName),
                prefix,
                localName);
            } else if (prefix.equals(PAGE_P)) {
                return notNull(
                pageContext.getAttribute(localName,PageContext.PAGE_SCOPE),
                prefix,
                localName);
            } else if (prefix.equals(REQUEST_P)) {
                return notNull(
                pageContext.getAttribute(localName,
                PageContext.REQUEST_SCOPE),
                prefix,
                localName);
            } else if (prefix.equals(SESSION_P)) {
                return notNull(
                pageContext.getAttribute(localName,
                PageContext.SESSION_SCOPE),
                prefix,
                localName);
            } else if (prefix.equals(APP_P)) {
                return notNull(
                pageContext.getAttribute(localName,
                PageContext.APPLICATION_SCOPE),
                prefix,
                localName);
            } else if (prefix.equals(PARAM_P)) {
                return notNull(
                pageContext.getRequest().getParameter(localName),
                prefix,
                localName);
            } else if (prefix.equals(INITPARAM_P)) {
                return notNull(
                pageContext.getServletContext().
                getInitParameter(localName),
                prefix,
                localName);
            } else if (prefix.equals(HEADER_P)) {
                HttpServletRequest hsr =
                (HttpServletRequest) pageContext.getRequest();
                return notNull(
                hsr.getHeader(localName),
                prefix,
                localName);
            } else if (prefix.equals(COOKIE_P)) {
                HttpServletRequest hsr =
                (HttpServletRequest) pageContext.getRequest();
                Cookie[] c = hsr.getCookies();
                for (int i = 0; i < c.length; i++)
                    if (c[i].getName().equals(localName))
                        return c[i].getValue();
                throw new UnresolvableException("$" + prefix + ":" + localName);
            } else {
                throw new UnresolvableException("$" + prefix + ":" + localName);
            }
        }    
        
        /**
         * Validate that the Object returned is not null. If it is
         * null, throw an exception.
         */
        private Object notNull(Object o, String prefix, String localName)
        throws UnresolvableException {
            if (o == null) {
                throw new UnresolvableException("$" + (prefix==null?"":"prefix"+":") + localName);
            }
            //p("resolved to: " + o);
            return o;
        }                
    }
    
    //*********************************************************************
    // Support for XPath evaluation
    
    private PageContext pageContext;
    private static HashMap exprCache;
    private static JSTLPrefixResolver jstlPrefixResolver;
    
    /** Initialize globally useful data. */
    private synchronized static void staticInit() {
        if (jstlPrefixResolver == null) {
            // register supported namespaces
            jstlPrefixResolver = new JSTLPrefixResolver();
            jstlPrefixResolver.addNamespace("page", PAGE_NS_URL);
            jstlPrefixResolver.addNamespace("request", REQUEST_NS_URL);
            jstlPrefixResolver.addNamespace("session", SESSION_NS_URL);
            jstlPrefixResolver.addNamespace("application", APP_NS_URL);
            jstlPrefixResolver.addNamespace("param", PARAM_NS_URL);
            jstlPrefixResolver.addNamespace("initParam", INITPARAM_NS_URL);
            jstlPrefixResolver.addNamespace("header", HEADER_NS_URL);
            jstlPrefixResolver.addNamespace("cookie", COOKIE_NS_URL);
            
            
            // create a HashMap to cache the expressions
            exprCache = new HashMap();
        }
    }
    
    static DocumentBuilderFactory dbf = null;
    static DocumentBuilder db = null;
    static Document d = null;
    
    static Document getDummyDocument( ) {
        try {
            if ( dbf == null ) {
                dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware( true );
                dbf.setValidating( false );
            }
            db = dbf.newDocumentBuilder();
            d = db.newDocument();
            return d;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
    
    // The following variable is used for holding the modified xpath string
    // when adapting parameter for Xalan XPath engine, where we need to have
    // a Non null context node.
    String modifiedXPath = null;
    

    
    
    
    /**
     * Evaluate an XPath expression to a String value. 
     */
    public String valueOf(Node n, String xpath) throws JspTagException  {
        //System.out.println("valueOf of XPathUtil = passed node: xpath => " +
        //     n + " : " + xpath );        
        staticInit();
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        
        xpath = modifiedXPath;
        
        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver,xpathSupport );
        
        String resultString = result.str();
        
        return resultString;
    }    

    /** 
     * Evaluate an XPath expression to a boolean value. 
     */
    public boolean booleanValueOf(Node n, String xpath)
    throws JspTagException {
        
        staticInit();
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        xpath = modifiedXPath;
        
        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver, xpathSupport );
        
        try {
            return result.bool();
        } catch (TransformerException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ERROR_XOBJECT", ex.getMessage()), ex);            
        }
    }
    
    /** 
     * Evaluate an XPath expression to a List of nodes. 
     */
    public List selectNodes(Node n, String xpath)  throws JspTagException {
        
        staticInit();
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        xpath = modifiedXPath;
        
        
        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver,xpathSupport );
        
        NodeList nl= JSTLXPathAPI.getNodeList(result);
        // System.out.println("NodeList => " + nl );
        Vector resultVect = new Vector();
        for ( int i=0; i<nl.getLength(); i++ ) {
            Node currNode = nl.item(i);
            //printDetails ( currNode );
            resultVect.add(i, nl.item(i) );
        }
        return resultVect;
    }
    
    /** 
     * Evaluate an XPath expression to a single node. 
     */
    public Node selectSingleNode(Node n, String xpath)
    throws JspTagException {
        //System.out.println("selectSingleNode of XPathUtil = passed node:" +
        //   "xpath => " + n + " : " + xpath );
        
        staticInit();
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        xpath = modifiedXPath;
        
        return (Node) JSTLXPathAPI.selectSingleNode( contextNode, xpath,
        jstlPrefixResolver,xpathSupport );
    }
    
    /** Returns a locally appropriate context given a node. */
    private VariableStack getLocalContext() {
        // set up instance-specific contexts
        VariableStack vc = new JstlVariableContext();
        return vc;
    }    
    
    //*********************************************************************
    // Adapt XPath expression for integration with Xalan
   
    /**
     * To evaluate an XPath expression using Xalan, we need 
     * to create an XPath object, which wraps an expression object and provides 
     * general services for execution of that expression.
     *
     * An XPath object can be instantiated with the following information:
     *     - XPath expression to evaluate
     *     - SourceLocator 
     *        (reports where an error occurred in the XML source or 
     *        transformation instructions)
     *     - PrefixResolver
     *        (resolve prefixes to namespace URIs)
     *     - type
     *        (one of SELECT or MATCH)
     *     - ErrorListener
     *        (customized error handling)
     *
     * Execution of the XPath expression represented by an XPath object
     * is done via method execute which takes the following parameters:
     *     - XPathContext 
     *        The execution context
     *     - Node contextNode
     *        The node that "." expresses
     *     - PrefixResolver namespaceContext
     *        The context in which namespaces in the XPath are supposed to be 
     *        expanded.
     *
     * Given all of this, if no context node is set for the evaluation
     * of the XPath expression, one must be set so Xalan 
     * can successfully evaluate a JSTL XPath expression.
     * (it will not work if the context node is given as a varialbe
     * at the beginning of the expression)
     *
     * @@@ Provide more details...
     */
    protected Node adaptParamsForXalan( JstlVariableContext jvc,  Node n,
    String xpath ) {
        Node boundDocument = null;
        
        modifiedXPath = xpath;
        String origXPath = xpath ;
        
        
        // If contextNode is not null then  just pass the values to Xalan XPath
        if ( n != null ) {
            return n;
        }
        
        if (  xpath.startsWith("$")  ) {
            // JSTL uses $scopePrefix:varLocalName/xpath expression
            String varQName= xpath.substring( xpath.indexOf("$")+1,
            xpath.indexOf("/") );
            String varPrefix =  null;
            String varLocalName =  varQName;
            if ( varQName.indexOf( ":") >= 0 ) {
                varPrefix = varQName.substring( 0, varQName.indexOf(":") );
                varLocalName = varQName.substring( varQName.indexOf(":")+1 );
            }
            
            if ( xpath.indexOf("/") > 0 ) {
                xpath = xpath.substring( xpath.indexOf("/"));
            }
            
            try {
                Object varObject=jvc.getVariableValue( null,varPrefix,
                varLocalName);
                //System.out.println( "varObject => : its Class " +varObject +
                // ":" + varObject.getClass() );
                
                if ( Class.forName("org.w3c.dom.Document").isInstance(
                varObject ) )  {
                    boundDocument = (Document)varObject;
                    //System.out.println("Document bound to " + varQName +
                    // " => "+ boundDocument );
                } else {
                    
                    //System.out.println("Creating a Dummy document to pass " +
                    // " onto as context node " );
                    
                    if ( Class.forName("java.util.Vector").isInstance(
                    varObject ) ) {
                        Document newDocument = getDummyDocument();
                        Vector nodeVector = (Vector)varObject;
                        for ( int i=0; i< nodeVector.size(); i++ ) {
                            Node currNode = (Node)nodeVector.elementAt(i);
                           /*
                            System.out.println("Current Node => " + currNode );
                            String namespaceURI = currNode.getNamespaceURI();
                            String prefix = currNode.getPrefix();
                            String localName = currNode.getLocalName();
                            QName qname = new QName( namespaceURI, prefix,
                                localName );
                            
                            printDetails ( currNode );
                            */
                            
                            Node importedNode = newDocument.importNode(
                            currNode, true );
                            newDocument.appendChild( importedNode );
                        }
                        boundDocument = newDocument;
                        // printDetails ( boundDocument );
                        // Verify : As we are adding Document element we need to
                        // change the xpath expression. Hopefully this won't
                        // change the result
                        xpath = "/*" +  xpath;
                    } else if ( Class.forName("org.w3c.dom.Node").isInstance(
                    varObject ) ) {
                        boundDocument = (Node)varObject;
                    } else {
                        boundDocument = getDummyDocument();
                        xpath = origXPath;
                    }
                    
                    
                }
            } catch ( UnresolvableException ue ) {
                System.out.println("Variable Unresolvable :" + ue.getMessage());
                ue.printStackTrace();
            } catch ( ClassNotFoundException cnf ) {
                // Will never happen
            }
        }
        modifiedXPath = xpath;
        return boundDocument;
    }
    
    //*********************************************************************
    // Static support for context retrieval from parent <forEach> tag
    
    public static Node getContext(Tag t) throws JspTagException {
        ForEachTag xt =
        (ForEachTag) TagSupport.findAncestorWithClass(
        t, ForEachTag.class);
        if (xt == null)
            return null;
        else
            return (xt.getContext());
    }
    
    //*********************************************************************
    // Utility methods
    
    private static void p(String s) {
        System.out.println("[XPathUtil] " + s);
    }
    
    public void printDetails(Node n) {
        System.out.println("\n\nDetails of Node = > " + n ) ;
        System.out.println("Name:Type:Node Value = > " + n.getNodeName() +
        ":" + n.getNodeType() + ":" + n.getNodeValue()  ) ;
        System.out.println("Namespace URI : Prefix : localName = > " +
        n.getNamespaceURI() + ":" +n.getPrefix() + ":" + n.getLocalName());
        System.out.println("\n Node has children => " + n.hasChildNodes() );
        if ( n.hasChildNodes() ) {
            NodeList nl = n.getChildNodes();
            System.out.println("Number of Children => " + nl.getLength() );
            for ( int i=0; i<nl.getLength(); i++ ) {
                Node childNode = nl.item(i);
                printDetails( childNode );
            }
        }
    }    
}
