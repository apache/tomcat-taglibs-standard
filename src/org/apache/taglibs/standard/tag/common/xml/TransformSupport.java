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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;
import org.w3c.dom.*;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;transform&gt;, the XML transformation
 * tag.</p>
 *
 * @author Shawn Bayern
 */
public abstract class TransformSupport extends BodyTagSupport {

    //*********************************************************************
    // Protected state

    protected Object xml;                       // attribute
    protected String xmlSystemId;		// attribute
    protected Object xslt;			// attribute
    protected String xsltSystemId;		// attribute
    protected Result result;			// attribute

    //*********************************************************************
    // Private state

    private String var;                            // 'var' attribute
    private int scope;				   // processed 'scope' attr
    private Transformer t;			   // actual Transformer
    private TransformerFactory tf;		   // reusable factory
    private DocumentBuilder db;			   // reusable factory
    private DocumentBuilderFactory dbf;		   // reusable factory


    //*********************************************************************
    // Constructor and initialization

    public TransformSupport() {
	super();
	init();
    }

    private void init() {
	xml = xslt = null;
	xmlSystemId = xsltSystemId = null;
	var = null;
	result = null;
	tf = null;
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    public int doStartTag() throws JspException {
      /*
       * We can set up our Transformer here, so we do so, and we let
       * it receive parameters directly from subtags (instead of
       * caching them.
       */
      try {

	//************************************
	// Initialize

	// set up the TransformerFactory if necessary
        if (tf == null)
            tf = TransformerFactory.newInstance();

	//************************************
	// Produce transformer

	Source s;
	if (xslt != null) {
	    s = getSource(xslt, xsltSystemId);
	} else {
	    throw new JspTagException(
	        Resources.getMessage("TRANSFORM_NO_TRANSFORMER"));
        }
	tf.setURIResolver(new JstlUriResolver(pageContext));
        t = tf.newTransformer(s);

	return EVAL_BODY_BUFFERED;

      } catch (SAXException ex) {
	throw new JspException(ex);
      } catch (ParserConfigurationException ex) {
	throw new JspException(ex);
      } catch (IOException ex) {
	throw new JspException(ex);
      } catch (TransformerConfigurationException ex) {
	throw new JspException(ex);
      }
    }

    // parse 'xml' or body, transform via our Transformer,
    // and store as 'var' or through 'result'
    public int doEndTag() throws JspException {
      try {

	//************************************
	// Initialize

	// set up our DocumentBuilderFactory if necessary
	if (dbf == null) {
	    dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
	} if (db == null)
	    db = dbf.newDocumentBuilder();

	//************************************
	// Determine source XML

	// if we haven't gotten a source, use the body (which may be empty)
	Object xml = this.xml;
	if (xml == null)				// still equal
	    if (bodyContent != null && bodyContent.getString() != null)
	        xml = bodyContent.getString().trim();
	    else
		xml = "";

	// let the Source be with you
	Source source = getSource(xml, xmlSystemId);

	//************************************
	// Conduct the transformation

	// we can assume at most one of 'var' or 'result' is specified
	if (result != null)
	    // we can write directly to the Result
	    t.transform(source, result);
	else if (var != null) {
	    // we need a Document
	    Document d = db.newDocument();
	    Result doc = new DOMResult(d);
	    t.transform(source, doc);
	    pageContext.setAttribute(var, d, scope);
	} else {
	    Result page =
		new StreamResult(new SafeWriter(pageContext.getOut()));
	    t.transform(source, page);
	}

	return EVAL_PAGE;
      } catch (SAXException ex) {
	throw new JspException(ex);
      } catch (ParserConfigurationException ex) {
	throw new JspException(ex);
      } catch (IOException ex) {
	throw new JspException(ex);
      } catch (TransformerException ex) {
	throw new JspException(ex);
      }
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
    }


    //*********************************************************************
    // Public methods for subtags

    /** Sets (adds) a transformation parameter on our transformer. */
    public void addParameter(String name, Object value) {
	t.setParameter(name, value);
    }


    //*********************************************************************
    // Utility methods

    /**
     * Retrieves a Source from the given Object, whether it be a String,
     * Reader, Node, or other supported types (even a Source already).
     * If 'url' is true, then we must be passed a String and will interpret
     * it as a URL.  A null input always results in a null output.
     */
    private Source getSource(Object o, String systemId)
	    throws SAXException, ParserConfigurationException, IOException {
	if (o == null)
	    return null;
        else if (o instanceof Source) {
	    return (Source) o;
        } else if (o instanceof String) {
	    // if we've got a string, chain to Reader below
	    return getSource(new StringReader((String) o), systemId);
        } else if (o instanceof Reader) {
	    // Without a systemId, or with an absolute one, we can
	    // read the input through SAX.  If our systemId relative,
	    // however, JAXP seems to effectively require us to go through DOM.
	    // (TrAX resolves our relative URLs to full paths before
            // passing them to our resolver.)  This is silly, but what can
            // you do?  If this is a bug and not a feature of the TrAX
            // implementation, we can remove the DOM logic below, later.
	    if (systemId == null || ImportSupport.isAbsoluteUrl(systemId)) {
	        // explicitly go through SAX to maintain
	        // control over how relative external entities resolve
	        XMLReader xr = XMLReaderFactory.createXMLReader();
                xr.setEntityResolver(
                    new ParseSupport.JstlEntityResolver(pageContext));
	        InputSource s = new InputSource((Reader) o);
	        if (systemId != null)
                    s.setSystemId(systemId);
	        Source result = new SAXSource(xr, s);
	        return result;
	    } else {
		// go through DOM to maintain full control over entities
		InputSource s = new InputSource((Reader) o);
		s.setSystemId(systemId);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(
		    new ParseSupport.JstlEntityResolver(pageContext));
		return new DOMSource(db.parse(s));
	    }
        } else if (o instanceof Node) {
	    return new DOMSource((Node) o);
        } else if (o instanceof List) {
	    // support 1-item List because our XPath processor outputs them	
	    List l = (List) o;
	    if (l.size() == 1) {
	        return getSource(l.get(0), systemId);		// unwrap List
	    } else {
	        throw new IllegalArgumentException(
                  Resources.getMessage("TRANSFORM_SOURCE_INVALID_LIST"));
	    }
        } else {
	    throw new IllegalArgumentException(
	       Resources.getMessage("TRANSFORM_SOURCE_UNRECOGNIZED")
	         + o.getClass());
	}
    }


    //*********************************************************************
    // Tag attributes

    public void setVar(String var) {
	this.var = var;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Private utility classes

    /**
     * A Writer based on a wrapped Writer but ignoring requests to
     * close() and flush() it.  (Someone must have wrapped the
     * toilet in my office similarly...)
     */
    private static class SafeWriter extends Writer {
	private Writer w;
	public SafeWriter(Writer w) { this.w = w; }
	public void close() { }
	public void flush() { }
	public void write(char[] cbuf, int off, int len) throws IOException {
	    w.write(cbuf, off, len);
	}
    }	

    //*********************************************************************
    // JSTL-specific URIResolver class

    /** Lets us resolve relative external entities. */
    private static class JstlUriResolver implements URIResolver {
        private final PageContext ctx;
        public JstlUriResolver(PageContext ctx) {
            this.ctx = ctx;
        }
        public Source resolve(String href, String base)
	        throws TransformerException {
            // pass if we don't have a systemId
            if (href == null)
                return null;

            // we're only concerned with relative URLs
            if (ImportSupport.isAbsoluteUrl(href))
                return null;

            // for relative URLs, load and wrap the resource.
            // don't bother checking for 'null' since we specifically want
            // the parser to fail if the resource doesn't exist
            InputStream s;
            if (href.startsWith("/")) {
                s = ctx.getServletContext().getResourceAsStream(href);
                if (s == null)
                    throw new TransformerException(href);
            } else {
                String pagePath =
                    ((HttpServletRequest) ctx.getRequest()).getServletPath();
                String basePath =
                    pagePath.substring(0, pagePath.lastIndexOf("/"));
                s = ctx.getServletContext().getResourceAsStream(
                      basePath + "/" + href);
		if (s == null)
		    throw new TransformerException(href);
            }
            return new StreamSource(s);
        }
    }

}
