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


import java.util.ArrayList;
import java.util.Collection;


public class Person {
	private int id;
	private String firstName = "" ;
	private String lastName = "" ;
	private String gender = "" ;
	private Collection spouseFamilies;
	private Collection childFamilies;
	private Person father = null ;
	private Event birth;
	private Event death;
	private Event burial;
	private String note;

        public Person(int id, String firstName, 
                 String lastName, 
                 String gender, 
                 Event birth, 
                 Event death,
                 Event burial,
                 String note) {
	    this.id = id;
	    this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
	    this.spouseFamilies = new ArrayList();
	    this.childFamilies = new ArrayList();
	    this.birth = birth;
	    this.death = death;
	    this.burial = burial;
	    this.note = note;
        }

	public int getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getGender() {
		return gender;
	}

	public Event getBirth() {
		return birth;
	}

	public Event getDeath() {
		return death;
	}

	public Event getBurial() {
		return burial;
	}

	public String getNote() {
		return note;
	}

	public Collection getSpouseFamilies() {
		return spouseFamilies;
	}

	public Collection getChildFamilies() {
		return childFamilies;
	}

	public void addSpouseFamily(Family spouseFamily) {
            spouseFamilies.add(spouseFamily);
	}

	public void addChildFamily(Family childFamily) {
            childFamilies.add(childFamily);
	}

}
