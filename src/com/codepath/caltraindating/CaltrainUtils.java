package com.codepath.caltraindating;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
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

	public static boolean isNetworkOnline(Context context) {
		try {
			ConnectivityManager conMan = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			State mobile = conMan.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			Log.d("DEBUG", "mobile: " + mobile + ", wifi: " + wifi);
			if (mobile == NetworkInfo.State.CONNECTED
					|| mobile == NetworkInfo.State.CONNECTING
					|| wifi == NetworkInfo.State.CONNECTED
					|| wifi == NetworkInfo.State.CONNECTING) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
