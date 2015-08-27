package com.bosch.iot.tapnbook.model;

import java.io.Serializable;

/**
 * Interface to hold User account details.
 */
public interface Account extends Serializable {

    String STORED_FILE_NAME = "IDENTITY.key";

    String getUserID();

    String getPassword();

    String getFullName();

    String getGmailId();

    Type getAccountType();

    String getCSVText();
}
