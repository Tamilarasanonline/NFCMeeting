package com.bosch.iot.tapnbook;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;

import android.content.SharedPreferences;

import com.google.api.services.calendar.CalendarScopes;

import android.content.Context;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends Activity {

    // Widgets
    private Button btOk;
    private Button btCancel;
    private TextView title;
    private Resources room;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "tamilarasanonline@gmail.com";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    GoogleAccountCredential credential;

    com.google.api.services.calendar.Calendar mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeWidgets();
        initializeCalendar();
    }

    private void initializeCalendar() {
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        if (credential != null && credential.getAllAccounts() != null && credential.getSelectedAccount() == null) {
            credential.setSelectedAccountName(PREF_ACCOUNT_NAME);
            Toast.makeText(this, "GetSelectedAccount " + credential.getSelectedAccountName(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        HomeActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshResults() {
        Toast.makeText(this, "refreshResults :: before Choose Account" + credential.getSelectedAccountName(), Toast.LENGTH_LONG).show();
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new ApiAsyncTask(this).execute();
            } else {
                Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void clearResultsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText("Retrieving data…");
            }
        });
    }

    public void updateResultsText(final List<String> dataStrings) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    title.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    title.setText("No data found.");
                } else {
                    title.setText(TextUtils.join("Data retrieved using" +
                            " the Google Calendar API:" + "\n\n", dataStrings));
                }
            }
        });
    }

    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(message);
            }
        });
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        room = null;
        NdefMessage msgs[] = null;
        if (isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google Service Available", Toast.LENGTH_LONG).show();
            refreshResults();
        } else {
            Toast.makeText(this, "Google Play Services required: " +
                    "after installing, close and relaunch this app.", Toast.LENGTH_LONG).show();
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
        }
        if (msgs != null && msgs.length > 0) {
            validateTagMessage(msgs);
        }
    }

    private void initializeWidgets() {
        btOk = (Button) findViewById(R.id.btOk);
        btCancel = (Button) findViewById(R.id.btCancel);
        title = (TextView) findViewById(R.id.lblTitle);
        initializeEventListeners();
    }

    private void initializeEventListeners() {
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOkClick((Button) v);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancelClick((Button) v);
            }
        });
    }

    private void buttonOkClick(View v) {
//        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);

//        credential = GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff())
//                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        Toast.makeText(this, "" + credential, Toast.LENGTH_LONG).show();

//        if (room != null) {
//            Toast.makeText(this, "" + room.toString(), Toast.LENGTH_LONG).show();
//
//
//        } else {
//            Toast.makeText(this, "No Meeting Room Selected yet :(", Toast.LENGTH_LONG).show();
//        }
    }

    private void buttonCancelClick(View v) {
        Toast.makeText(this, "Cancel Clicked", Toast.LENGTH_LONG).show();
        finish();
    }

    private void validateTagMessage(NdefMessage[] msgs) {
        String message = "Invalid NFC tag :(";
        if (msgs.length == 1) {
            NdefRecord[] records = msgs[0].getRecords();
            if (records.length == 1) {
                room = getResourceName(new String(records[0].getPayload(), Charset.forName("UTF-8")));
                message = "Book " + room.toString() + "?";
            }
        }
        title.setText(message);
    }

    private Resources getResourceName(String payload) {
        if (payload.trim().startsWith("en")) {
            String strRoom = payload.replaceFirst("en", "");
            for (Resources room : Resources.values()) {
                if (room.toString().trim().equalsIgnoreCase(strRoom.trim())) {
                    return room;
                }
            }
        }
        return null;
    }
}
