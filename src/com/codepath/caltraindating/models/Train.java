package com.codepath.caltraindating.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Train {

	String id;
	ParseUser user;
	int usualStop=-1;
	int usualBoard=-1;
	Date lastSelected = Schedule.getNow();
	static ArrayList<Train> recentTrains = new ArrayList<Train>();
	
	public Train(ParseUser user,String id){
		this.user = user;
		this.id = id;
	}
	
	public static int indexOfUsualStop(ArrayList<Stop> stops, Train train){
		for(int i=0;i<stops.size();i++){
			if(train.usualStop == stops.get(i).getStationId()){
				return i;
			}
		}
		return 0;
	}
	
	public Train(ParseObject p){
		id = p.getString("id");
		user = p.getParseUser("user");
		usualStop = p.getInt("usualStop");
		usualBoard = p.getInt("usualBoard");
		lastSelected = p.getDate("lastSelected");
	}
	
	public boolean equals(Train t){
		return this.id.equalsIgnoreCase(t.getId());
	}
	public boolean equals(Object t){
		return this.id.equalsIgnoreCase(((Train)t).getId());
	}
	
	public void save(){
		final ParseObject train = new ParseObject("Train");
		train.put("user",user);
		train.put("id",id);
		train.put("usualStop",usualStop);
		train.put("usualBoard",usualBoard);
		train.put("lastSelected", lastSelected);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Train");
		query.whereEqualTo("user", user);
		query.whereEqualTo("id", id);
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> existing, ParseException e) {
		        if (e == null) {
		        	if(existing.size() == 1){
		        		//update train
		        		ParseObject ex = existing.get(0);
		        		ex.put("usualStop",usualStop);
		        		ex.put("usualBoard",usualBoard);
		        		ex.put("lastSelected", lastSelected);
		        		ex.saveInBackground();
		        	}else if(existing.size() == 0){
		        		//insert train
		        		train.saveInBackground();
		        	}
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }

		});
	}
	
	
	public static void getRecentTrains(ParseUser u, /*final FindCallback fc,*/ final Callback<ArrayList<Train>> cb){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Train");
		query.whereEqualTo("user", u);
		query.orderByDescending("updatedAt");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> trains, ParseException e) {
		        if (e == null) {
		        	recentTrains.clear();
		        	for (ParseObject p: trains){
		        		recentTrains.add(new Train(p));
		        	}
		        	cb.complete(recentTrains);
		        } else {
		            Log.e("tag", "Error: " + e.getMessage());
		        }
		    }

		});
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}

	public int getUsualStop() {
		return usualStop;
	}

	public void setUsualStop(int usualStop) {
		this.usualStop = usualStop;
	}

	public int getUsualBoard() {
		return usualBoard;
	}

	public void setUsualBoard(int usualBoard) {
		this.usualBoard = usualBoard;
	}

	public Date getLastSelected() {
		return lastSelected;
	}

	public void setLastSelected(Date lastSelected) {
		this.lastSelected = lastSelected;
	}
}
