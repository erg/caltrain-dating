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
	
	static final String[] weekdayNorthTrains = {"305","313"};
	static final String[] weekdaySouthTrains = {};
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
	
	
	static final int DEST_GILROY = 0;
	static final int DEST_SANMARTIN = 1;
	static final int DEST_MORGANHILL = 2;
	static final int DEST_BLOSSOMHILL = 3;
	static final int DEST_CAPITOL = 4;
	static final int DEST_TAMIEN = 5;
	static final int DEST_SANJOSE = 6;
	static final int DEST_COLLEGEPARK = 7;
	static final int DEST_SANTACLARA = 8;
	static final int DEST_LAWRENCE = 9;
	static final int DEST_SUNNYVALE = 10;
	static final int DEST_MOUNTAINVIEW = 11;
	static final int DEST_SANANTONIO = 12;
	static final int DEST_CALIFORNIAAVE = 13;
	static final int DEST_PALOALTO = 14;
	static final int DEST_MENLOPARK = 15;
	static final int DEST_REDWOODCITY = 16;
	static final int DEST_SANCARLOS = 17;
	static final int DEST_BELMONT = 18;
	static final int DEST_HILLSDALE = 19;
	static final int DEST_HAYWARDPARK = 20;
	static final int DEST_SANMATEO = 21;
	static final int DEST_BURLINGAME = 22;
	static final int DEST_MILLBRAE = 23;
	static final int DEST_SANBRUNO = 24;
	static final int DEST_SOSANFRANCISCO = 25;
	static final int DEST_BAYSHORE = 26;
	static final int DEST_22STREET = 27;
	static final int DEST_SANFRANCISCO = 28;
	

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
		Long now = new Date().getTime();
		for(Stop s : allStops){
			if ((station == null || s.stationId == station) && Math.abs(now - s.timeMillis) < timeWindow){
				ret.add(s);
			}
		}
		return ret;
	}
	
	public static ArrayList<Stop> getStopsAheadOfTrain(Train t, Date d){
		Log.e("tag","get stops ahead of now: "+dateString(d));
		ArrayList<Stop> ret = new ArrayList<Stop>();
		Long millis = d.getTime();	
		Long maxTime = (long) (5*3600*1000);
		ArrayList<Stop> trainStops = getTrainTimes(t.getId());
		Stop prev = null;
		int stopSize = trainStops.size();
		if(isNorthBound(t.getId())){
			int i = stopSize-1;
			while(i > 0){
				if(stopsAheadHelper(ret,trainStops,prev,millis,i)){
					return ret;
				}
				i--;
				
			}
		}else{
			int i = 0;
			while(i < stopSize){
				if(stopsAheadHelper(ret,trainStops,prev,millis,i)){
					return ret;
				}
				i++;
			}
		}
		return ret;
	}
	
	private static boolean stopsAheadHelper(ArrayList<Stop> ret, ArrayList<Stop> trainStops, Stop prev, Long millis, int i){
		Long maxTime = (long) (5*3600*1000);
		Stop stop = trainStops.get(i);
		if(stop != null && prev != null && stop.getTimeMillis() > prev.getTimeMillis()){
			return true;
		}else if(stop != null  && stop.getTimeMillis() > millis && stop.getTimeMillis() < millis+maxTime){
			ret.add(stop);
			prev = stop;
		}
		return false;
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

	
	public static Date setTime(Date d,int hr, int min, int sec, int mil){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		c.set(Calendar.MILLISECOND, mil);
		return c.getTime();
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
	
	public static String dateString(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
	}
	
	public static Date getFutureDate(String time24){
		//takes a 24 hr string aka "14:15:16" and returns the date that matches the next time that time will happen
		Date prevMid = setTime(new Date(),0,0,0,0);
		String[] times = time24.split(":");
		Date target = incrementTime(prevMid,Integer.valueOf(times[0]),Integer.valueOf(times[1]),Integer.valueOf(times[2]),0);
		Date now = new Date();
		if(now.after(target)){
			//return target + 1 day
			return incrementTime(target,24,0,0,0);
		}else{
			return target;
		}
	}


	public static void initSchedules(Activity activity) {
		InputStream is = activity.getResources().openRawResource(R.raw.shedules);
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int i = 0;
		try{
			while((line = br.readLine()) != null){
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
							target = getFutureDate(split[j]);
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
