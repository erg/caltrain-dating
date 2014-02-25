package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.caltraindating.CaltrainUtils;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.models.Checkin;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class RiderListAdapter extends ArrayAdapter<Checkin> {

	public RiderListAdapter(Context context, ArrayList<Checkin> checkins) {
		super(context, 0, checkins);
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParseUser rider = getItem(position).getUser();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_rider, null);
        }
        
        ImageView ivProfileThumbnail = (ImageView)convertView.findViewById(R.id.ivProfileThumbnail);
        
        ArrayList<String> thumbnails = (ArrayList<String>)rider.get("imgSrcs");
        if(thumbnails.size() > 0) {
        	String url = thumbnails.get(0);
        	Picasso.with(getContext()).load(url).into(ivProfileThumbnail);
        }
        TextView tvFirstName = (TextView)convertView.findViewById(R.id.tvFirstName);
        tvFirstName.setText((String)rider.get("firstName"));
        
        TextView tvAge = (TextView)convertView.findViewById(R.id.tvAge);
		String age = CaltrainUtils.calculateAge(convertView.getContext(), (String)rider.get("birthday"));
		tvAge.setText(age);		

        
        return convertView;
    }
	
}
