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

package org.apache.taglibs.standard.examples.startup;

import java.util.Enumeration;
import java.util.Hashtable;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.taglibs.standard.examples.beans.Customers;

/**
 * Initialization class. Builds all the data structures
 * used in the "examples" webapp.
 *
 * @author Pierre Delisle
 */
public class Init implements ServletContextListener {

    //*********************************************************************
    // ServletContextListener methods

    // recovers the one context parameter we need

    public void contextInitialized(ServletContextEvent sce) {
        //p("contextInitialized");
        init(sce);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        //p("contextInitialized");
    }

    //*********************************************************************
    // Initializations

    private void init(ServletContextEvent sce) {
        /*
         *  Customers
         */
        Customers.create("Richard", "Maurice", "5/15/35",
                "123 Chemin Royal", "Appt. #301",
                "Montreal", "QC", "H3J 9R9", "Canada");
        Customers.create("Mikita", "Stan", "12/25/47",
                "45 Fisher Blvd", "Suite 203",
                "Chicago", "IL", "65982", "USA", "(320)876-9784", null);
        Customers.create("Gilbert", "Rod", "3/11/51",
                "123 Main Street", "",
                "New-York City", "NY", "19432", "USA");
        Customers.create("Howe", "Gordie", "7/25/46",
                "7654 Wings Street", "",
                "Detroit", "MG", "07685", "USA", "(465)675-0761", "(465)879-9802");
        Customers.create("Sawchuk", "Terrie", "11/05/46",
                "12 Maple Leafs Avenue", "",
                "Toronto", "ON", "M5C 1Z1", "Canada");
        sce.getServletContext().setAttribute("customers", Customers.findAll());

        /**
         * Array of primitives (int)
         */
        int[] intArray = new int[]{10, 20, 30, 40, 50};
        sce.getServletContext().setAttribute("intArray", intArray);

        /**
         * Array of Objects (String)
         */
        String[] stringArray = new String[]{
                "A first string",
                "La deuxieme string",
                "Ella troisiemo stringo",
        };
        sce.getServletContext().setAttribute("stringArray", stringArray);

        /**
         * String-keyed Map
         */
        Hashtable stringMap = new Hashtable();
        sce.getServletContext().setAttribute("stringMap", stringMap);
        stringMap.put("one", "uno");
        stringMap.put("two", "dos");
        stringMap.put("three", "tres");
        stringMap.put("four", "cuatro");
        stringMap.put("five", "cinco");
        stringMap.put("six", "seis");
        stringMap.put("seven", "siete");
        stringMap.put("eight", "ocho");
        stringMap.put("nine", "nueve");
        stringMap.put("ten", "diez");

        /**
         * Integer-keyed Map
         */
        // we use a Hashtable so we can get an Enumeration easily, below
        Hashtable numberMap = new Hashtable();
        sce.getServletContext().setAttribute("numberMap", numberMap);
        numberMap.put(new Integer(1), "uno");
        numberMap.put(new Integer(2), "dos");
        numberMap.put(new Integer(3), "tres");
        numberMap.put(new Integer(4), "cuatro");
        numberMap.put(new Integer(5), "cinco");
        numberMap.put(new Integer(6), "seis");
        numberMap.put(new Integer(7), "siete");
        numberMap.put(new Integer(8), "ocho");
        numberMap.put(new Integer(9), "nueve");
        numberMap.put(new Integer(10), "diez");

        /**
         * Enumeration
         */
        Enumeration enum_ = numberMap.keys();
        // don't use 'enum' for attribute name because it is a
        // reserved word in EcmaScript.
        sce.getServletContext().setAttribute("enumeration", enum_);

        /**
         * Message arguments for parametric replacement
         */
        Object[] serverInfoArgs =
                new Object[]{
                        sce.getServletContext().getServerInfo(),
                        System.getProperty("java.version")
                };
        sce.getServletContext().setAttribute("serverInfoArgs", serverInfoArgs);
    }

    //*********************************************************************
    // Initializations

    private void p(String s) {
        System.out.println("[Init] " + s);
    }
}
