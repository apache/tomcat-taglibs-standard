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

package org.apache.taglibs.standard.tag.common.core;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.standard.resources.Resources;

/**
 * <p>Support for tag handlers for &lt;import&gt;, the general-purpose
 * text-importing mechanism for JSTL 1.0.  The rtexprvalue and expression-
 * evaluating libraries each have handlers that extend this class.</p>
 *
 * @author Shawn Bayern
 */

public abstract class ImportSupport extends BodyTagSupport 
        implements TryCatchFinally, ParamParent {

    //*********************************************************************
    // Public constants
    
    /** <p>Valid characters in a scheme.</p>
     *  <p>RFC 1738 says the following:</p>
     *  <blockquote>
     *   Scheme names consist of a sequence of characters. The lower
     *   case letters "a"--"z", digits, and the characters plus ("+"),
     *   period ("."), and hyphen ("-") are allowed. For resiliency,
     *   programs interpreting URLs should treat upper case letters as
     *   equivalent to lower case in scheme names (e.g., allow "HTTP" as
     *   well as "http").
     *  </blockquote>
     * <p>We treat as absolute any URL that begins with such a scheme name,
     * followed by a colon.</p>
     */
    public static final String VALID_SCHEME_CHARS =
	"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

    /** Default character encoding for response. */
    public static final String DEFAULT_ENCODING = "ISO-8859-1";

    //*********************************************************************
    // Protected state

    protected String url;                         // 'url' attribute
    protected String context;			  // 'context' attribute
    protected String charEncoding;                // 'charEncoding' attrib.

    //*********************************************************************
    // Private state (implementation details)

    private String var;                 // 'var' attribute
    private int scope;			// processed 'scope' attribute
    private String varReader;           // 'varReader' attribute
    private Reader r;	 		// exposed reader, if relevant
    private boolean isAbsoluteUrl;	// is our URL absolute?
    private ParamSupport.ParamManager params;    // parameters
    private String urlWithParams;	// URL with parameters, if applicable

    //*********************************************************************
    // Constructor and initialization

    public ImportSupport() {
	super();
	init();
    }

    private void init() {
	url = var = varReader = context = charEncoding = urlWithParams = null;
	params = null;
        scope = PageContext.PAGE_SCOPE;
    }


    //*********************************************************************
    // Tag logic

    // determines what kind of import and variable exposure to perform 
    public int doStartTag() throws JspException {
	// Sanity check
	if (context != null
	        && (!context.startsWith("/") || !url.startsWith("/"))) {
	    throw new JspTagException(
		Resources.getMessage("IMPORT_BAD_RELATIVE"));
	}

	// reset parameter-related state
	urlWithParams = null;
	params = new ParamSupport.ParamManager();

	// check the URL
	if (url == null || url.equals(""))
	    throw new NullAttributeException("import", "url");

	// Record whether our URL is absolute or relative
	isAbsoluteUrl = isAbsoluteUrl();

	try {
	    // If we need to expose a Reader, we've got to do it right away
	    if  (varReader != null) {
	        r = acquireReader();
	        pageContext.setAttribute(varReader, r);
	    }
	} catch (IOException ex) {
	    throw new JspTagException(ex.toString());
	}

	return EVAL_BODY_INCLUDE;
    }

    // manages connections as necessary (creating or destroying)
    public int doEndTag() throws JspException {
        try {
	    // If we didn't expose a Reader earlier...
	    if (varReader == null) {
	        // ... store it in 'var', if available ...
	        if (var != null)
	            pageContext.setAttribute(var, acquireString(), scope);
                // ... or simply output it, if we have nowhere to expose it
	        else
	            pageContext.getOut().print(acquireString());
	    }
	    return EVAL_PAGE;
        } catch (IOException ex) {
	    throw new JspTagException(ex.toString());
        }
    }

    // simply rethrows its exception
    public void doCatch(Throwable t) throws Throwable {
	throw t;
    }

    // cleans up if appropriate
    public void doFinally() { 
        try {
	    // If we exposed a Reader in doStartTag(), close it.
	    if (varReader != null) {
		// 'r' can be null if an exception was thrown...
	        if (r != null)
		    r.close();
		pageContext.removeAttribute(varReader, PageContext.PAGE_SCOPE);
	    }
        } catch (IOException ex) {
	    // ignore it; close() failed, but there's nothing more we can do
        }
    }

    // Releases any resources we may have (or inherit)
    public void release() {
	init();
        super.release();
    }

    //*********************************************************************
    // Tag attributes known at translation time

    public void setVar(String var) {
	this.var = var;
    }

    public void setVarReader(String varReader) {
	this.varReader = varReader;
    }

    public void setScope(String scope) {
	this.scope = Util.getScope(scope);
    }


    //*********************************************************************
    // Collaboration with subtags

    // inherit Javadoc
    public void addParameter(String name, String value) {
	params.addParameter(name, value);
    }

    //*********************************************************************
    // Actual URL importation logic

    /*
     * Overall strategy:  we have two entry points, acquireString() and
     * acquireReader().  The latter passes data through unbuffered if
     * possible (but note that it is not always possible -- specifically
     * for cases where we must use the RequestDispatcher.  The remaining
     * methods handle the common.core logic of loading either a URL or a local
     * resource.
     *
     * We consider the 'natural' form of absolute URLs to be Readers and
     * relative URLs to be Strings.  Thus, to avoid doing extra work,
     * acquireString() and acquireReader() delegate to one another as
     * appropriate.  (Perhaps I could have spelled things out more clearly,
     * but I thought this implementation was instructive, not to mention
     * somewhat cute...)
     */

    private String acquireString() throws IOException, JspException {
	if (isAbsoluteUrl) {
	    // for absolute URLs, delegate to our peer
	    BufferedReader r = new BufferedReader(acquireReader());
	    StringBuffer sb = new StringBuffer();
	    int i;

	    // under JIT, testing seems to show this simple loop is as fast
	    // as any of the alternatives
	    while ((i = r.read()) != -1)
	        sb.append((char)i);

	    return sb.toString();
	} else { 
	    // handle relative URLs ourselves

	    // URL is relative, so we must be an HTTP request
	    if (!(pageContext.getRequest() instanceof HttpServletRequest
		  && pageContext.getResponse() instanceof HttpServletResponse))
		throw new JspTagException(
		    Resources.getMessage("IMPORT_REL_WITHOUT_HTTP"));

	    // retrieve an appropriate ServletContext
	    ServletContext c = null;
	    String targetUrl = targetUrl();
	    if (context != null)
	        c = pageContext.getServletContext().getContext(context);
	    else {
	        c = pageContext.getServletContext();

		// normalize the URL if we have an HttpServletRequest
		if (!targetUrl.startsWith("/")) {
		    String sp = ((HttpServletRequest) 
			pageContext.getRequest()).getServletPath();
		    targetUrl = sp.substring(0, sp.lastIndexOf('/'))
			+ '/' + targetUrl;
		}
	    }

            if (c == null) {
                throw new JspTagException(
                    Resources.getMessage(
                        "IMPORT_REL_WITHOUT_DISPATCHER", context, targetUrl));
            }

	    // from this context, get a dispatcher
	    RequestDispatcher rd =
                c.getRequestDispatcher(stripSession(targetUrl));
	    if (rd == null)
		throw new JspTagException(stripSession(targetUrl));

	    // include the resource, using our custom wrapper
	    ImportResponseWrapper irw = 
		new ImportResponseWrapper(
		    (HttpServletResponse) pageContext.getResponse());

	    // spec mandates specific error handling form include()
	    try {
	        rd.include(pageContext.getRequest(), irw);
	    } catch (IOException ex) {
		throw new JspException(ex);
	    } catch (RuntimeException ex) {
		throw new JspException(ex);
	    } catch (ServletException ex) {
		Throwable rc = ex.getRootCause();
		if (rc == null)
		    throw new JspException(ex);
		else
		    throw new JspException(rc);
	    }

	    // disallow inappropriate response codes per JSTL spec
	    if (irw.getStatus() < 200 || irw.getStatus() > 299) {
		throw new JspTagException(irw.getStatus() + " " +
		    stripSession(targetUrl));
	    }

	    // recover the response String from our wrapper
	    return irw.getString();
	}
    }

    private Reader acquireReader() throws IOException, JspException {
	if (!isAbsoluteUrl) {
	    // for relative URLs, delegate to our peer
	    return new StringReader(acquireString());
	} else {
            String target = targetUrl();
	    try {
	        // handle absolute URLs ourselves, using java.net.URL
	        URL u = new URL(target);
                URLConnection uc = u.openConnection();
                InputStream i = uc.getInputStream();

	        // okay, we've got a stream; encode it appropriately
	        Reader r = null;
	        if (charEncoding != null && !charEncoding.equals(""))
		    r = new InputStreamReader(i, charEncoding);
	        else {
		    String responseAdvisoryEncoding = uc.getContentEncoding();
		    if (responseAdvisoryEncoding != null)
		        r = new InputStreamReader(i, responseAdvisoryEncoding);
		    else
		        r = new InputStreamReader(i, DEFAULT_ENCODING);
	        }

		// check response code for HTTP URLs before returning, per spec,
		// before returning
		if (uc instanceof HttpURLConnection) {
		    int status = ((HttpURLConnection) uc).getResponseCode();
		    if (status < 200 || status > 299)
			throw new JspTagException(status + " " + target);
		}

	        return r;
	    } catch (IOException ex) {
		throw new JspException(
                    Resources.getMessage("IMPORT_ABS_ERROR", target, ex), ex);
	    } catch (RuntimeException ex) {  // because the spec makes us
		throw new JspException(
                    Resources.getMessage("IMPORT_ABS_ERROR", target, ex), ex);
	    }
	}
    }

    /** Wraps responses to allow us to retrieve results as Strings. */
    private class ImportResponseWrapper extends HttpServletResponseWrapper {

	//************************************************************
	// Overview

	/*
	 * We provide either a Writer or an OutputStream as requested.
	 * We actually have a true Writer and an OutputStream backing
	 * both, since we don't want to use a character encoding both
	 * ways (Writer -> OutputStream -> Writer).  So we use no
	 * encoding at all (as none is relevant) when the target resource
	 * uses a Writer.  And we decode the OutputStream's bytes
	 * using OUR tag's 'charEncoding' attribute, or ISO-8859-1
	 * as the default.  We thus ignore setLocale() and setContentType()
	 * in this wrapper.
	 *
	 * In other words, the target's asserted encoding is used
	 * to convert from a Writer to an OutputStream, which is typically
	 * the medium through with the target will communicate its
	 * ultimate response.  Since we short-circuit that mechanism
	 * and read the target's characters directly if they're offered
	 * as such, we simply ignore the target's encoding assertion.
	 */

	//************************************************************
	// Data

	/** The Writer we convey. */
	private StringWriter sw = new StringWriter();

	/** A buffer, alternatively, to accumulate bytes. */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	/** A ServletOutputStream we convey, tied to this Writer. */
	private ServletOutputStream sos = new ServletOutputStream() {
	    public void write(int b) throws IOException {
		bos.write(b);
	    }
	};

	/** 'True' if getWriter() was called; false otherwise. */
	private boolean isWriterUsed;

	/** 'True if getOutputStream() was called; false otherwise. */
	private boolean isStreamUsed;

	/** The HTTP status set by the target. */
	private int status = 200;
	
	//************************************************************
	// Constructor and methods

	/** Constructs a new ImportResponseWrapper. */
	public ImportResponseWrapper(HttpServletResponse response) {
	    super(response);
	}
	
	/** Returns a Writer designed to buffer the output. */
	public PrintWriter getWriter() {
	    if (isStreamUsed)
		throw new IllegalStateException(
		    Resources.getMessage("IMPORT_ILLEGAL_STREAM"));
	    isWriterUsed = true;
	    return new PrintWriter(sw);
	}
	
	/** Returns a ServletOutputStream designed to buffer the output. */
	public ServletOutputStream getOutputStream() {
	    if (isWriterUsed)
		throw new IllegalStateException(
		    Resources.getMessage("IMPORT_ILLEGAL_WRITER"));
	    isStreamUsed = true;
	    return sos;
	}

	/** Has no effect. */
	public void setContentType(String x) {
	    // ignore
	}

	/** Has no effect. */
	public void setLocale(Locale x) {
	    // ignore
	}

	public void setStatus(int status) {
	    this.status = status;
	}

	public int getStatus() {
	    return status;
	}

	/** 
	 * Retrieves the buffered output, using the containing tag's 
	 * 'charEncoding' attribute, or the tag's default encoding,
	 * <b>if necessary</b>.
         */
	// not simply toString() because we need to throw
	// UnsupportedEncodingException
	public String getString() throws UnsupportedEncodingException {
	    if (isWriterUsed)
		return sw.toString();
	    else if (isStreamUsed) {
		if (charEncoding != null && !charEncoding.equals(""))
		    return bos.toString(charEncoding);
		else
		    return bos.toString(DEFAULT_ENCODING);
	    } else
		return "";		// target didn't write anything
	}
    }

    //*********************************************************************
    // Some private utility methods

    /** Returns our URL (potentially with parameters) */
    private String targetUrl() {
	if (urlWithParams == null)
	    urlWithParams = params.aggregateParams(url);
	return urlWithParams;
    }

    /**
     * Returns <tt>true</tt> if our current URL is absolute,
     * <tt>false</tt> otherwise.
     */
    private boolean isAbsoluteUrl() throws JspTagException {
        return isAbsoluteUrl(url);
    }


    //*********************************************************************
    // Public utility methods

    /**
     * Returns <tt>true</tt> if our current URL is absolute,
     * <tt>false</tt> otherwise.
     */
    public static boolean isAbsoluteUrl(String url) {
	// a null URL is not absolute, by our definition
	if (url == null)
	    return false;

	// do a fast, simple check first
	int colonPos;
	if ((colonPos = url.indexOf(":")) == -1)
	    return false;

	// if we DO have a colon, make sure that every character
	// leading up to it is a valid scheme character
	for (int i = 0; i < colonPos; i++)
	    if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1)
		return false;

	// if so, we've got an absolute url
	return true;
    }

    /**
     * Strips a servlet session ID from <tt>url</tt>.  The session ID
     * is encoded as a URL "path parameter" beginning with "jsessionid=".
     * We thus remove anything we find between ";jsessionid=" (inclusive)
     * and either EOS or a subsequent ';' (exclusive).
     */
    public static String stripSession(String url) {
	StringBuffer u = new StringBuffer(url);
        int sessionStart;
        while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
            int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
            if (sessionEnd == -1)
		sessionEnd = u.toString().indexOf("?", sessionStart + 1);
	    if (sessionEnd == -1) 				// still
                sessionEnd = u.length();
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }
}
