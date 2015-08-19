package com.bosch.iot.tapnbook;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;


import com.google.api.services.calendar.CalendarScopes;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tamil on 18/8/2015.
 */
public class GoogleService {
    public HomeActivity activity;
    public com.google.api.services.calendar.Calendar mService;
    private GoogleAccountCredential credential;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "tamilarasanonline@gmail.com";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};


    public GoogleService(HomeActivity act) {
        activity = act;
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                activity.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        if (credential.getAllAccounts().length > 0 && credential.getSelectedAccount() == null) {
            credential.setSelectedAccountName(PREF_ACCOUNT_NAME);
        }
    }

    public String getLoggedInUser(){
        if(credential != null && credential.getSelectedAccount() != null){
            Account ac = credential.getSelectedAccount();
            return ac.name;
        }
        return "";
    }

    public void IsRoomAvailable(String calendarId) {
        if (checkGoogleService()) {
            new EventAvailabilityTask(this, calendarId).execute();
        }
    }

    public void updateEventAvailabilityStatus(final List<String> events) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (events == null) {
                    Toast.makeText(activity, "Error retrieving events!", Toast.LENGTH_LONG).show();
                } else if (events.size() == 0) {
                    Toast.makeText(activity, "No event found.", Toast.LENGTH_LONG).show();
                    activity.updateEventAvailabilityValue(true);
                } else {
                    Toast.makeText(activity, "Events>>" + TextUtils.join("\n\n", events), Toast.LENGTH_LONG).show();
                    activity.updateEventAvailabilityValue(false);
                }
            }
        });
    }

    public void bookMeetingRoom(Resources room) {
        if (checkGoogleService()) {
            new CreateEventTask(this, room).execute();
        }
    }

    public void updateEventCreationStatus(final String event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null) {
                    Toast.makeText(activity, "Error retrieving events!", Toast.LENGTH_LONG).show();
                } else {
                    activity.updateEventCreationValue(event);
                }
            }
        });
    }
    public void updateStatus(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        activity,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private boolean checkGoogleService() {
        if (isGooglePlayServicesAvailable()) {
            if (credential.getSelectedAccountName() == null) {
                chooseAccount();
            } else {
                if (isDeviceOnline()) {
                    return true;
                } else {
                    Toast.makeText(activity, "No network connection available.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(activity, "Google Play Services required:", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    private void chooseAccount() {
        Intent i = credential.newChooseAccountIntent();
        Toast.makeText(activity, i.getDataString(), Toast.LENGTH_LONG).show();

        activity.startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


}

