package com.bosch.iot.tapnbook;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tamil on 18/8/2015.
 */
public class PossibleTimeSameResourceTask extends AsyncTask<Void, Void, Void> {
    private GoogleService service;
    private String calendarId;

    PossibleTimeSameResourceTask(GoogleService ser, String calendarId) {
        this.service = ser;
        this.calendarId=calendarId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            service.updatePossibleTimeSameResourceStatus(getNextAvailableTime());
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            service.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            service.activity.startActivityForResult(
                    userRecoverableException.getIntent(), service.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            service.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    private DateTime getNextAvailableTime() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.mService.events().list(this.calendarId)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        long preEnd = -1;
        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            DateTime end = event.getEnd().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            if (end == null) {
                end = event.getEnd().getDate();
            }

            if(preEnd < 0){
                preEnd= end.getValue();
            }else{
                if ((start.getValue() - preEnd) >= 3600000){
                    return new DateTime(preEnd);
                }else{
                    preEnd = end.getValue();
                }
            }
        }
        return  new DateTime(preEnd);
    }


}