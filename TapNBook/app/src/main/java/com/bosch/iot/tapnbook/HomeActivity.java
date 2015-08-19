package com.bosch.iot.tapnbook;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;

import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class HomeActivity extends Activity {

    // Widgets
    public Button btOk;
    public TextView roomName;
    public TextView bookingTime;
    public RadioButton rdSameRoom;
    public RadioButton rdSameTime;
    public Resources room;
    private Resources changedRoom;
    private long changedTime = -1;
    private GoogleService service;
    private Calendar to;
    private Calendar from;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("H:mm a", new DateFormatSymbols());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new GoogleService(this);
        setContentView(R.layout.activity_home);
        initializeWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    service.isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        service.credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(service.PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Account unspecified.", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    service.chooseAccount();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        from = null;
        to = null;
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
        rdSameRoom=(RadioButton) findViewById(R.id.rdSameRoom);
        rdSameTime=(RadioButton) findViewById(R.id.rdSameTime);
        rdSameRoom.setVisibility(View.INVISIBLE);
        rdSameTime.setVisibility(View.INVISIBLE);
        initializeEventListeners();

    }

    private void initializeEventListeners() {
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOkClick((Button) v);
            }
        });

        rdSameTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked();
            }
        });

        rdSameRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked();
            }
        });
        prepareWidgetValues();

    }

    private void prepareWidgetValues() {
        roomName.setText("(((((O)))))");
        bookingTime.setText("");
        btOk.setEnabled(false);
    }

    private void onRadioButtonClicked(){
        btOk.setBackgroundColor(Color.GREEN);
        btOk.setEnabled(true);
        btOk.setText("Book");
    }

    private void buttonOkClick(View v) {
        Toast.makeText(this, "Room"+room + " changedRoom : "+ changedRoom + " ChaTime :"+ new DateTime(changedTime), Toast.LENGTH_LONG).show();
        if (room != null) {
            if(!rdSameRoom.isSelected() && !rdSameTime.isSelected()){
                service.bookMeetingRoom(room , from.getTimeInMillis());
            }else if(rdSameRoom.isSelected() && !rdSameTime.isSelected()){
                service.bookMeetingRoom(room , changedTime);
            }else if(!rdSameRoom.isSelected() && rdSameTime.isSelected()){
                service.bookMeetingRoom(changedRoom , changedTime);
            }
            btOk.setEnabled(false);
            btOk.setText("...");
        } else {
            Toast.makeText(this, "No Meeting Room Selected yet :(", Toast.LENGTH_LONG).show();
        }
    }

    private void validateTagMessage(NdefMessage[] msgs) {
        if (msgs.length == 1) {
            NdefRecord[] records = msgs[0].getRecords();
            if (records.length == 1) {
                room = getResourceName(new String(records[0].getPayload(), Charset.forName("UTF-8")));
            }
        }
        prepareValues();
    }

    private void prepareValues() {
        if (room != null) {
            prepareCurrentDateTime();
            service.IsRoomAvailable(room.getCalenderId());
        } else {
            roomName.setText("Invalid NFC Tag  :(");
            bookingTime.setText("(((((O)))))");
            btOk.setEnabled(false);

        }
    }

    public void updatePossibleResourceSameTime(Resources r){
        rdSameRoom.setVisibility(View.VISIBLE);
        rdSameRoom.setText(r.getRoomName() + " is available for the same time");
        btOk.setText("Still U Want to Book ?");
        btOk.setBackgroundColor(Color.RED);
        changedRoom = r;
    }

    public void updatePossibleTimeSameResource(long time){
        rdSameTime.setVisibility(View.VISIBLE);
        rdSameTime.setText(room.getRoomName() + " is only available at " + dateFormatter.format(new Date(time)));
        btOk.setText("Still U Want to Book ?");
        btOk.setBackgroundColor(Color.RED);
        changedTime = time;
    }

    public void updateEventAvailabilityValue(boolean flag) {
        if (flag) {
            roomName.setText(room.getRoomName());
            bookingTime.setText(getCurrentTimeText());
            btOk.setEnabled(true);
            btOk.setBackgroundColor(Color.GREEN);
        } else {
            roomName.setText(room.getRoomName() + " Not Available");
            bookingTime.setText(getCurrentTimeText());
            btOk.setBackgroundColor(Color.RED);
            btOk.setEnabled(false);
        }
    }

    public void updateEventCreationValue(String link) {
        if (link != null) {
            btOk.setText("Booked :)");
            btOk.setEnabled(false);
            Toast.makeText(this, "Calender Booked Successfully" + link, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Calender Booking failed :(", Toast.LENGTH_LONG).show();
        }
    }

    private void prepareCurrentDateTime() {
        to = Calendar.getInstance();
        from = Calendar.getInstance();
        to.add(Calendar.HOUR, 1);
    }

    private String getCurrentTimeText() {
        return dateFormatter.format(from.getTime()) + " To " + dateFormatter.format(to.getTime());
    }

    private Resources getResourceName(String payload) {
        if (payload.trim().startsWith("en")) {
            String strRoom = payload.replaceFirst("en", "");
            for (Resources room : Resources.values()) {
                if (room.getRoomName().trim().equalsIgnoreCase(strRoom.trim())) {
                    return room;
                }
            }
        }
        return null;
    }
}
