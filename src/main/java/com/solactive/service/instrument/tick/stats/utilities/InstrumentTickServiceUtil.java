package com.solactive.service.instrument.tick.stats.utilities;

import java.util.Calendar;
import java.util.Date;

public class InstrumentTickServiceUtil {

	public static Date truncateMillis(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Long trucateMillis(Long millisec) {
		millisec = (millisec / 1000) * 1000;
		return millisec;
	}
	
	public static Long timeMinusSeconds(Long millis, int secondsToMinus) {
		millis = millis - (secondsToMinus * 1000);
		return millis;
	}
}
