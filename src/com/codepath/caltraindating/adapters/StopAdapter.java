package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.models.Stop;

public class StopAdapter extends ArrayAdapter<Stop> {
	
	private Activity context;
	ArrayList<Stop> data = null;
	public static final int FORMAT_LONG=0;
	public static final int FORMAT_TRAIN=1;
	public static final int FORMAT_NAME=2;
	int format = FORMAT_LONG;
	int resource;

	public StopAdapter(Activity context, int resource, ArrayList<Stop> data, int format) {
		super(context, resource, data);
		this.context =context;
		this.data = data;
		this.format = format;
		this.resource = resource;
	}
	
	@Override
	 public View getDropDownView(int position, View convertView, ViewGroup parent)
	 {   
		return viewHelper(position,convertView,parent);
	 }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return viewHelper(position,convertView,parent);
	}
	
	private View viewHelper(int position, View convertView, ViewGroup parent){
		View row = convertView;
	     if(row == null)
	     {
	         //inflate your customlayout for the textview
	         LayoutInflater inflater = context.getLayoutInflater();
	         row = inflater.inflate(resource, parent, false);
	     }
	     ((TextView) row.findViewById(R.id.tvSpinnerText)).setText(format(data.get(position)));

	     return row;
	}
	
	String format(Stop s){
		if(format == FORMAT_LONG){
			return s.getTrainStopTimePretty();
		}else if(format == FORMAT_TRAIN){
			return s.getTrain();
		}else if(format == FORMAT_NAME){
			return s.getStop();
		}
		return s.getTrainStopTimePretty(); 
	}
	


}
