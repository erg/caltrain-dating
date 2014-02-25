package com.codepath.caltraindating.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
		this.checkoutTime = Schedule.getArrivalTime(train, destination);
		this.train = train;
		this.user = user;
		this.destination = destination;
	}
	
	public Checkin(ParseObject p){
		user = p.getParseUser("user");
		destination = p.getInt("destination");
		checkinTime = p.getLong("checkinTime");
		checkoutTime = p.getLong("checkoutTime");
		train = p.getString("train");
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
	
	public static void getCheckins(ParseUser u, Train t,Date d, final Callback<ArrayList<Checkin>> cb){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Checkin");
		//query.whereNotEqualTo("user", u);
		query.include("user");
		query.whereEqualTo("train", t.getId());
		query.whereGreaterThan("checkoutTime", d.getTime());
		query.orderByDescending("updatedAt");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> checkins, ParseException e) {
		        if (e == null) {
		        	// XXX: Can't ask for unique users
		        	// so if they check in several times, we get them several times...
		        	HashSet<String> seen = new HashSet<String>();;
		        	ArrayList<Checkin> ret = new ArrayList<Checkin>();
		        	for (ParseObject p: checkins){
		        		Checkin checkin = new Checkin(p);
		        		String objId = checkin.getUser().getObjectId();
		        		if(!seen.contains(objId)) {
		        			seen.add(objId);
		        			ret.add(new Checkin(p));
		        		}
		        	}
		        	cb.complete(ret);
		        } else {
		            Log.e("tag", "Error: " + e.getMessage());
		        }
		    }

		});
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public long getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(long checkinTime) {
		this.checkinTime = checkinTime;
	}

	public long getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(long checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public String getTrain() {
		return train;
	}

	public void setTrain(String train) {
		this.train = train;
	}
}
