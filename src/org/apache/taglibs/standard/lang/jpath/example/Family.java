/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.taglibs.standard.lang.jpath.example;




public class Family {
	private int id;
	private Person[] children = null ;
	private Person husband = null ;
	private Person wife = null ;
	private Event marriage;

        public Family(int id,
                 Person husband,
                 Person wife, 
                 Event marriage,
                 Person[] children) {
	    this.id = id;
	    this.husband = husband;
	    this.wife = wife;
	    this.marriage = marriage;
	    this.children = children;
        }

	public int getId() {
		return id;
	}

	public Person getHusband() {
		return husband;
	}

	public Person getWife() {
		return wife;
	}

	public Event getMarriage() {
		return marriage;
	}

	public Person[] getChildren() {
		return children;
	}

}
