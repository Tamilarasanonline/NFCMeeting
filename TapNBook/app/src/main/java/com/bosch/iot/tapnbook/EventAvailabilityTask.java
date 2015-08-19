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

/**
 * Created by Tamil on 18/8/2015.
 */
public class EventAvailabilityTask extends AsyncTask<Void, Void, Void> {
    private GoogleService service;
    private String calendarId;

    EventAvailabilityTask(GoogleService ser,String calendarId) {
        this.service = ser;
        this.calendarId=calendarId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            service.updateEventAvailabilityStatus(getCalenderEvents());
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

    private List<String> getCalenderEvents() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime afterOneHour = new DateTime(System.currentTimeMillis()+3600000);
        List<String> eventStrings = new ArrayList<String>();
        Events events = service.mService.events().list(this.calendarId)
                .setMaxResults(2)
                .setTimeMin(now)
                .setTimeMax(afterOneHour)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }


}