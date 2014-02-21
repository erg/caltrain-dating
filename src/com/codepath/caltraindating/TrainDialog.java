package com.codepath.caltraindating;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.codepath.caltraindating.adapters.StopAdapter;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Stop;

public class TrainDialog extends DialogFragment implements OnClickListener{
	
	final Long timeWindow = (long) 3600000/2;
	Listener listener = null;
	Button done;
	Stop selected = null;
	ArrayList<Stop> stops = new ArrayList<Stop>();
	

	public interface Listener {
		public void onStopSelected(Stop s);
	}
	
	public TrainDialog(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_train_nums, container);
		
		done = (Button)v.findViewById(R.id.btTrainPick);
		done.setOnClickListener(this);
		
		//getDialog().setTitle("Find your train");
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		final Spinner trainPick = (Spinner)v.findViewById(R.id.spTrainPick);
		final StopAdapter trainAdapter = new StopAdapter(getActivity(),R.layout.spinner_item_grey,stops,StopAdapter.FORMAT_LONG);
		trainPick.setAdapter(trainAdapter);
		trainPick.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
					selected = (Stop) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		Spinner stationPick = (Spinner)v.findViewById(R.id.spStationPick);
		ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,Schedule.stopNamesList);
		stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationPick.setAdapter(stationAdapter);
		stationPick.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				trainAdapter.clear();
				ArrayList<Stop> st = Schedule.getStopsByTimePretty(timeWindow,pos);
				Log.e("tag","onItemSelected size: "+st.size());
				for(Stop s: st){
					trainAdapter.add(s);
				}
				selected = (Stop) trainPick.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		selected = (Stop) trainPick.getSelectedItem();
		return v;
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btTrainPick){
			if(selected != null){
				listener.onStopSelected(selected);
				dismiss();
			}
		}
	}



	public Listener getListener() {
		return listener;
	}



	public void setListener(Listener listener) {
		this.listener = listener;
	}

}
