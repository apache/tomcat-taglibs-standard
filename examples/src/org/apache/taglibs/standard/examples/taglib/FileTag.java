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

package org.apache.taglibs.standard.examples.taglib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * <p>Tag handler for &lt;file&gt;
 *
 * @author Pierre Delisle
 * @version $Revision$ $Date$
 */
public class FileTag extends TagSupport {
    
    //*********************************************************************
    // Instance variables
    
    private String id;
    private String file;
    
    private Reader reader;
    
    //*********************************************************************
    // Constructors
    
    public FileTag() {
        super();
        init();
    }
    
    private void init() {
        id = null;
        file = null;
    }
    
    //*********************************************************************
    // Tag's properties
    
    /**
     * Tag's 'id' attribute
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Tag's 'file' attribute
     */
    public void setfile(String file) {
        this.file = file;
    }
    
    //*********************************************************************
    // TagSupport methods
    
    public int doStartTag() throws JspException {
        reader = getReaderFromFile((String)eval("file", file, String.class));
        exposeVariable(reader);
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        try {
            reader.close();
        } catch (IOException ex) {}
        reader = null;
        return EVAL_PAGE;
    }
    
    /**
     * Releases any files we may have (or inherit)
     */
    public void release() {
        super.release();
        init();
    }
    
    //*********************************************************************
    // Tag's specific behavior methods
    
    public Reader getReaderFromFile(String name) throws JspException {
        InputStream in = pageContext.getServletContext().
            getResourceAsStream(name);
        if (in == null) {
            throw new JspException("Could not access " + name);
        }

        return new InputStreamReader(in);
    }

    
    //*********************************************************************
    // Utility methods
    
    /**
     * Evaluate elexprvalue
     */
    private Object eval(String attName, String attValue, Class clazz)
    throws JspException {
        Object obj = ExpressionEvaluatorManager.evaluate(
        attName, attValue, clazz, this, pageContext);
        if (obj == null) {
            throw new NullAttributeException("file", attName);
        } else {
            return obj;
        }
    }
    
    private void exposeVariable(Reader reader) {
        if (id != null) {
            pageContext.setAttribute(id, reader);
        }
    }
}
