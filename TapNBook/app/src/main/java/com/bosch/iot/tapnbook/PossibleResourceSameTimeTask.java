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
public class PossibleResourceSameTimeTask extends AsyncTask<Void, Void, Void> {
    private GoogleService service;
    private String calendarId;

    PossibleResourceSameTimeTask(GoogleService ser, String calendarId) {
        this.service = ser;
        this.calendarId = calendarId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            service.updatePossibleResourceSameTimeStatus(getAvailableResource());
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

    private Resources getAvailableResource() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime afterOneHour = new DateTime(System.currentTimeMillis() + 3600000);

        for (Resources r : getOtherResources(this.calendarId)) {
            Events events = service.mService.events().list(r.getCalenderId())
                    .setMaxResults(2)
                    .setTimeMin(now)
                    .setTimeMax(afterOneHour)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            if (events.getItems().size() == 0) {
                return r;
            }
        }
        return null;
    }

    private List<Resources> getOtherResources(String resourceId) {
        List<Resources> otherResources = new ArrayList<Resources>();
        for (Resources r : Resources.values()) {
            if (!r.getCalenderId().equalsIgnoreCase(resourceId)) {
                otherResources.add(r);
            }
        }
        return otherResources;
    }


}