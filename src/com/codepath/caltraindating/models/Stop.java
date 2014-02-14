package com.codepath.caltraindating.models;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stop {
	String train;
	Long timeMillis;
	Date dateTime;
	String timePretty;
	
	public Stop(String train, Date time){
		this.train = train;
		this.timeMillis = time.getTime();
		this.dateTime = time;
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		this.timePretty = c.getDisplayName(Calendar.HOUR, Calendar.SHORT, Locale.US)+":"+c.getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.US)+
				(c.get(Calendar.HOUR_OF_DAY)>=12?"PM":"AM");
	}
	
	
	public String getTrain() {
		return train;
	}

	public void setTrain(String train) {
		this.train = train;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getTimePretty() {
		return timePretty;
	}

	public void setTimePretty(String timePretty) {
		this.timePretty = timePretty;
	}
}
