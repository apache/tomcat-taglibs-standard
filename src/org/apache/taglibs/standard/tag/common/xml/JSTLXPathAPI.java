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

import java.util.Vector;

import javax.servlet.jsp.JspTagException;
import javax.xml.transform.TransformerException;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * The methods in this class are convenience methods into the
 * low-level XPath API.
 * These functions tend to be a little slow, since a number of objects must be
 * created for each evaluation.  A faster way is to precompile the
 * XPaths using the low-level API, and then just use the XPaths
 * over and over.
 *
 * NOTE: In particular, each call to this method will create a new
 * XPathContext, a new DTMManager... and thus a new DTM. That's very
 * safe, since it guarantees that you're always processing against a
 * fully up-to-date view of your document. But it's also portentially
 * very expensive, since you're rebuilding the DTM every time. You should
 * consider using an instance of CachedXPathAPI rather than these static
 * methods.
 *
 * @see <a href="http://www.w3.org/TR/xpath">XPath Specification</a>
 * 
 */
public class JSTLXPathAPI extends XPathAPI {    
    /**
     * Use an XPath string to select a single node.
     * XPath namespace prefixes are resolved using the prefixResolver.
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     * @return The first node found that matches the XPath, or null.
     *
     * @throws JspTagException
     */
    public static Node selectSingleNode(
    Node contextNode, String str, PrefixResolver prefixResolver)
    throws JspTagException {
        
        // Have the XObject return its result as a NodeSetDTM.
        NodeIterator nl = selectNodeIterator(contextNode, str, prefixResolver);
        
        // Return the first node, or null
        return nl.nextNode();
    }
    
    /**
     * Use an XPath string to select a single node.
     * XPath namespace prefixes are resolved using the prefixResolver.
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     * @return The first node found that matches the XPath, or null.
     *
     * @throws JspTagException
     */
    public static Node selectSingleNode(
    Node contextNode, String str, PrefixResolver prefixResolver,
    XPathContext xpathSupport ) throws JspTagException {
        
        // Have the XObject return its result as a NodeSetDTM.
        NodeIterator nl = selectNodeIterator(contextNode, str, prefixResolver, xpathSupport);
        
        // Return the first node, or null
        return nl.nextNode();
    }
    
    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved using PrefixResolver.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     *  @return A NodeIterator, should never be null.
     *
     * @throws JspTagException
     */
    public static NodeIterator selectNodeIterator(
    Node contextNode, String str, PrefixResolver prefixResolver)
    throws JspTagException {
        
        // Execute the XPath, and have it return the result
        XObject list = eval(contextNode, str, prefixResolver, null);
        
        // Have the XObject return its result as a NodeSetDTM.
        return getNodeIterator(list);
    }
    
    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved using PrefixResolver.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     *  @return A NodeIterator, should never be null.
     *
     * @throws JspTagException
     */
    public static NodeIterator selectNodeIterator(
    Node contextNode, String str, PrefixResolver prefixResolver,
    XPathContext xpathSupport ) throws JspTagException {
        
        // Execute the XPath, and have it return the result
        XObject list = eval(contextNode, str, prefixResolver, xpathSupport);
        
        // Have the XObject return its result as a NodeSetDTM.
        return getNodeIterator(list);
    }
    
    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved using the prefixResolver.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     *  @return A NodeIterator, should never be null.
     *
     * @throws JspTagException
     */
    private static NodeList selectNodeList(
    Node contextNode, String str, PrefixResolver prefixResolver)
    throws JspTagException {
        // Execute the XPath, and have it return the result
        XObject list = eval(contextNode, str, prefixResolver, null);
        
        // Return a NodeList.
        return getNodeList(list);
    }
    
    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved using the prefixResolver.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @param prefixResolver The PrefixResolver using which prefixes in the XPath will be resolved to namespaces.
     *  @return A NodeIterator, should never be null.
     *
     * @throws JspTagException
     */
    public static NodeList selectNodeList(
    Node contextNode, String str, PrefixResolver prefixResolver,
    XPathContext xpathSupport ) 
    throws JspTagException 
    {        
        // Execute the XPath, and have it return the result
        XObject list = eval(contextNode, str, prefixResolver, xpathSupport);
        
        // Return a NodeList.
        return getNodeList(list);
    }
        
    /**
     * Returns a NodeIterator from an XObject.
     *  @param list The XObject from which a NodeIterator is returned.
     *  @return A NodeIterator, should never be null.
     *  @throws JspTagException
     */
    private static NodeIterator getNodeIterator(XObject list) 
    throws JspTagException {
        try {
            return list.nodeset();
        } catch (TransformerException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ERROR_XOBJECT", ex.toString()), ex);            
        }
    }        

    /**
     * Returns a NodeList from an XObject.
     *  @param list The XObject from which a NodeList is returned.
     *  @return A NodeList, should never be null.
     *  @throws JspTagException
     */
    static NodeList getNodeList(XObject list) 
    throws JspTagException {
        try {
            return list.nodelist();
        } catch (TransformerException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ERROR_XOBJECT", ex.toString()), ex);            
        }
    }        
    
    /**
     *   Evaluate XPath string to an XObject.
     *   XPath namespace prefixes are resolved from the namespaceNode.
     *   The implementation of this is a little slow, since it creates
     *   a number of objects each time it is called.  This could be optimized
     *   to keep the same objects around, but then thread-safety issues would arise.
     *
     *   @param contextNode The node to start searching from.
     *   @param str A valid XPath string.
     *   @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
     *   @param prefixResolver Will be called if the parser encounters namespace
     *                         prefixes, to resolve the prefixes to URLs.
     *   @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
     *   @see org.apache.xpath.objects.XObject
     *   @see org.apache.xpath.objects.XNull
     *   @see org.apache.xpath.objects.XBoolean
     *   @see org.apache.xpath.objects.XNumber
     *   @see org.apache.xpath.objects.XString
     *   @see org.apache.xpath.objects.XRTreeFrag
     *
     * @throws JspTagException
     */
    public static XObject eval(
    Node contextNode, String str, PrefixResolver prefixResolver,
    XPathContext xpathSupport) throws JspTagException {
        //System.out.println("eval of XPathContext params: contextNode:str(xpath)"+
        // ":prefixResolver:xpathSupport => " + contextNode + ":" + str + ":" +
        //  prefixResolver + ":" + xpathSupport );        
        try {
            if (xpathSupport == null) {
                return eval(contextNode, str, prefixResolver);
            }
            
            // Since we don't have a XML Parser involved here, install some default support
            // for things like namespaces, etc.
            // (Changed from: XPathContext xpathSupport = new XPathContext();
            //    because XPathContext is weak in a number of areas... perhaps
            //    XPathContext should be done away with.)
            // Create the XPath object.
            XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null);
            
            // Execute the XPath, and have it return the result
            int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
            
            // System.out.println("Context Node id ( after getDTMHandlerFromNode) => " + ctxtNode );
            XObject xobj = xpath.execute(xpathSupport, ctxtNode, prefixResolver);
            return xobj;
        } catch (TransformerException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ERROR_EVALUATING_EXPR", str, ex.toString()), ex);            
        } catch (IllegalArgumentException ex) {
            throw new JspTagException(
                Resources.getMessage("XPATH_ILLEGAL_ARG_EVALUATING_EXPR", str, ex.toString()), ex);            
        }
    }
    
    public static XObject eval(
    Node contextNode, String str, PrefixResolver prefixResolver,
    XPathContext xpathSupport, Vector varQNames) throws JspTagException {
        //p("***************** eval ");
        //p( "contextNode => " + contextNode );
        //p( "XPath str => " + str );
        //p( "PrefixResolver => " + prefixResolver );
        //p( "XPath Context => " + xpathSupport );
        //p( "Var QNames => " + varQNames );
        //p( "Global Var Size => " + varQNames.size() );        
        try {
            XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null);            
            xpath.fixupVariables( varQNames, varQNames.size());
            // Execute the XPath, and have it return the result
            int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);            
            // System.out.println("Context Node id ( after getDTMHandlerFromNode) => " + ctxtNode );            
            return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
        } catch (TransformerException ex) {
            throw new JspTagException(
            Resources.getMessage("XPATH_ERROR_EVALUATING_EXPR", str, ex.toString()), ex);
        } catch (IllegalArgumentException ex) {
            throw new JspTagException(
            Resources.getMessage("XPATH_ILLEGAL_ARG_EVALUATING_EXPR", str, ex.toString()), ex);
        }
    } 
    
    private static void p(String s) {
        System.out.println("[JSTLXPathAPI] " + s);
    }    
}
