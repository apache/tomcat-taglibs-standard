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

package org.apache.taglibs.standard.tag.common.xml;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;parse&gt;, the XML parsing tag.</p>
 *
 * @author Shawn Bayern
 */
public abstract class ParseSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected Object xml;                          // 'xml' attribute
    protected String systemId;                     // 'systemId' attribute
    protected XMLFilter filter;			   // 'filter' attribute

    //*********************************************************************
    // Private state

    private String var;                            // 'var' attribute
    private String varDom;			   // 'varDom' attribute
    private int scope;				   // processed 'scope' attr
    private int scopeDom;			   // processed 'scopeDom' attr

    // state in support of XML parsing...
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private TransformerFactory tf;
    private TransformerHandler th;


    //*********************************************************************
    // Constructor and initialization

    public ParseSupport() {
	super();
	init();
    }

    private void init() {
	var = varDom = null;
	xml = null;
	systemId = null;
	filter = null;
	dbf = null;
	db = null;
	tf = null;
	th = null;
	scope = PageContext.PAGE_SCOPE;
	scopeDom = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    // parse 'source' or body, storing result in 'var'
    public int doEndTag() throws JspException {
      try {
	
	// set up our DocumentBuilder
        if (dbf == null) {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
        }
        db = dbf.newDocumentBuilder();

	// if we've gotten a filter, set up a transformer to support it
	if (filter != null) {
            if (tf == null)
                tf = TransformerFactory.newInstance();
            if (!tf.getFeature(SAXTransformerFactory.FEATURE))
                throw new JspTagException(
		    Resources.getMessage("PARSE_NO_SAXTRANSFORMER"));
            SAXTransformerFactory stf = (SAXTransformerFactory) tf;
            th = stf.newTransformerHandler();
	}

	// produce a Document by parsing whatever the attributes tell us to use
	Document d;
	Object xmlText = this.xml;
	if (xmlText == null) {
	    // if the attribute was specified, use the body as 'xml'
	    if (bodyContent != null && bodyContent.getString() != null)
		xmlText = bodyContent.getString().trim();
	    else
		xmlText = "";
	}
	if (xmlText instanceof String)
	    d = parseStringWithFilter((String) xmlText, filter);
	else if (xmlText instanceof Reader)
	    d = parseReaderWithFilter((Reader) xmlText, filter);
	else
	    throw new JspTagException(
	        Resources.getMessage("PARSE_INVALID_SOURCE"));

	// we've got a Document object; store it out as appropriate
	// (let any exclusivity or other constraints be enforced by TEI/TLV)
	if (var != null)
	    pageContext.setAttribute(var, d, scope);
	if (varDom != null)
	    pageContext.setAttribute(varDom, d, scopeDom);

	return EVAL_PAGE;
      } catch (SAXException ex) {
	throw new JspException(ex);
      } catch (IOException ex) {
	throw new JspException(ex);
      } catch (ParserConfigurationException ex) {
	throw new JspException(ex);
      } catch (TransformerConfigurationException ex) {
	throw new JspException(ex);
      }
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Private utility methods

    /** Parses the given InputSource after, applying the given XMLFilter. */
    private Document parseInputSourceWithFilter(InputSource s, XMLFilter f)
            throws SAXException, IOException {
	if (f != null) {
            // prepare an output Document
            Document o = db.newDocument();

            // use TrAX to adapt SAX events to a Document object
            th.setResult(new DOMResult(o));
            XMLReader xr = XMLReaderFactory.createXMLReader();
	    xr.setEntityResolver(new JstlEntityResolver(pageContext));
            //   (note that we overwrite the filter's parent.  this seems
            //    to be expected usage.  we could cache and reset the old
            //    parent, but you can't setParent(null), so this wouldn't
            //    be perfect.)
            f.setParent(xr);
            f.setContentHandler(th);
            f.parse(s);
            return o;
	} else
	    return parseInputSource(s);	
    }

    /** Parses the given Reader after applying the given XMLFilter. */
    private Document parseReaderWithFilter(Reader r, XMLFilter f)
            throws SAXException, IOException {
	return parseInputSourceWithFilter(new InputSource(r), f);
    }

    /** Parses the given String after applying the given XMLFilter. */
    private Document parseStringWithFilter(String s, XMLFilter f)
            throws SAXException, IOException {
        StringReader r = new StringReader(s);
        return parseReaderWithFilter(r, f);
    }

    /** Parses the given Reader after applying the given XMLFilter. */
    private Document parseURLWithFilter(String url, XMLFilter f)
            throws SAXException, IOException {
	return parseInputSourceWithFilter(new InputSource(url), f);
    }

    /** Parses the given InputSource into a Document. */
    private Document parseInputSource(InputSource s)
	    throws SAXException, IOException {
	db.setEntityResolver(new JstlEntityResolver(pageContext));

        // normalize URIs so they can be processed consistently by resolver
        if (systemId == null)
            s.setSystemId("jstl:");
	else if (ImportSupport.isAbsoluteUrl(systemId))
            s.setSystemId(systemId);
        else
            s.setSystemId("jstl:" + systemId);
	return db.parse(s);
    }

    /** Parses the given Reader into a Document. */
    private Document parseReader(Reader r) throws SAXException, IOException {
        return parseInputSource(new InputSource(r));
    }

    /** Parses the given String into a Document. */
    private Document parseString(String s) throws SAXException, IOException {
        StringReader r = new StringReader(s);
        return parseReader(r);
    }

    /** Parses the URL (passed as a String) into a Document. */
    private Document parseURL(String url) throws SAXException, IOException {
	return parseInputSource(new InputSource(url));
    }

    //*********************************************************************
    // JSTL-specific EntityResolver class

    /** Lets us resolve relative external entities. */
    public static class JstlEntityResolver implements EntityResolver {
	private final PageContext ctx;
        public JstlEntityResolver(PageContext ctx) {
            this.ctx = ctx;
        }
        public InputSource resolveEntity(String publicId, String systemId)
	        throws FileNotFoundException {
	    // pass if we don't have a systemId
	    if (systemId == null)
		return null;

	    // strip leading "jstl:" off URL if applicable
	    if (systemId.startsWith("jstl:"))
		systemId = systemId.substring(5);

	    // we're only concerned with relative URLs
	    if (ImportSupport.isAbsoluteUrl(systemId))
		return null;

	    // for relative URLs, load and wrap the resource.
	    // don't bother checking for 'null' since we specifically want
	    // the parser to fail if the resource doesn't exist
	    InputStream s;
	    if (systemId.startsWith("/")) {
	        s = ctx.getServletContext().getResourceAsStream(systemId);
	        if (s == null)
		    throw new FileNotFoundException(
			Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY",
			 systemId));
	    } else {
		String pagePath =
		    ((HttpServletRequest) ctx.getRequest()).getServletPath();
		String basePath =
		    pagePath.substring(0, pagePath.lastIndexOf("/"));
		s = ctx.getServletContext().getResourceAsStream(
		      basePath + "/" + systemId);
	        if (s == null)
		    throw new FileNotFoundException(
			Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY",
			 systemId));
	    }
	    return new InputSource(s);
        }
    }

    //*********************************************************************
    // Tag attributes

    public void setVar(String var) {
	this.var = var;
    }

    public void setVarDom(String varDom) {
	this.varDom = varDom;
    }

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }

    public void setScopeDom(String scopeDom) {
	this.scopeDom = Util.getScope(scopeDom);
    }
}
