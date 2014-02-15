package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.caltraindating.models.Stop;

public class StopAdapter extends ArrayAdapter<Stop> {
	
	private Activity context;
	ArrayList<Stop> data = null;

	public StopAdapter(Activity context, int resource, ArrayList<Stop> data) {
		super(context, resource, data);
		this.context =context;
		this.data = data;
	}
	
	@Override
	 public View getDropDownView(int position, View convertView, ViewGroup parent)
	 {   
	     View row = convertView;
	     if(row == null)
	     {
	         //inflate your customlayout for the textview
	         LayoutInflater inflater = context.getLayoutInflater();
	         row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
	     }
	     ((TextView) row).setText(data.get(position).getTrainStopTimePretty());

	     return row;
	 }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
	     if(row == null)
	     {
	         //inflate your customlayout for the textview
	         LayoutInflater inflater = context.getLayoutInflater();
	         row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
	     }
	     ((TextView) row).setText(data.get(position).getTrainStopTimePretty());

	     return row;
	}
	


}
