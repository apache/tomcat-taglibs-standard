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

package org.apache.taglibs.standard.examples.taglib;

import java.io.IOException;

import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.examples.beans.Customer;

/**
 * <p>Tag handler for &lt;customerFmt&gt;
 *
 * @author Pierre Delisle
 */
public class CustomerFmtTag extends TagSupport {

    //*********************************************************************
    // Instance variables

    /**
     * Holds value of property customer.
     */
    private Customer customer;

    /**
     * Holds value of property fmt.
     */
    private String fmt;

    //*********************************************************************
    // Constructors

    public CustomerFmtTag() {
        super();
        init();
    }

    private void init() {
        customer = null;
        fmt = null;
    }

    //*********************************************************************
    // TagSupport methods

    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if (fmt.equalsIgnoreCase("short")) {
                out.println(customer.getFirstName() + " " +
                        customer.getLastName());
            } else if (fmt.equalsIgnoreCase("long")) {
                out.println(customer.getFirstName() + " " +
                        customer.getLastName() + " " + customer.getAddress());
            } else {
                out.println("invalid format");
            }
        } catch (IOException ex) {
        }

        return SKIP_BODY;
    }

    // Releases any resources we may have (or inherit)

    public void release() {
        super.release();
        init();
    }

    //*********************************************************************
    // Accessors

    /**
     * Getter for property customer.
     *
     * @return Value of property customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Setter for property customer.
     *
     * @param customer New value of property customer.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Getter for property fmt.
     *
     * @return Value of property fmt.
     */
    public String getFmt() {
        return fmt;
    }

    /**
     * Setter for property fmt.
     *
     * @param fmt New value of property fmt.
     */
    public void setFmt(String fmt) {
        this.fmt = fmt;
    }
}
