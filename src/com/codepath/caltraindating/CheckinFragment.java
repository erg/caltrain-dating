package com.codepath.caltraindating;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.codepath.caltraindating.adapters.StopAdapter;
import com.codepath.caltraindating.models.Stop;
import com.codepath.caltraindating.models.Train;

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
			setupDialog();
		}
	}
	
	public void setupDialog(){
		final Long timeWindow = (long) 3600000*24;
		Dialog d = new Dialog(getActivity());
		d.setContentView(R.layout.dialog_train_nums);
		d.setTitle("What train are you on?");
		Spinner trainPick = (Spinner)d.findViewById(R.id.spTrainPick);
		final StopAdapter trainAdapter = new StopAdapter(getActivity(),android.R.layout.simple_spinner_item,Train.getStopsByTimePretty(timeWindow,null));
		trainPick.setAdapter(trainAdapter);
		
		Spinner stationPick = (Spinner)d.findViewById(R.id.spStationPick);
		stationPick.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				trainAdapter.clear();
				for(Stop s: Train.getStopsByTimePretty(timeWindow,pos)){
					trainAdapter.add(s);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,Train.stopNamesList);
		stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationPick.setAdapter(stationAdapter);
		
		d.show();
	}
}
