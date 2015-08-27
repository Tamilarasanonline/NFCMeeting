package com.bosch.iot.tapnbook.model;

/**
 * Microsoft User Account Details.
 */
public class OutLookUserAccount implements Account {

    private String userID;
    private String password;
    private String fullName;
    private String gmailId;
    private Type accountType;

    public OutLookUserAccount(String userID, String password, String fullName, String gmailId) {
        this.userID = userID;
        this.password = password;
        this.fullName = fullName;
        this.gmailId = gmailId;
    }

    public OutLookUserAccount(String fromCSV) {
        String[] values = fromCSV.split(",");
        this.userID = values[0];
        this.password = values[1];
        this.fullName = values[2];
        this.gmailId = values[3];
        this.accountType= Type.valueOf(values[4]);
    }

    @Override
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getGmailId() {
        return gmailId;
    }

    public void setGmailId(String gmailId) {
        this.gmailId = gmailId;
    }

    @Override
    public Type getAccountType() {
        return this.accountType;
    }

    public void setAccountType(Type accountType) {
        this.accountType = accountType;
    }

    @Override
    public String getCSVText() {
        return this.userID + ","
                + this.password + ","
                + this.fullName + ","
                + this.accountType + ",";
    }

}
