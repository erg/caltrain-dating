package com.codepath.caltraindating.models;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stop {
	String train;
	Long timeMillis;
	Date dateTime;
	String timePretty;
	String stopTimePretty;
	String stop;
	String trainStopTimePretty;
	String hour;
	String minute;
	String ampm;
	int stationId;
	
	public Stop(String train, Date time, String stop, int stationId){
		this.train = train;
		this.timeMillis = time.getTime();
		this.dateTime = time;
		this.stationId = stationId;
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		hour =  (String) ((c.get(Calendar.HOUR)==0)?"12":Integer.toString(c.get(Calendar.HOUR)));
		minute =  (String) ((c.get(Calendar.MINUTE)<10)?("0"+c.get(Calendar.MINUTE)):Integer.toString(c.get(Calendar.MINUTE)));
		ampm = (c.get(Calendar.HOUR_OF_DAY)>=12?"PM":"AM");
		this.timePretty = hour + ":"+minute+" "+ampm;
				
		this.stopTimePretty = stop + " @ " + this.timePretty;
		this.stop = stop;
		this.trainStopTimePretty = "#"+train+" ("+stopTimePretty+")";
	}
	
	public String getStopTimePretty() {
		return stopTimePretty;
	}


	public void setStopTimePretty(String stopTimePretty) {
		this.stopTimePretty = stopTimePretty;
	}


	public String getStop() {
		return stop;
	}


	public void setStop(String stop) {
		this.stop = stop;
	}


	public String getTrainStopTimePretty() {
		return trainStopTimePretty;
	}


	public void setTrainStopTimePretty(String trainStopTimePretty) {
		this.trainStopTimePretty = trainStopTimePretty;
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
