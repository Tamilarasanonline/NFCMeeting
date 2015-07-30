package com.bosch.iot.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

public class CalenderService {

	private String applicationName;

	private JsonFactory jsonFactory;
	private GoogleClientSecrets clientSecrets;
	private GoogleAuthorizationCodeFlow authorizationCodeFlow;
	private HttpTransport httpTransport;
	private FileDataStoreFactory dataStoreFactory;
	private Credential credential;
	private Calendar calendar;

	public CalenderService() throws GeneralSecurityException, IOException {
		this.applicationName = "NFCMeeting";
		jsonFactory = JacksonFactory.getDefaultInstance();
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	}

	public CalenderService(String appName) throws GeneralSecurityException,
			IOException {
		this();
		this.applicationName = appName;
	}

	public void setHttpProxy(String host, int port) {
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", "" + port);
	}

	public void setHttpsProxy(String host, int port) {
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", "" + port);
	}

	public Calendar getUserCalendar() throws IOException {
		calendar = new com.google.api.services.calendar.Calendar.Builder(
				httpTransport, jsonFactory, prepareUserCredential())
				.setApplicationName(applicationName).build();
		return calendar;
	}

	private GoogleClientSecrets prepareClientSecrets() throws IOException {
		clientSecrets = GoogleClientSecrets.load(
				jsonFactory,
				new InputStreamReader(CalenderService.class
						.getResourceAsStream("/client_secret.json")));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret()
						.startsWith("Enter ")) {
			System.out
					.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calender "
							+ "into CalenderService/src/main/resources/client_secret.json");
			System.exit(1);
		}

		return clientSecrets;
	}

	private FileDataStoreFactory prepareCalendarStoreDirectory()
			throws IOException {
		java.io.File DATA_STORE_DIR = new java.io.File(
				System.getProperty("user.home"), ".store/calendar_sample");
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		return dataStoreFactory;
	}

	private GoogleAuthorizationCodeFlow prepareAuthorizationCode()
			throws IOException {
		authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, prepareClientSecrets(),
				Collections.singleton(CalendarScopes.CALENDAR))
				.setDataStoreFactory(prepareCalendarStoreDirectory()).build();
		return authorizationCodeFlow;
	}

	private Credential prepareUserCredential() throws IOException {
		credential = new AuthorizationCodeInstalledApp(
				prepareAuthorizationCode(), new LocalServerReceiver())
				.authorize("user");
		return credential;
	}
}
