package com.codepath.caltraindating;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.codepath.caltraindating.models.Stop;

public class CheckinFragment extends Fragment implements OnClickListener, TrainDialog.Listener{
	
	Button checkIn;
	Spinner trainNumbers;
	Spinner trainStops;
	Button addTrain;
	Listener listener;
	ArrayList<String> recentTrains = new ArrayList<String>();
	ArrayAdapter<String> trainAdapter;
	TrainDialog trainDialog;
	SharedPreferences sharedPref;
	
	public interface Listener{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		sharedPref = getActivity().getSharedPreferences(
		        getString(R.string.preferences_file), Context.MODE_PRIVATE);
		initRecentTrains();
		View v = inflater.inflate(R.layout.fragment_checkin, container,false);
		checkIn = (Button)v.findViewById(R.id.btCheckIn);
		addTrain = (Button)v.findViewById(R.id.btAddTrain);
		trainNumbers = (Spinner)v.findViewById(R.id.spTrainNumber);
		trainStops = (Spinner)v.findViewById(R.id.spTrainStop);
		addTrain.setOnClickListener(this);
		trainDialog = new TrainDialog(getActivity(),this);
		
		trainAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, recentTrains);
		trainNumbers.setAdapter(trainAdapter);
		
		return v;
		
	}
	
	public void setListener(Listener l){
		this.listener = l;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btAddTrain){
			trainDialog.show();
		}
	}
	
	private void initRecentTrains(){
		String trainConcat = sharedPref.getString("recentTrains", "");
		String[] trains = trainConcat.split(",");
		for(int i=0;i<trains.length;i++){
			recentTrains.add(trains[i]);
		}
	}
	
	private void insertRecentTrain(String t){
		int pos = recentTrains.indexOf(t);
		if(pos >= 0){
			recentTrains.remove(pos);
		}
		recentTrains.add(0,t);
		trainAdapter.notifyDataSetChanged();
	}
	
	private void saveRecentTrains(){
		String trains = "";
		for(String t : recentTrains){
			trains += t + ",";
		}
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("recentTrains", trains);
		editor.commit();
	}

	@Override
	public void onStopSelected(Stop s) {
		insertRecentTrain(s.getTrain());
		saveRecentTrains();
	}
}
