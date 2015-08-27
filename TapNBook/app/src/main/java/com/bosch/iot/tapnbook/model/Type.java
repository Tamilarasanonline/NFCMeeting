package com.bosch.iot.tapnbook.model;

/**
 * Account Type enum.
 */
public enum Type {
    GOOGLE("GOOGLE"), OUTLOOK("OUTLOOK");

    private final String name;

    private Type(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}
