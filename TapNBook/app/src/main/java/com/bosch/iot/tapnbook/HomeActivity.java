package com.bosch.iot.tapnbook;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class HomeActivity extends Activity {

    // Widgets
    private Button btOk;
    private TextView roomName;
    private TextView bookingTime;
    private Resources room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toast.makeText(this, "" + getCurrentTimeText(), Toast.LENGTH_LONG).show();
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
        btOk = (Button) findViewById(R.id.btBook);
        roomName = (TextView) findViewById(R.id.lblResource);
        bookingTime = (TextView) findViewById(R.id.lblTime);
        initializeEventListeners();

    }

    private void initializeEventListeners() {
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOkClick((Button) v);
            }
        });
        prepareWidgetValues();

    }

    private void prepareWidgetValues() {
        roomName.setText("(((((O)))))");
        bookingTime.setText("");
        btOk.setEnabled(false);
    }

    private void buttonOkClick(View v) {
        if (room != null) {
            Toast.makeText(this, "" + room.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No Meeting Room Selected yet :(", Toast.LENGTH_LONG).show();
        }
    }

    private void validateTagMessage(NdefMessage[] msgs) {
        if (msgs.length == 1) {
            NdefRecord[] records = msgs[0].getRecords();
            if (records.length == 1) {
                room = getResourceName(new String(records[0].getPayload(), Charset.forName("UTF-8")));
                Toast.makeText(this, "" + room.toString(), Toast.LENGTH_LONG).show();
            }
        }
        prepareValues();
    }

    private void prepareValues() {
        Toast.makeText(this, "" + room.toString(), Toast.LENGTH_LONG).show();
        if (room != null) {
            roomName.setText(room.toString());
            bookingTime.setText(getCurrentTimeText());
            btOk.setEnabled(true);
        } else {
            roomName.setText("Invalid NFC Tag  :(");
            bookingTime.setText("(((((O)))))");
            btOk.setEnabled(false);

        }
    }

    private String getCurrentTimeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm a", new DateFormatSymbols());
        Calendar to = Calendar.getInstance();
        Calendar from = Calendar.getInstance();
        to.add(Calendar.HOUR, 1);
        return sdf.format(from.getTime()) + " To " + sdf.format(to.getTime());
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
