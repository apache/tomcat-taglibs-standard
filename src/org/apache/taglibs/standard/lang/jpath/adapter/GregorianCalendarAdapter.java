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

package org.apache.taglibs.standard.lang.jpath.adapter;

import java.util.GregorianCalendar;

public class GregorianCalendarAdapter implements JSPDate {

    private GregorianCalendar calendar;

    public GregorianCalendarAdapter(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public GregorianCalendarAdapter(int year, int month, int day) {
        calendar = new GregorianCalendar(year, month, day);
    }

    public GregorianCalendarAdapter(int year, int month, int day, int hour,
            int minute) { 
        calendar = new GregorianCalendar(year, month, day, hour, minute);
    }

    public GregorianCalendarAdapter(int year, int month, int day, int hour,
            int minute, int second) { 
        calendar = new GregorianCalendar(year, month, day, hour, minute, second);
    }

    public void roll(int field, int amount) {
        calendar.roll(field, amount);
    }

    public void add(int field, int amount) {
        calendar.add(field, amount);
    }

    public Double getTime() {
        return new Double(calendar.getTime().getTime());
    }

    public static Object adapt(Object o) {
        GregorianCalendarAdapter adapter = new GregorianCalendarAdapter((GregorianCalendar)o);
        return adapter;
    }

    public static Class[] getAdaptedClasses() {
        Class[] adaptedClasses = {GregorianCalendar.class};
        return adaptedClasses;
    }

}
