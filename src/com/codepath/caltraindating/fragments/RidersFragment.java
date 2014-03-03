package com.codepath.caltraindating.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.ChatViewAdapter;
import com.codepath.caltraindating.adapters.RiderListAdapter;
import com.codepath.caltraindating.models.Callback;
import com.codepath.caltraindating.models.Checkin;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.parse.ParseUser;

public class RidersFragment extends Fragment{
	Train currentTrain = null;
	Listener listener = null;
	ListView lvRiders;
	RiderListAdapter riderListAdapter;
	Activity activity;
	boolean loaded = false;
	// XXX: Sorry about static!
	static ArrayList<Checkin> checkins;
	
	public interface Listener{
		ParseUser getUser();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_riders, container,false);
		lvRiders = (ListView)v.findViewById(R.id.lvRiders);
		
		if(!loaded) {
			loadRiders(currentTrain);
		} else {
			lvRiders.setAdapter(riderListAdapter);
		}
		return v;		
	}


    public void showProgressBar() {
	    getActivity().setProgressBarIndeterminateVisibility(true);
	}
	    
	public void hideProgressBar() {
	  	getActivity().setProgressBarIndeterminateVisibility(false);
	}


	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

	private void loadRiders(Train t){
		showProgressBar();
		Checkin.getCheckins(listener.getUser(), currentTrain, Schedule.getNow(), new Callback<ArrayList<Checkin>>(){
			@Override
			public void complete(ArrayList<Checkin> checkins) {
				RidersFragment.checkins = checkins;
				riderListAdapter = new RiderListAdapter(getActivity(), activity, (ArrayList<Checkin>) checkins);
		        lvRiders.setAdapter(riderListAdapter);
				hideProgressBar();
				for(Checkin c: checkins){
					Log.d("tag","got checkin: "+c.getUser().getObjectId());
//					Log.d("tag","got checkin: "+c.getUser().getString("firstName"));
//					Log.d("tag","got checkin: "+c.getUser().getString("lastName"));
//					Log.d("tag","got checkin: "+c.getUser().getString("birthday"));
				}
				loaded = true;
			}
		});
	}

	public RiderListAdapter getRiderListAdapter() {
		return riderListAdapter;
	}

	public Train getCurrentTrain() {
		return currentTrain;
	}

	public void setCurrentTrain(Train currentTrain) {
		this.currentTrain = currentTrain;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public void setMessageNotice(String msgFromUserId) {
		for (Checkin c: checkins) {
			if (c.getUser().getObjectId().equals(msgFromUserId)) {
				riderListAdapter.notifyDataSetChanged();
			}
		}
	}
}
