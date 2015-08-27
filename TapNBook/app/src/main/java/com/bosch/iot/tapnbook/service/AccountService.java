package com.bosch.iot.tapnbook.service;

import android.content.Context;

import com.bosch.iot.tapnbook.model.Account;

/**
 * Interface to handle Account Read/Write/Store service.
 */
public interface AccountService {
    Account readAccountFromStorage();

    boolean writeAccountToStorage(Account account);
}
