package com.codepath.caltraindating;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

public class CheckinFragment extends Fragment implements OnClickListener{
	
	Button checkIn;
	Spinner trainNumbers;
	Spinner trainStops;
	Button addTrain;
	
	Listener listener;
	
	public interface Listener{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_checkin, container,false);
		checkIn = (Button)v.findViewById(R.id.btCheckIn);
		addTrain = (Button)v.findViewById(R.id.btAddTrain);
		trainNumbers = (Spinner)v.findViewById(R.id.spTrainNumber);
		trainStops = (Spinner)v.findViewById(R.id.spTrainStop);
		addTrain.setOnClickListener(this);
		return v;
		
	}
	
	public void setListener(Listener l){
		this.listener = l;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btAddTrain){
			Dialog d = new Dialog(getActivity());
			d.setContentView(R.layout.dialog_train_nums);
			d.setTitle("What train are you on?");
			d.show();
		}
	}
}
