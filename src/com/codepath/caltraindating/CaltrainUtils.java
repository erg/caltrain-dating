package com.codepath.caltraindating;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public class CaltrainUtils {

	// Calculate age by year, then set birthday to current year and correct if necessary
	public static String calculateAge(Context context, String date) {
		final String FACEBOOK = "MM/dd/yyyy";
		try {
			SimpleDateFormat sf = new SimpleDateFormat(FACEBOOK, Locale.ENGLISH);
			sf.setLenient(true);
			Date birthDate = sf.parse(date);
			Date now = new Date();
			int age = now.getYear() - birthDate.getYear();
			birthDate.setHours(0);
			birthDate.setMinutes(0);
			birthDate.setSeconds(0);
			birthDate.setYear(now.getYear());
			if(birthDate.after(now)) {
				age -= 1;
			}
			return Integer.toString(age);
			
		} catch (Exception e) {
			Log.d("DEBUG", "exception in getTwitterDate:");
			e.printStackTrace();
			return "Unknown";
		}
	}
}
