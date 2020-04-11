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

package org.apache.taglibs.standard.examples.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Object that represents a Customer.
 *
 * @author Pierre Delisle
 */

public class Customer {

    //*********************************************************************
    // Instance variables

    /**
     * Holds value of property key.
     */
    int key;

    /**
     * Holds value of property lastName.
     */
    private String lastName;

    /**
     * Holds value of property firstName.
     */
    private String firstName;

    /**
     * Holds value of property birthDate.
     */
    private Date birthDate;

    /**
     * Holds value of property address.
     */
    private Address address;

    /**
     * Holds value of property phoneHome.
     */
    private String phoneHome;

    /**
     * Holds value of property phoneCell.
     */
    private String phoneCell;

    static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    //*********************************************************************
    // Constructors

    public Customer() {
    }

    public Customer(int key,
                    String lastName,
                    String firstName,
                    Date birthDate,
                    Address address,
                    String phoneHome,
                    String phoneCell) {
        init(key, lastName, firstName, birthDate, address, phoneHome, phoneCell);
    }

    public void init(int key,
                     String lastName,
                     String firstName,
                     Date birthDate,
                     Address address,
                     String phoneHome,
                     String phoneCell) {
        setKey(key);
        setLastName(lastName);
        setFirstName(firstName);
        setBirthDate(birthDate);
        setAddress(address);
        setPhoneHome(phoneHome);
        setPhoneCell(phoneCell);
    }

    //*********************************************************************
    // Properties

    /**
     * Getter for property key.
     *
     * @return Value of property key.
     */
    public int getKey() {
        return key;
    }

    /**
     * Setter for property key.
     *
     * @param key New value of property key.
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * Getter for property lastName.
     *
     * @return Value of property lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for property lastName.
     *
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for property firstName.
     *
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for property firstName.
     *
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for property birthDate.
     *
     * @return Value of property birthDate.
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Setter for property birthDate.
     *
     * @param birthDate New value of property birthDate.
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Getter for property address.
     *
     * @return Value of property address.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Setter for property address.
     *
     * @param address New value of property address.
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Getter for property phoneHome.
     *
     * @return Value of property phoneHome.
     */
    public String getPhoneHome() {
        return phoneHome;
    }

    /**
     * Setter for property phoneHome.
     *
     * @param phoneHome New value of property phoneHome.
     */
    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    /**
     * Getter for property phoneCell.
     *
     * @return Value of property phoneCell.
     */
    public String getPhoneCell() {
        return phoneCell;
    }

    /**
     * Setter for property phoneCell.
     *
     * @param phoneCell New value of property phoneCell.
     */
    public void setPhoneCell(String phoneCell) {
        this.phoneCell = phoneCell;
    }

    //*********************************************************************
    // Utility Methods

    /**
     * Return a String representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(key).append("] ");
        sb.append(getLastName()).append(", ");
        sb.append(getFirstName()).append("  ");
        sb.append(df.format(getBirthDate())).append("  ");
        sb.append(getAddress()).append("  ");
        if (getPhoneHome() != null) {
            sb.append(getPhoneHome()).append("  ");
        }
        if (getPhoneCell() != null) {
            sb.append(getPhoneCell());
        }
        return (sb.toString());
    }
}

