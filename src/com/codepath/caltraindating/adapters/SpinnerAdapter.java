package com.codepath.caltraindating.adapters;

import com.codepath.caltraindating.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<String> {
	private Activity context;
	 String[] data = null;

	 public SpinnerAdapter(Activity context, int resource, String[] data2)
	 {
	     super(context, resource, data2);
	     this.context = context;
	     this.data = data2;
	 }

	 @Override
	 public View getDropDownView(int position, View convertView, ViewGroup parent)
	 {   
	     View row = convertView;
	     if(row == null)
	     {
	         //inflate your customlayout for the textview
	         LayoutInflater inflater = context.getLayoutInflater();
	         row = inflater.inflate(R.layout.spinner_item_red, parent, false);
	     }
	     //put the data in it
	     String item = data[position];
	     if(item != null)
	     {   
	        TextView text1 = (TextView) row.findViewById(R.id.tvSpinnerText);
	        text1.setTextColor(Color.WHITE);
	        text1.setText(item);
	     }

	     return row;
	 }
}
