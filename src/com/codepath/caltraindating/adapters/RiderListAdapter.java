package com.codepath.caltraindating.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.caltraindating.CaltrainUtils;
import com.codepath.caltraindating.ChatHolder;
import com.codepath.caltraindating.MainActivity;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.fragments.ChatFragment;
import com.codepath.caltraindating.fragments.ViewProfileFragment;
import com.codepath.caltraindating.models.ChatModel;
import com.codepath.caltraindating.models.Checkin;
import com.parse.ParseUser;
import com.parse.PushService;
import com.squareup.picasso.Picasso;

public class RiderListAdapter extends ArrayAdapter<Checkin> {

	Context context;
	MainActivity mainActivity;
	ArrayList<Checkin> checkins;
	ParseUser targetUser;

	public RiderListAdapter(Context context, Activity activity,
			ArrayList<Checkin> checkins) {
		super(context, 0, checkins);
		this.context = context;
		this.mainActivity = (MainActivity)activity;
		this.checkins = checkins;
	}

	// Don't let them click the listview items themselves
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		ParseUser rider = getItem(position).getUser();
		String targetUserId = rider.getObjectId();
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_rider, null);
		}

		ImageView ivProfileThumbnail = (ImageView) convertView
				.findViewById(R.id.ivProfileThumbnail);

		// View row = super.getView(position, convertView, parent);

		ivProfileThumbnail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewProfileFragment profileFragment = ViewProfileFragment
						.newInstance(false, position);
				mainActivity.switchToFragmentBack(profileFragment);
			}
		});

		ArrayList<String> thumbnails = (ArrayList<String>) rider.get("imgSrcs");
		if (thumbnails != null && thumbnails.size() > 0) {
			String url = thumbnails.get(0);
			Picasso.with(getContext()).load(url).into(ivProfileThumbnail);
		}
		TextView tvFirstName = (TextView) convertView
				.findViewById(R.id.tvFirstName);
		tvFirstName.setText((String) rider.get("firstName"));

		TextView tvAge = (TextView) convertView.findViewById(R.id.tvAge);
		String age = CaltrainUtils.calculateAge(convertView.getContext(),
				(String) rider.get("birthday"));
		tvAge.setText(age);

		ImageView ivChatAttention = (ImageView) convertView.findViewById(R.id.ivChatAttention);
		if (ChatHolder.getInstance().hasNewMessage(targetUserId))
			// ivChatAttention.setVisibility(View.VISIBLE);
		    ivChatAttention.setAlpha(255);
		else
		    // ivChatAttention.setVisibility(View.GONE);
		    ivChatAttention.setAlpha(0);
		
		double riderLongitude = getItem(position).getLongitude();
		double riderLatitude = getItem(position).getLatitude();
		int carsBetween = distCars(mainActivity.getLatitude(), mainActivity.getLongitude(), riderLatitude, riderLongitude);
		TextView tvDist = (TextView) convertView.findViewById(R.id.tvDist);
		if (carsBetween != 0) {
			if (carsBetween==1)
				tvDist.setText(" prob. in the same car");
			else if (carsBetween==2)
				tvDist.setText(" prob. 1 car away");
			else if (carsBetween > 10)
				tvDist.setText(" prob. not on the same train");
			else
				tvDist.setText(" prob. " + (carsBetween-1) + " cars away");
		}
		else
			tvDist.setText("");
			
		Button btMatch = (Button) convertView.findViewById(R.id.btMatch);
		btMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				targetUser = getItem(position).getUser();
		        // Get passed riderOwnId and riderChatToId from last activity 
		        String riderOwnId = mainActivity.getCurrentUserId();
		        String riderChatToId = targetUser.getObjectId();
		        String channel = riderChatToId + "-" + riderOwnId;
		        ArrayList<ChatModel> chatHistory = ChatHolder.getInstance().retrieve(riderChatToId);
		        if (chatHistory==null) {
		          	PushService.subscribe(context, channel, mainActivity.getClass());
		        }
				
				ChatFragment chatFragment = ChatFragment.newInstance(riderOwnId, riderChatToId);
				mainActivity.slideLeftToFragment(chatFragment, "CHAT");
			}
		});

		return convertView;
	}
	
	protected int distCars(double lat1, double lng1, double lat2, double lng2) {
		if ((lat1==0.0f && lng1==0.0f) || (lat2==0.0f && lng2==0.0f))
			return 0;
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    float distance = (float) (dist * meterConversion);
	    // Log.d("DEBUG", "distance=" + distance);
	    
	    return (int) (distance / 20.0f) + 1;
	}
	
}
