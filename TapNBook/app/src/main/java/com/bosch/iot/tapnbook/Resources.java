package com.bosch.iot.tapnbook;

/**
 * Created by Tamil on 13/8/2015.
 */
public enum Resources {
    ROOMT1("Meeting Room 4-1"),
    ROOMT2("Meeting Room 4-2"),
    ROOMT3("Meeting Room 4-3"),
    ROOMT4("Meeting Room 4-4");

    // Member to hold the name
    private String string;

    // constructor to set the string
    Resources(String name){string = name;}

    // the toString just returns the given name
    @Override
    public String toString() {
        return string;
    }
}
