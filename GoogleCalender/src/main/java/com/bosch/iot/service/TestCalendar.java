package com.bosch.iot.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

public class TestCalendar {

	public static void main(String[] args) {
		try {
			CalenderService service = new CalenderService();
			service.setHttpProxy("192.168.51.80", 3128);
			service.setHttpsProxy("192.168.51.80", 3128);
			Calendar userCalendar = service.getUserCalendar();			
			com.google.api.services.calendar.model.Events events = userCalendar.events().list("iotcaptain@gmail.com").execute();		    
		    for(Event e : events.getItems())
				 System.out.println(""+ e.toPrettyString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
