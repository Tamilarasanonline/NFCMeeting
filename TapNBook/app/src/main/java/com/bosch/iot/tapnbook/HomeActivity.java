package com.bosch.iot.tapnbook;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeWidgets();
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

    @Override
    protected void onResume() {
        super.onResume();
        room = null;
        NdefMessage msgs[] = null;
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
        if(room != null){
            Toast.makeText(this, ""+room.toString() , Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "No Meeting Room Selected yet :(", Toast.LENGTH_LONG).show();
        }
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
        Toast.makeText(this, payload, Toast.LENGTH_LONG).show();
        if (payload.trim().startsWith("en")) {
            Toast.makeText(this, "inSIDE iF", Toast.LENGTH_LONG).show();
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
