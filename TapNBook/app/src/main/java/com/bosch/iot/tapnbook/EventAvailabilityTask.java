package com.bosch.iot.tapnbook;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Tamil on 18/8/2015.
 */
public class EventAvailabilityTask extends AsyncTask<Void, Void, Void> {
    private GoogleService service;

    EventAvailabilityTask(GoogleService ser) {
        this.service = ser;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            List<Event> events = getAvailableEvents();
            if (events != null && events.size() == 0) {
                service.activity.updateBookingValue(true);
            } else {
                service.activity.updateBookingValue(false);
            }
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            service.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            service.activity.startActivityForResult(
                    userRecoverableException.getIntent(), service.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            service.activity.printErrorMessage(e.getMessage());
            service.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    private List<Event> getAvailableEvents() throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        long after = currentTimeMillis + 3600000;
        TimeZone zone =TimeZone.getDefault();
        DateTime from = new DateTime(currentTimeMillis);
        DateTime to = new DateTime(after);
        Events events = service.mService.events().list("iotcaptain@gmail.com")
                .setMaxResults(2)
                .setTimeMin(from)
                .setTimeMax(to)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setTimeZone(zone.toString())
                .execute();
        return events.getItems();
    }

}