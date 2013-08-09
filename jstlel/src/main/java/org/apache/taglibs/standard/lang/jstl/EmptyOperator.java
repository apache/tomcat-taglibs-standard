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


package org.apache.taglibs.standard.lang.jstl;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


/**
 * <p>The implementation of the empty operator
 *
 * @author Nathan Abramson - Art Technology Group
 */


public class EmptyOperator

        extends UnaryOperator

{

    //-------------------------------------

    // Singleton

    //-------------------------------------


    public static final EmptyOperator SINGLETON =

            new EmptyOperator();


    //-------------------------------------

    /**
     * Constructor
     */

    public EmptyOperator()

    {

    }


    //-------------------------------------

    // Expression methods

    //-------------------------------------

    /**
     * Returns the symbol representing the operator
     */

    public String getOperatorSymbol()

    {

        return "empty";

    }


    //-------------------------------------

    /**
     * Applies the operator to the given value
     */

    public Object apply(Object pValue,

                        Object pContext,

                        Logger pLogger)

            throws ELException

    {

        // See if the value is null

        if (pValue == null) {

            return PrimitiveObjects.getBoolean(true);

        }


        // See if the value is a zero-length String

        else if ("".equals(pValue)) {

            return PrimitiveObjects.getBoolean(true);

        }


        // See if the value is a zero-length array

        else if (pValue.getClass().isArray() &&

                Array.getLength(pValue) == 0) {

            return PrimitiveObjects.getBoolean(true);

        }


        // See if the value is an empty Collection

        else if (pValue instanceof Collection &&

                ((Collection) pValue).isEmpty()) {

            return PrimitiveObjects.getBoolean(true);

        }


        // See if the value is an empty Map

        else if (pValue instanceof Map &&

                ((Map) pValue).isEmpty()) {

            return PrimitiveObjects.getBoolean(true);

        }


        // Otherwise, not empty

        else {

            return PrimitiveObjects.getBoolean(false);

        }

    }


    //-------------------------------------

}

