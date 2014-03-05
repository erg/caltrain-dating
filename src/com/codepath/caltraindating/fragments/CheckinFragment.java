package com.codepath.caltraindating.fragments;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.caltraindating.MainActivity;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.TrainDialog;
import com.codepath.caltraindating.adapters.StopAdapter;
import com.codepath.caltraindating.adapters.TrainAdapter;
import com.codepath.caltraindating.models.Callback;
import com.codepath.caltraindating.models.Checkin;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Stop;
import com.codepath.caltraindating.models.Train;
import com.parse.ParsePush;
import com.parse.ParseUser;

public class CheckinFragment extends Fragment implements OnClickListener, TrainDialog.Listener{
	
	Button checkIn;
	Spinner trainNumbers;
	Spinner trainStops;
	Button addTrain;
	Listener listener;
	ArrayList<Train> recentTrains = new ArrayList<Train>();
	TrainAdapter trainAdapter;
	ArrayList<Stop> trainStations = new ArrayList<Stop>();
	StopAdapter stationAdapter;
	TrainDialog trainDialog;
	SharedPreferences sharedPref;
	Train currentTrain = null;
	RelativeLayout stationCheckin;
	TextView tvCheckinStop;
	
	protected LocationManager locationManager;
	
	public interface Listener{
		ParseUser getUser();
		void checkedIn(Train train);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		sharedPref = getActivity().getSharedPreferences(
		        getString(R.string.preferences_file), Context.MODE_PRIVATE);
		
		View v = inflater.inflate(R.layout.fragment_checkin, container,false);
		stationCheckin = (RelativeLayout)v.findViewById(R.id.rlStationCheckin);
		checkIn = (Button)v.findViewById(R.id.btCheckIn);
		addTrain = (Button)v.findViewById(R.id.btAddTrain);
		trainNumbers = (Spinner)v.findViewById(R.id.spTrainNumber);
		trainStops = (Spinner)v.findViewById(R.id.spTrainStop);
		tvCheckinStop = (TextView)v.findViewById(R.id.tvCheckinStop);
		
		initRecentTrains();
		addTrain.setOnClickListener(this);
		trainDialog = new TrainDialog();
		trainDialog.setListener(this);
		trainDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogGrey);
		
		stationAdapter = new StopAdapter(getActivity(),R.layout.spinner_item_grey,trainStations, StopAdapter.FORMAT_NAME);
		trainStops.setAdapter(stationAdapter);
		
		trainAdapter = new TrainAdapter(getActivity(), R.layout.spinner_item_red, recentTrains);
		trainNumbers.setAdapter(trainAdapter);
		trainNumbers.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				currentTrain = (Train) parent.getItemAtPosition(pos);
				updateStations();
				showStationSection();
			}

			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		trainStops.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				Stop s = (Stop) parent.getItemAtPosition(pos);
				if(currentTrain != null){
					currentTrain.setUsualStop(s.getStationId());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		checkIn.setOnClickListener(this);
		
		currentTrain = (Train) trainNumbers.getSelectedItem();
		updateStations();
		
		return v;
		
	}
	
	public void setListener(Listener l){
		this.listener = l;
	}
	
	private void showStationSection(){
		if(stationCheckin.getVisibility() != View.VISIBLE){
			final Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
			animationFadeIn.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					stationCheckin.setVisibility(View.VISIBLE);
				}
			});
			stationCheckin.startAnimation(animationFadeIn);
		}
	}
	
	private void updateStations(){
		if(currentTrain != null){
			trainStations.clear();
			ArrayList<Stop> stopsAhead = Schedule.getStopsAheadOfTrain(currentTrain, Schedule.getNow());
			if(stopsAhead.size()>0){
				checkIn.setVisibility(View.VISIBLE);
				trainStops.setVisibility(View.VISIBLE);
				tvCheckinStop.setText("Where are you getting off?");
				trainStations.addAll(stopsAhead);
				stationAdapter.notifyDataSetChanged();
				trainStops.setSelection(Train.indexOfUsualStop(stopsAhead, currentTrain));
				currentTrain.setUsualStop(stopsAhead.get(0).getStationId());
			}else{
				checkIn.setVisibility(View.INVISIBLE);
				trainStops.setVisibility(View.INVISIBLE);
				tvCheckinStop.setText("this train isn't running right now");
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btAddTrain){
			showTrainDialog();
		}else if(id == R.id.btCheckIn){
			checkIn();
		}
	}
	
	private void showTrainDialog(){
		FragmentManager fm = getActivity().getSupportFragmentManager();
        trainDialog.show(fm, "dialog_train_nums");
	}
	
	private void checkIn(){
		Date now = Schedule.getNow();
		double longitude = 0.0d;
		double latitude = 0.0d;
		currentTrain.setLastSelected(now);
		currentTrain.save();
		String currentUserId = listener.getUser().getObjectId();
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
		    longitude = location.getLongitude();	
		    latitude = location.getLatitude();
		}
		else {
			if (currentUserId.equals("nEcX6PawED")) {
			    latitude = 37.787970d;
			    longitude = -122.399536d;
			}
			else if (currentUserId.equals("S0P8makZSA")) {
				latitude = 37.789148d;
				longitude = -122.401326d;
			}
			else if (currentUserId.equals("oeHLX39OwU")) {
				latitude = 37.788219d;
				longitude = -122.399996d;
			}
		}
		Checkin c = new Checkin(listener.getUser(), currentTrain.getId(), currentTrain.getUsualStop(), now.getTime(), longitude, latitude);
		c.save();
		MainActivity mainActivity = (MainActivity)getActivity();
		mainActivity.setLatitude(latitude);
		mainActivity.setLongitude(longitude);
		listener.checkedIn(currentTrain);
	    try {
	        JSONObject chatData = new JSONObject("{\"action\": \"com.codepath.caltraindating.CHAT\", \"message\": \"checkin\", \"user\": \"" + currentUserId + "\"}");
	        
            ParsePush push = new ParsePush();
            String channel = "CHECK-IN";
            push.setChannel(channel);
            push.setData(chatData);
            push.sendInBackground();
        }
        catch (JSONException je) {
	        Log.w("WARN", "send message error: " + je.getMessage());
        }
	}
	
	private void initRecentTrains(){
		Log.e("tag", "init trains");
		Train.getRecentTrains(listener.getUser(), new Callback<ArrayList<Train>>(){
			@Override
			public void complete(ArrayList<Train> trains) {
				Log.e("tag", "Got trains from parse: "+trains.size());
				recentTrains.clear();
				if(trains.size()>0){
					currentTrain = trains.get(0);
				}else{
					showTrainDialog();
				}
				for(Train t: trains){
					recentTrains.add(t);
				}
				trainAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void insertRecentTrain(Train t){
		int pos = recentTrains.indexOf(t);
		if(pos >= 0){
			recentTrains.remove(pos);
		}
		recentTrains.add(0,t);
		trainAdapter.notifyDataSetChanged();
	}
	/*
	private void saveRecentTrains(){
		String trains = "";
		for(String t : recentTrains){
			trains += t + ",";
		}
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("recentTrains", trains);
		editor.commit();
	}*/

	@Override
	public void onStopSelected(Stop s) {
		currentTrain = new Train(listener.getUser(),s.getTrain());
		insertRecentTrain(currentTrain);
		trainNumbers.setSelection(0);
		updateStations();
	}
}
