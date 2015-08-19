package com.bosch.iot.tapnbook;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tamil on 18/8/2015.
 */
public class CreateEventTask extends AsyncTask<Void, Void, Void> {
    private GoogleService service;
    private Resources room;

    CreateEventTask(GoogleService ser, Resources r) {
        this.service = ser;
        this.room = r;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            service.updateEventCreationStatus(createEvent());
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

    private String createEvent()throws IOException {
        Event event = new Event()
                .setSummary(room.getRoomName() + " Booked  by TapNBook")
                .setLocation(room.getLocation())
                .setDescription("TapNBook Event booked by " + service.getLoggedInUser());

        DateTime startDateTime = new DateTime(System.currentTimeMillis());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(room.getTimeZone());
        event.setStart(start);

        DateTime endDateTime = new DateTime(System.currentTimeMillis()+3600000);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(room.getTimeZone());
        event.setEnd(end);
        event.setRecurrence(null);

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(service.getLoggedInUser())
        };
        event.setAttendees(Arrays.asList(attendees));
        event = service.mService.events().insert(room.getCalenderId(), event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return event.getHtmlLink();
    }
}