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



public class Train {
	
	static final int numStations = 29;
	static final String[] weekdayNorthTrains = {"305","313"};
	static final String[] weekdaySouthTrains = {};
	static final String[] weekendNorthTrains = {};
	static final String[] weekendSouthTrains = {};
	
	static HashMap<String,ArrayList<Long>> weekdayNorthSchedule = new HashMap<String,ArrayList<Long>>();
	static HashMap<String,ArrayList<Long>> weekdaySouthSchedule = new HashMap<String,ArrayList<Long>>();
	static HashMap<String,ArrayList<Long>> weekendNorthSchedule = new HashMap<String,ArrayList<Long>>();
	static HashMap<String,ArrayList<Long>> weekendSouthSchedule = new HashMap<String,ArrayList<Long>>();
	static HashMap<Integer,String> trainIndex = new HashMap<Integer,String>();
	

	public static long getArrivalTime(int train, int destination) {
		return 0;
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
	
	public static Date incrementTime(Date d,int day, int hr, int min, int sec, int mil){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, hr);
		c.add(Calendar.MINUTE, min);
		c.add(Calendar.SECOND, sec);
		c.add(Calendar.MILLISECOND, mil);
		return c.getTime();
	}
	
	public static Date getFutureDate(String time24){
		//takes a 24 hr string aka "14:15:16" and returns the date that matches the next time that time will happen
		Date prevMid = setTime(new Date(),0,0,0,0);
		String[] times = time24.split(":");
		Date target = incrementTime(prevMid,0,Integer.valueOf(times[0]),Integer.valueOf(times[1]),Integer.valueOf(times[2]),0);
		Date now = new Date();
		if(now.after(target)){
			//return target + 1 day
			return incrementTime(target,24,0,0,0,0);
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
							weekdayNorthSchedule.put(num, new ArrayList<Long>());
						}else if(Arrays.asList(weekdaySouthTrains).contains(num)){
							weekdaySouthSchedule.put(num, new ArrayList<Long>());
						}else if(Arrays.asList(weekendNorthTrains).contains(num)){
							weekendNorthSchedule.put(num, new ArrayList<Long>());
						}else if(Arrays.asList(weekendSouthTrains).contains(num)){
							weekendSouthSchedule.put(num, new ArrayList<Long>());
						}
					}
				}else{
					for(int j=1;j<split.length;j++){
						String num = trainIndex.get(j);
						Long millis;
						if(split[j].isEmpty() || split[j].equals("")){
							millis = (long) 0;
						}else{
							Date target = getFutureDate(split[j]);
							millis = target.getTime();
						}
						ArrayList<Long> times;
						if(Arrays.asList(weekdayNorthTrains).contains(num)){
							times = weekdayNorthSchedule.get(num);
						}else if(Arrays.asList(weekdaySouthTrains).contains(num)){
							times = weekdaySouthSchedule.get(num);
						}else if(Arrays.asList(weekendNorthTrains).contains(num)){
							times = weekendNorthSchedule.get(num);
						}else{
							times = weekendSouthSchedule.get(num);
						}
						times.add(millis);
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
