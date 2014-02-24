package com.codepath.caltraindating.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.util.Log;

import com.codepath.caltraindating.R;



public class Schedule {
	
	static final String[] weekdayNorthTrains = {"101", "103", "305", "207", "309", "211", "313", "215", "217", "319", "221", "323", "225", "227", "329", "231", "233", "135", "237", "139", "143", "147", "151", "155", "257", "159", "261", "263", "365", "267", "269", "371", "273", "375", "277", "279", "381", "283", "385", "287", "289", "191", "193", "195", "197", "199"};
	static final String[] weekdaySouthTrains = {"102", "104", "206", "208", "210", "312", "314", "216", "218", "220", "322", "324", "226", "228", "230", "332", "134", "236", "138", "142", "146", "150", "152", "254", "156", "258", "360", "262", "264", "366", "268", "370", "272", "274", "376", "278", "380", "282", "284", "386", "288", "190", "192", "194", "196", "198"};
	static final String[] weekendNorthTrains = {};
	static final String[] weekendSouthTrains = {};
	
	static HashMap<String,ArrayList<Stop>> weekdayNorthSchedule = new HashMap<String,ArrayList<Stop>>();
	static HashMap<String,ArrayList<Stop>> weekdaySouthSchedule = new HashMap<String,ArrayList<Stop>>();
	static HashMap<String,ArrayList<Stop>> weekendNorthSchedule = new HashMap<String,ArrayList<Stop>>();
	static HashMap<String,ArrayList<Stop>> weekendSouthSchedule = new HashMap<String,ArrayList<Stop>>();
	static HashMap<Integer,String> trainIndex = new HashMap<Integer,String>();
	static HashMap<Integer,String> stopNames = new HashMap<Integer,String>();
	public static ArrayList<String> stopNamesList = new ArrayList<String>();
	
	static ArrayList<Stop> allStops = new ArrayList<Stop>();
	
	
	public static Date getNow(){
		return new Date();
		/*simulates a 7 am-ish checkin
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR, 9);
		c.set(Calendar.AM_PM, Calendar.AM);
		return c.getTime();*/
	}
	
	public static Long getArrivalTime(String train, int destination) {
		ArrayList<Stop> times = getTrainTimes(train);
		Stop s = times.get(destination);
		if(s != null){
			return s.getTimeMillis();
		}else{
			return null;
		}
	}
	
	public static ArrayList<Stop> getStopsByTimePretty(Long timeWindow, Integer station){
		ArrayList<Stop> ret = new ArrayList<Stop>();
		Long now = getNow().getTime();
		for(Stop s : allStops){
			if ((station == null || s.stationId == station) && Math.abs(now - s.getTimeMillis()) < timeWindow ){
				ret.add(s);
			}
		}
		return ret;
	}
	
	public static ArrayList<Stop> getStopsAheadOfTrain(Train t, Date d){
		ArrayList<Stop> ret = new ArrayList<Stop>();
		Long millis = d.getTime();	
		ArrayList<Stop> trainStops = getTrainTimes(t.getId());
		int stopSize = trainStops.size();
		if(isNorthBound(t.getId())){
			for(int i=stopSize-1;i>=0;i--){
				Stop stop = trainStops.get(i);
				if(stop != null && stop.getTimeMillis() > millis){
					ret.add(stop);
				}
			}
		}else{
			for(int i=0;i<stopSize;i++){
				Stop stop = trainStops.get(i);
				if(stop != null && stop.getTimeMillis() > millis){
					ret.add(stop);
				}
			}
		}
		return ret;
	}
	
	public static boolean isNorthBound(String train){
		if(weekdayNorthSchedule.containsKey(train)){
			return true;
		}else if(weekdaySouthSchedule.containsKey(train)){
			return false;
		}else if(weekendNorthSchedule.containsKey(train)){
			return true;
		}else{
			return false;
		}
	}
	
	public static String dateString(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.DAY_OF_MONTH)+" -- "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
	}
	
	public static Date incrementTime(Date d, int hr, int min, int sec, int mil){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.HOUR, hr);
		c.add(Calendar.MINUTE, min);
		c.add(Calendar.SECOND, sec);
		c.add(Calendar.MILLISECOND, mil);
		return c.getTime();
	}
	
	public static Date setTime(Date d,int hr, int min, int sec, int mil){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		c.set(Calendar.MILLISECOND, mil);
		return c.getTime();
	}
	
	public static Date getTodaysDateTime(String time24){
		//takes a 24 hr string aka "14:15:16" and returns the date that time happened today (today = 3am - 3am)
		Date prevMid = setTime(getNow(),0,0,0,0);
		String[] times = time24.split(":");
		Date target = incrementTime(prevMid,Integer.valueOf(times[0]),Integer.valueOf(times[1]),0,0);
		Date now = getNow();
		long hours3 = 3600*1000*3;
		if(now.getTime() - prevMid.getTime() < hours3){
			//return target + 1 day
			return incrementTime(target,24,0,0,0);
		}else{
			return target;
		}
	}
	
	public static ArrayList<Stop> getTrainTimes(String train){
		if(weekdayNorthSchedule.containsKey(train)){
			return weekdayNorthSchedule.get(train);
		}else if(weekdaySouthSchedule.containsKey(train)){
			return weekdaySouthSchedule.get(train);
		}else if(weekendNorthSchedule.containsKey(train)){
			return weekendNorthSchedule.get(train);
		}else{
			return weekendSouthSchedule.get(train);
		}
	}
	
	public static void initSchedules(Activity activity) {
		InputStream is = activity.getResources().openRawResource(R.raw.schedules);
		allStops.clear();
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int i = 0;
		try{
			while((line = br.readLine()) != null){
				//Log.e("tag","line: "+line);
				String[] split = line.split(",",-1);
				if(i == 0){
					for(int j=1;j<split.length;j++){
						String num = split[j];
						trainIndex.put(j, num);
						if(Arrays.asList(weekdayNorthTrains).contains(num)){
							weekdayNorthSchedule.put(num, new ArrayList<Stop>());
						}else if(Arrays.asList(weekdaySouthTrains).contains(num)){
							weekdaySouthSchedule.put(num, new ArrayList<Stop>());
						}else if(Arrays.asList(weekendNorthTrains).contains(num)){
							weekendNorthSchedule.put(num, new ArrayList<Stop>());
						}else if(Arrays.asList(weekendSouthTrains).contains(num)){
							weekendSouthSchedule.put(num, new ArrayList<Stop>());
						}
					}
				}else{
					stopNames.put(i-1, split[0]);
					stopNamesList.add(split[0]);
					for(int j=1;j<split.length;j++){
						String num = trainIndex.get(j);
						Long millis;
						Date target = null;
						boolean north = true;
						if(!split[j].isEmpty() && !split[j].equals("")){
							target = getTodaysDateTime(split[j]);
						}
						ArrayList<Stop> times;
						if(Arrays.asList(weekdayNorthTrains).contains(num)){
							times = weekdayNorthSchedule.get(num);
						}else if(Arrays.asList(weekdaySouthTrains).contains(num)){
							times = weekdaySouthSchedule.get(num);
							north = false;
						}else if(Arrays.asList(weekendNorthTrains).contains(num)){
							times = weekendNorthSchedule.get(num);
						}else{
							times = weekendSouthSchedule.get(num);
							north = false;
						}
						if(target == null){
							times.add(null);
						}else{
							Stop s = new Stop(num,target,split[0],i-1,north);
							times.add(s);
							allStops.add(s);
						}
						
					}
				}
				i++;
			}
			is.close();
			br.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
