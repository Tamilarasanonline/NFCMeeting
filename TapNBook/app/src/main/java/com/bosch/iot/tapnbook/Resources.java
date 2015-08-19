package com.bosch.iot.tapnbook;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tamil on 13/8/2015.
 */
public enum Resources {
    ROOMT1("Meeting Room 4-1","d2i3jfur7v755d2c8306epvcoc@group.calendar.google.com", "Level 4 ,11 Bishan Street 21, Singapore 573943","Asia/Singapore"),
    ROOMT2("Meeting Room 4-2","vprf9j8amdldjdgssn0dfq6r5k@group.calendar.google.com","Level 4 ,11 Bishan Street 21, Singapore 573943","Asia/Singapore"),
    ROOMT3("Meeting Room 4-3","ls6t9je8fn302mf8lle2n2toss@group.calendar.google.com","Level 4 ,11 Bishan Street 21, Singapore 573943","Asia/Singapore"),
    ROOMT4("Meeting Room 4-4","plif11i6l1av0qsq2a15ro8o68@group.calendar.google.com","Level 4 ,11 Bishan Street 21, Singapore 573943","Asia/Singapore");

    private final List<String> values;

    Resources(String ...values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }

    public String getRoomName() {
        return values.get(0);
    }

    public String getCalenderId() {
        return values.get(1);
    }
    public String getLocation() {
        return values.get(2);
    }

    public String getTimeZone() {
        return values.get(3);
    }

}
