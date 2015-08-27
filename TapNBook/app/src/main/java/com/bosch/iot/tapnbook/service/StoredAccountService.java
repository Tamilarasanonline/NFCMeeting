package com.bosch.iot.tapnbook.service;

import android.content.Context;

import com.bosch.iot.tapnbook.model.Account;
import com.bosch.iot.tapnbook.model.GoogleUserAccount;
import com.bosch.iot.tapnbook.model.OutLookUserAccount;
import com.bosch.iot.tapnbook.model.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Class to handle stored account deatails.
 */
public class StoredAccountService implements AccountService {
    private Context context;

    public StoredAccountService(Context ctx) {
        this.context = ctx;
    }

    @Override
    public Account readAccountFromStorage() {
        try {
            FileInputStream fileInputStream = context.openFileInput(Account.STORED_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            String csvFile = stringBuilder.toString();
            return parserAccount(csvFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean writeAccountToStorage(Account account){
        try {
            FileOutputStream fos = context.openFileOutput(Account.STORED_FILE_NAME, context.MODE_WORLD_WRITEABLE);
            fos.write(account.getCSVText().getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private Account parserAccount(String csvFile) {
        if (csvFile.contains(Type.OUTLOOK.toString())) {
            return new OutLookUserAccount(csvFile);
        }
        return new GoogleUserAccount(csvFile);
    }


}
