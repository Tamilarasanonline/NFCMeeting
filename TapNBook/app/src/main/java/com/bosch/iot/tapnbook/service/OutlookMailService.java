package com.bosch.iot.tapnbook.service;

import com.bosch.iot.tapnbook.model.Account;

import java.net.URI;
import java.net.URISyntaxException;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class OutlookMailService implements MailService {

    private static final String OUTLOOK_EWS_URL = "https://imb-exchange.bosch-si.com/ews/exchange.asmx";
    private Account user;

    public OutlookMailService(Account userAccount) {
        this.user = userAccount;
    }

    private ExchangeService getService() throws URISyntaxException {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        ExchangeCredentials credentials = new WebCredentials(user.getUserID(), user.getPassword());
        service.setCredentials(credentials);
        service.setUrl(new URI(OUTLOOK_EWS_URL));
        return service;
    }



}
