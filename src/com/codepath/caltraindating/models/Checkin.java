package com.codepath.caltraindating.models;

import com.parse.ParseUser;

public class Checkin {
	ParseUser user;
	int destination;
	long checkinTime;
	long checkoutTime;
	int train;
	
	public Checkin(ParseUser user,int train, int destination, Long checkinTime){
		if(checkinTime == null){
			checkinTime = System.currentTimeMillis();
		}
		checkoutTime = Train.getArrivalTime(train, destination);
	}
}
