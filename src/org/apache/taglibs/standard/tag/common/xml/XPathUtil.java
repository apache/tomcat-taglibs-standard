/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

package org.apache.taglibs.standard.tag.common.xml;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Support for tag handlers that evaluate XPath expressions.</p>
 *
 * @author Shawn Bayern
 * @author Ramesh Mandava ( ramesh.mandava@sun.com )
 * @author Pierre Delisle ( pierre.delisle@sun.com )
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
    
    int globalVarSize=0;
    public Vector getVariableQNames ( ) {

        globalVarSize = 0;
        Vector variableVector = new Vector ( );
        // Now construct attributes in different scopes
        Enumeration enum = pageContext.getAttributeNamesInScope( 
            PageContext.PAGE_SCOPE );
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( PAGE_NS_URL, PAGE_P, varName); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
            
            variableVector.addElement( new QName(null, varName ) );
            globalVarSize++;
        }
        enum = pageContext.getAttributeNamesInScope( 
            PageContext.REQUEST_SCOPE );
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( REQUEST_NS_URL,REQUEST_P, varName); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
            variableVector.addElement( new QName(null, varName ) );
            globalVarSize++;
        }
        enum = pageContext.getAttributeNamesInScope( 
            PageContext.SESSION_SCOPE );
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( SESSION_NS_URL, SESSION_P,varName); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
            variableVector.addElement( new QName(null, varName ) );
            globalVarSize++;
        }
        enum = pageContext.getAttributeNamesInScope( 
            PageContext.APPLICATION_SCOPE );
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( APP_NS_URL, APP_P,varName ); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
            variableVector.addElement( new QName(null, varName ) );
            globalVarSize++;
        }
        enum = pageContext.getRequest().getParameterNames();
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( PARAM_NS_URL, PARAM_P,varName ); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
        }
        enum = pageContext.getServletContext().getInitParameterNames();
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( INITPARAM_NS_URL, INITPARAM_P,varName ); 
            //Adding both namespace qualified QName and just localName
            variableVector.addElement( varQName );
            globalVarSize++;
        }
        enum = ((HttpServletRequest)pageContext.getRequest()).getHeaderNames();
        while ( enum.hasMoreElements() ) {
            String varName = (String)enum.nextElement();
            QName varQName = new QName ( HEADER_NS_URL, HEADER_P,varName ); 
            //Adding namespace qualified QName 
            variableVector.addElement( varQName );
            globalVarSize++;
        }
        Cookie[] c= ((HttpServletRequest)pageContext.getRequest()).getCookies();
        if ( c!= null ) {
	    for (int i = 0; i < c.length; i++) {
	        String varName = c[i].getName();
                QName varQName = new QName ( COOKIE_NS_URL, COOKIE_P,varName ); 
                //Adding namespace qualified QName 
                variableVector.addElement( varQName );
                globalVarSize++;
            }
        }

        return variableVector;
        
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
            //p( "***********************************getVariableOrParam begin****");
            String namespace = qname.getNamespaceURI();
            String prefix = qname.getPrefix();
            String localName = qname.getLocalName();
            
            //p("namespace:prefix:localname=>"+ namespace
            //     + ":" + prefix +":" + localName );
            
            try {
                Object varObject = getVariableValue(namespace,prefix,localName);


                //XObject varObject = myvs.getVariableOrParam( xpathSupport, varQName);
                XObject newXObject = new XObject( varObject);

                if ( Class.forName("org.w3c.dom.Document").isInstance( varObject) ) {

                    NodeList nl= ((Document)varObject).getChildNodes();
                    // To allow non-welformed document
                    Vector nodeVector = new Vector();
                    for ( int i=0; i<nl.getLength(); i++ ) {
                        Node currNode = nl.item(i);
                        if ( currNode.getNodeType() == Node.ELEMENT_NODE ) {
                            nodeVector.addElement( currNode);
                        }
                    }
                    JSTLNodeList jstlNodeList = new JSTLNodeList( nodeVector);
                    newXObject = new XNodeSetForDOM( jstlNodeList, xctxt );
                    
                    return newXObject;
                   
                } 
                if ( Class.forName(
        "org.apache.taglibs.standard.tag.common.xml.JSTLNodeList").isInstance(
                     varObject) ) {
                    JSTLNodeList jstlNodeList = (JSTLNodeList)varObject;
                    if  ( ( jstlNodeList.getLength() == 1 ) && 
   (!Class.forName("org.w3c.dom.Node").isInstance( jstlNodeList.elementAt(0) ) ) ) { 
                        varObject = jstlNodeList.elementAt(0);
                        //Now we need to allow this primitive type to be coverted 
                        // to type which Xalan XPath understands 
                    } else {
                        return new XNodeSetForDOM (  jstlNodeList ,xctxt );
                    }
                } 
                if (Class.forName("org.w3c.dom.Node").isInstance( varObject)) {
                    newXObject = new XNodeSetForDOM ( new JSTLNodeList( (Node)varObject ),xctxt );
                } else if ( Class.forName("java.lang.String").isInstance( varObject)){
                    newXObject = new XString ( (String)varObject );
                } else if ( Class.forName("java.lang.Boolean").isInstance( varObject) ) {
                    newXObject = new XBoolean ( (Boolean)varObject );
                } else if ( Class.forName("java.lang.Number").isInstance( varObject) ) {
                    newXObject = new XNumber ( (Number)varObject );
                } 

                return newXObject;
               // myvs.setGlobalVariable( i, newXObject );
            } catch ( ClassNotFoundException cnfe ) {
                // This shouldn't happen (FIXME: LOG)
                System.out.println("CLASS NOT FOUND EXCEPTION :" + cnfe );
            } 
            //System.out.println("*****getVariableOrParam returning *null*" );
            return null ;
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
            // p("resolving: ns=" + namespace + " prefix=" + prefix + " localName=" + localName);
            // We can match on namespace with Xalan but leaving as is
            // [ I 'd prefer to match on namespace, but this doesn't appear
            // to work in Jaxen]
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
                throw new UnresolvableException("$" + (prefix==null?"":prefix+":") + localName);
            }
            //p("resolved to: " + o);
            return o;
        }                
    }
    
    //*********************************************************************
    // Support for XPath evaluation
    
    private PageContext pageContext;
    private static HashMap exprCache;
    private static JSTLPrefixResolver jstlPrefixResolver = null;
    
    /** Initialize globally useful data. */
    private synchronized static void staticInit() {
        if (jstlPrefixResolver == null) {
            // register supported namespaces
            jstlPrefixResolver = new JSTLPrefixResolver();
            jstlPrefixResolver.addNamespace("pageScope", PAGE_NS_URL);
            jstlPrefixResolver.addNamespace("requestScope", REQUEST_NS_URL);
            jstlPrefixResolver.addNamespace("sessionScope", SESSION_NS_URL);
            jstlPrefixResolver.addNamespace("applicationScope", APP_NS_URL);
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

            DOMImplementation dim = db.getDOMImplementation();
            d = dim.createDocument("http://java.sun.com/jstl", "dummyroot", null); 
            //d = db.newDocument();
            return d;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

     static Document getDummyDocumentWithoutRoot( ) {
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
        //p("******** valueOf(" + n + ", " + xpath + ")");
        staticInit();
        // @@@ but where do we set the Pag4eContext for the varaiblecontext?
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        
        Vector varVector = fillVarStack(vs, xpathSupport);                
        
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        
        xpath = modifiedXPath;
        
        //p("******** valueOf: modified xpath: " + xpath);

        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver,xpathSupport, varVector);

        
        //p("******Result TYPE => " + result.getTypeString() );
        
        String resultString = result.str();
        //p("******** valueOf: after eval: " + resultString);
        
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
        
        Vector varVector = fillVarStack(vs, xpathSupport);        
        
        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        xpath = modifiedXPath;
        
        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver, xpathSupport, varVector);
        
        try {
            return result.bool();
        } catch (TransformerException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ERROR_XOBJECT", ex.toString()), ex);            
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
        
        Vector varVector = fillVarStack(vs, xpathSupport);                

        Node contextNode = adaptParamsForXalan( vs, n, xpath.trim() );
        xpath = modifiedXPath;
        
        XObject result = JSTLXPathAPI.eval( contextNode, xpath,
        jstlPrefixResolver,xpathSupport, varVector);
        try {
            NodeList nl= JSTLXPathAPI.getNodeList(result);
            return new JSTLNodeList( nl );
        } catch ( JspTagException e ) {
            try { 
                //If result can't be converted to NodeList we receive exception
                // In this case we may have single primitive value as the result
                // Populating List with this value ( String, Boolean or Number )

                //System.out.println("JSTLXPathAPI.getNodeList thrown exception:"+ e);
                Vector vector = new Vector();
                Object resultObject = null;
                if ( result.getType()== XObject.CLASS_BOOLEAN ) {
                    resultObject = new Boolean( result.bool());
                } else if ( result.getType()== XObject.CLASS_NUMBER ) {
                    resultObject = new Double( result.num());
                } else if ( result.getType()== XObject.CLASS_STRING ) {
                    resultObject = result.str();
                }

                vector.add( resultObject );
                return new JSTLNodeList ( vector );
            } catch ( TransformerException te ) {
                throw new JspTagException(te.toString(), te);
            }
        }
          
        
       
    }
    
    /** 
     * Evaluate an XPath expression to a single node. 
     */
    public Node selectSingleNode(Node n, String xpath)
    throws JspTagException {
        //p("selectSingleNode of XPathUtil = passed node:" +
        //   "xpath => " + n + " : " + xpath );
        
        staticInit();
        JstlVariableContext vs = new JstlVariableContext();
        XPathContext xpathSupport = new XPathContext();
        xpathSupport.setVarStack( vs);
        
        Vector varVector = fillVarStack(vs, xpathSupport);                

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
        boolean whetherOrigXPath = true;
        
        // If contextNode is not null then  just pass the values to Xalan XPath
        if ( n != null ) {
            return n;
        }
        
        if (  xpath.startsWith("$")  ) {
            // JSTL uses $scopePrefix:varLocalName/xpath expression

            String varQName=  xpath.substring( xpath.indexOf("$")+1);
            if ( varQName.indexOf("/") > 0 ) {
                varQName = varQName.substring( 0, varQName.indexOf("/"));
            }
            String varPrefix =  null;
            String varLocalName =  varQName;
            if ( varQName.indexOf( ":") >= 0 ) {
                varPrefix = varQName.substring( 0, varQName.indexOf(":") );
                varLocalName = varQName.substring( varQName.indexOf(":")+1 );
            }
            
            if ( xpath.indexOf("/") > 0 ) {
                xpath = xpath.substring( xpath.indexOf("/"));
            } else  {
                xpath = "/*";
                whetherOrigXPath = false; 
            }
           
            
            try {
                Object varObject=jvc.getVariableValue( null,varPrefix,
                varLocalName);
                //System.out.println( "varObject => : its Class " +varObject +
                // ":" + varObject.getClass() );
                
                if ( Class.forName("org.w3c.dom.Document").isInstance(
                varObject ) )  {
                    //boundDocument = ((Document)varObject).getDocumentElement();
                    boundDocument = ((Document)varObject);
                } else {
                    
                    //System.out.println("Creating a Dummy document to pass " +
                    // " onto as context node " );
                    
                    if ( Class.forName("org.apache.taglibs.standard.tag.common.xml.JSTLNodeList").isInstance( varObject ) ) {
                        Document newDocument = getDummyDocument();

                        JSTLNodeList jstlNodeList = (JSTLNodeList)varObject;
                        if   ( jstlNodeList.getLength() == 1 ) { 
                            if ( Class.forName("org.w3c.dom.Node").isInstance(
                                jstlNodeList.elementAt(0) ) ) { 
                                Node node = (Node)jstlNodeList.elementAt(0);
                                Document doc = getDummyDocumentWithoutRoot();
                                Node importedNode = doc.importNode( node, true);
                                doc.appendChild (importedNode );
                                boundDocument = doc;
                                if ( whetherOrigXPath ) {
                                    xpath="/*" + xpath;
                                }

                            } else {

                                //Nodelist with primitive type
                                Object myObject = jstlNodeList.elementAt(0);

                                //p("Single Element of primitive type");
                                //p("Type => " + myObject.getClass());

                                xpath = myObject.toString();

                                //p("String value ( xpathwould be this) => " + xpath);
                                boundDocument = newDocument;
                            } 
                            
                        } else {

                            Element dummyroot = newDocument.getDocumentElement();
                            for ( int i=0; i< jstlNodeList.getLength(); i++ ) {
                                Node currNode = (Node)jstlNodeList.item(i);
                            
                                Node importedNode = newDocument.importNode(
                                    currNode, true );

                                //printDetails ( newDocument);

                                dummyroot.appendChild( importedNode );

                                //p( "Details of the document After importing");
                                //printDetails ( newDocument);
                            }
                            boundDocument = newDocument;
                            // printDetails ( boundDocument );
                            //Verify :As we are adding Document element we need
                            // to change the xpath expression.Hopefully this
                            // won't  change the result

                            xpath = "/*" +  xpath;
                        }
                    } else if ( Class.forName("org.w3c.dom.Node").isInstance(
                    varObject ) ) {
                        boundDocument = (Node)varObject;
                    } else {
                        boundDocument = getDummyDocument();
                        xpath = origXPath;
                    }
                    
                    
                }
            } catch ( UnresolvableException ue ) {
                // FIXME: LOG
                System.out.println("Variable Unresolvable :" + ue.getMessage());
                ue.printStackTrace();
            } catch ( ClassNotFoundException cnf ) {
                // Will never happen
            }
        } else { 
            //System.out.println("Not encountered $ Creating a Dummydocument 2 "+
             //   "pass onto as context node " );
            boundDocument = getDummyDocument();
        }
     
        modifiedXPath = xpath;
        //System.out.println("Modified XPath::boundDocument =>" + modifiedXPath +
        //    "::" + boundDocument );
         
        return boundDocument;
    }
    

    //*********************************************************************
    // 
    
    /**
     ** @@@ why do we have to pass varVector in the varStack first, and then
     * to XPath object?
     */
    private Vector fillVarStack(JstlVariableContext vs, XPathContext xpathSupport) 
    throws JspTagException {
        org.apache.xpath.VariableStack myvs = xpathSupport.getVarStack();
        Vector varVector = getVariableQNames();
        for ( int i=0; i<varVector.size(); i++ ) {
          
            QName varQName = (QName)varVector.elementAt(i);

            try { 
                XObject variableValue = vs.getVariableOrParam( xpathSupport, varQName );
                //p("&&&&Variable set to => " + variableValue.toString() );
                //p("&&&&Variable type => " + variableValue.getTypeString() );
                myvs.setGlobalVariable( i, variableValue );

            } catch ( TransformerException te ) {
                throw new JspTagException(te.toString(), te);
            } 
 
        }
        return varVector;
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
    
    public static void printDetails(Node n) {
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

class JSTLNodeList extends Vector implements NodeList   {
    
    Vector nodeVector;

    public JSTLNodeList ( Vector nodeVector ) {
        this.nodeVector = nodeVector;
    }

    public JSTLNodeList ( NodeList nl ) {
        nodeVector = new Vector();
        //System.out.println("[JSTLNodeList] nodelist details");
        for ( int i=0; i<nl.getLength(); i++ ) {
            Node currNode = nl.item(i);
            //XPathUtil.printDetails ( currNode );
            nodeVector.add(i, nl.item(i) );
        }
    }

    public JSTLNodeList ( Node n ) {
        nodeVector = new Vector();
        nodeVector.addElement( n );
    }
        

    public Node item ( int index ) {
        return (Node)nodeVector.elementAt( index );
    }

    public Object elementAt ( int index ) {
        return nodeVector.elementAt( index );
    }

    public Object get ( int index ) {
        return nodeVector.get( index );
    }

    public int getLength (  ) {
        return nodeVector.size( );
    }

    public int size (  ) {
        //System.out.println("JSTL node list size => " + nodeVector.size() );
        return nodeVector.size( );
    }

    // Can implement other Vector methods to redirect those methods to 
    // the vector in the variable param. As we are not using them as part 
    // of this implementation we are not doing that here. If this changes
    // then we need to override those methods accordingly  

}
         



