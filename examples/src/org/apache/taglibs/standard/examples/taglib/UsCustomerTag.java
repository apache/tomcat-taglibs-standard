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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import org.apache.taglibs.standard.examples.beans.Customer;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * <p>Tag handler for &lt;usCustomer&gt;
 *
 * @author Pierre Delisle
 * @version $Revision$ $Date$
 */
public class UsCustomerTag extends ConditionalTagSupport {
    
    //*********************************************************************
    // Instance Variables
    
    /** Holds value of property customer. */
    private String customer;
    
    //*********************************************************************
    // Constructor and lusCustomerecycle management
    
    public UsCustomerTag() {
        super();
        init();
    }
    
    private void init() {
        customer = null;
    }
    
    //*********************************************************************
    // TagSupport methods
    
    public void release() {
        super.release();
        init();
    }
    
    //*********************************************************************
    // ConditionalTagSupport methods
    
    protected boolean condition() throws JspTagException {
	try {
         Customer customerObj = (Customer)eval("customer", customer, Customer.class);
         if (customerObj == null) {
             throw new NullAttributeException("usCustomer", "test");
         } else {
             System.out.println("country: " + customerObj.getAddress().getCountry());
             return (customerObj.getAddress().getCountry().equalsIgnoreCase("USA"));
         }
        } catch (JspException ex) {
	 throw new JspTagException(ex.toString());
	}
    }
    
    //*********************************************************************
    // Accessors
    
    /**
     * Getter for property customer.
     * @return Value of property customer.
     */
    public String getCustomer() {
        return customer;
    }
    
    /**
     * Setter for property customer.
     * @param customer New value of property customer.
     */
    public void setCustomer(String customer) {
        this.customer = customer;
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
            throw new NullAttributeException(attName, attValue);
        } else {
            return obj;
        }
    }
}
