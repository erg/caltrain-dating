package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.models.Train;

public class StringAdapter extends ArrayAdapter<String> {
	private Activity context;
	ArrayList<String> data = null;
	int resource;

	public StringAdapter(Activity context, int resource, ArrayList<String> data) {
		super(context, resource, data);
		this.context =context;
		this.data = data;
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
	     ((TextView) row.findViewById(R.id.tvSpinnerText)).setText(data.get(position));

	     return row;
	}
}