package com.codepath.caltraindating.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Checkin {
	ParseUser user;
	int destination;
	long checkinTime;
	long checkoutTime;
	String train;
	
	public Checkin(ParseUser user,String train, int destination, Long checkinTime){
		if(checkinTime == null){
			checkinTime = System.currentTimeMillis();
		}
		this.checkinTime = checkinTime;
		this.checkoutTime = Train.getArrivalTime(train, destination);
		this.train =  train;
		this.user = user;
		this.destination = destination;
	}
	
	public void save(){
		ParseObject checkin = new ParseObject("Checkin");
		checkin.put("user",user);
		checkin.put("destination",destination);
		checkin.put("checkinTime",checkinTime);
		checkin.put("checkoutTime",checkoutTime);
		checkin.put("train",train);
		checkin.saveInBackground();
	}
}
