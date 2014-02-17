package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.caltraindating.models.Stop;
import com.codepath.caltraindating.models.Train;

public class TrainAdapter extends ArrayAdapter<Train> {
	private Activity context;
	ArrayList<Train> data = null;

	public TrainAdapter(Activity context, int resource, ArrayList<Train> data) {
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
	     ((TextView) row).setText(data.get(position).getId());

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
	     ((TextView) row).setText(data.get(position).getId());

	     return row;
	}
}
