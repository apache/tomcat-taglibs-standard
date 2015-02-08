/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.util.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

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
    protected XMLFilter filter;               // 'filter' attribute

    //*********************************************************************
    // Private state

    private String var;                            // 'var' attribute
    private String varDom;               // 'varDom' attribute
    private int scope;                   // processed 'scope' attr
    private int scopeDom;               // processed 'scopeDom' attr
    private XmlUtil.JstlEntityResolver entityResolver;


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
        scope = PageContext.PAGE_SCOPE;
        scopeDom = PageContext.PAGE_SCOPE;
    }

    //*********************************************************************
    // Tag logic

    // parse 'source' or body, storing result in 'var'

    @Override
    public int doEndTag() throws JspException {
        // produce a Document by parsing whatever the attributes tell us to use
        Object xmlText = this.xml;
        if (xmlText == null) {
            // if the attribute was specified, use the body as 'xml'
            if (bodyContent != null && bodyContent.getString() != null) {
                xmlText = bodyContent.getString().trim();
            } else {
                xmlText = "";
            }
        }
        if (xmlText instanceof String) {
            xmlText = new StringReader((String) xmlText);
        }
        if (!(xmlText instanceof Reader)) {
            throw new JspTagException(Resources.getMessage("PARSE_INVALID_SOURCE"));
        }
        InputSource source = XmlUtil.newInputSource(((Reader) xmlText), systemId);

        Document d;
        if (filter != null) {
            d = parseInputSourceWithFilter(source, filter);
        } else {
            d = parseInputSource(source);
        }

        // we've got a Document object; store it out as appropriate
        // (let any exclusivity or other constraints be enforced by TEI/TLV)
        if (var != null) {
            pageContext.setAttribute(var, d, scope);
        }
        if (varDom != null) {
            pageContext.setAttribute(varDom, d, scopeDom);
        }

        return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)

    @Override
    public void release() {
        init();
    }


    //*********************************************************************
    // Private utility methods

    /**
     * Parses the given InputSource after, applying the given XMLFilter.
     */
    private Document parseInputSourceWithFilter(InputSource s, XMLFilter f) throws JspException {
        try {
            XMLReader xr = XmlUtil.newXMLReader(entityResolver);
            //   (note that we overwrite the filter's parent.  this seems
            //    to be expected usage.  we could cache and reset the old
            //    parent, but you can't setParent(null), so this wouldn't
            //    be perfect.)
            f.setParent(xr);

            TransformerHandler th = XmlUtil.newTransformerHandler();
            Document o = XmlUtil.newEmptyDocument();
            th.setResult(new DOMResult(o));
            f.setContentHandler(th);

            f.parse(s);
            return o;
        } catch (IOException e) {
            throw new JspException(e);
        } catch (SAXException e) {
            throw new JspException(e);
        } catch (TransformerConfigurationException e) {
            throw new JspException(e);
        } catch (ParserConfigurationException e) {
            throw new JspException(e);
        }
    }

    /**
     * Parses the given InputSource into a Document.
     */
    private Document parseInputSource(InputSource s) throws JspException {
        try {
            DocumentBuilder db = XmlUtil.newDocumentBuilder();
            db.setEntityResolver(entityResolver);
            return db.parse(s);
        } catch (SAXException e) {
            throw new JspException(e);
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        entityResolver = pageContext == null ? null: new XmlUtil.JstlEntityResolver(pageContext);
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
