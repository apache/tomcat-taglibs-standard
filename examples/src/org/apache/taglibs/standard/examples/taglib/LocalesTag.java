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

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * <p>Tag handler for &lt;locales&gt;
 *
 * @author Felipe Leme <jstl@felipeal.net>
 * @version $Revision$ $Date$
 */
public class LocalesTag extends LoopTagSupport {

    private static final Locale[] locales = Locale.getAvailableLocales();
    private int pointer; 
    private String varTotal;
    private String endEL;
    private String beginEL;

    public void setVarTotal( String value ) {
	varTotal = value;
    }
    public void prepare() {
	pointer = 0;
	try {
	    begin = ( (Integer) ExpressionEvaluatorManager.evaluate( "begin", beginEL, Integer.class,
								     this, super.pageContext )).intValue();
	    end = ( (Integer) ExpressionEvaluatorManager.evaluate( "end", endEL, Integer.class,
								   this, super.pageContext )).intValue();
	} catch( JspException exc ) {
	    System.err.println( "Exception evaluating  EL expressions: beginEL = " + beginEL +
				", endEL = " + endEL );
	    begin = end = -1;
	    exc.printStackTrace();
	}
	if ( varTotal!=null && varTotal.length()>0 ) {
	    pageContext.setAttribute( varTotal, new Integer(locales.length) );
	}
    
    } 
    public boolean hasNext() {
	return pointer < locales.length;
    }  
    public Object next() {
	return locales[ pointer++ ];
    }
  
    public void setBegin( String value ) {
  	beginEL = value;
    }
  
    public void setEnd( String value ) {
  	endEL = value;
    }                     
}
