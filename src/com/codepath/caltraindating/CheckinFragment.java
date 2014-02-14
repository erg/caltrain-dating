package com.codepath.caltraindating;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

public class CheckinFragment extends Fragment{
	
	Button checkIn;
	Spinner trainNumbers;
	Spinner trainStops;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_checkin, container,false);
		checkIn = (Button)v.findViewById(R.id.btCheckIn);
		trainNumbers = (Spinner)v.findViewById(R.id.spTrainNumber);
		trainStops = (Spinner)v.findViewById(R.id.spTrainStop);
		
		return v;
		
	}
}
