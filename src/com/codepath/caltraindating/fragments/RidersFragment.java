package com.codepath.caltraindating.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.codepath.caltraindating.ChatHolder;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.RiderListAdapter;
import com.codepath.caltraindating.models.Callback;
import com.codepath.caltraindating.models.ChatInParse;
import com.codepath.caltraindating.models.Checkin;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class RidersFragment extends Fragment implements OnClickListener{
	Train currentTrain = null;
	Listener listener = null;
	PullToRefreshListView lvRiders;
	RiderListAdapter riderListAdapter;
	Activity activity;
	boolean loaded = false;
	// XXX: Sorry about static!
	static ArrayList<Checkin> checkins;
	RelativeLayout rlRiders;
	RelativeLayout rlInvite;
	Button btInvite;
	
	public interface Listener{
		ParseUser getUser();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_riders, container,false);
		lvRiders = (PullToRefreshListView)v.findViewById(R.id.lvRiders);
        if(!loaded) {
            loadRiders(currentTrain);
        } else {
            lvRiders.setAdapter(riderListAdapter);
        }
        // Set a listener to be invoked when the list should be refreshed.
        lvRiders.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	riderListAdapter.clear();
            	loadRiders(currentTrain);
            	lvRiders.onRefreshComplete();
            }
        });		
        btInvite = (Button)v.findViewById(R.id.btInvite);
        rlRiders = (RelativeLayout)v.findViewById(R.id.rlRiders);
        rlInvite = (RelativeLayout)v.findViewById(R.id.rlInvite);
        btInvite.setOnClickListener(this);
        PushService.subscribe(getActivity(), "CHECK-IN", activity.getClass());
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActivity().getActionBar().show();
	}
	    
	public void loadRiders(Train t){
		showProgressBar();
		Checkin.getCheckins(listener.getUser(), currentTrain, Schedule.getNow(), new Callback<ArrayList<Checkin>>(){
			@Override
			public void complete(ArrayList<Checkin> checkins) {
				RidersFragment.checkins = checkins;
				riderListAdapter = new RiderListAdapter(getActivity(), activity, (ArrayList<Checkin>) checkins);
		        lvRiders.setAdapter(riderListAdapter);
				hideProgressBar();
				for(Checkin c: checkins){
					final String targetUserId = c.getUser().getObjectId();
			    	ParseQuery<ChatInParse> queryNewMessage = ParseQuery.getQuery(ChatInParse.class);
			    	queryNewMessage.whereEqualTo("riderChatFrom", c.getUser());
			    	queryNewMessage.whereEqualTo("riderChatTo", listener.getUser());
			    	queryNewMessage.whereEqualTo("isRead", false);
			    	queryNewMessage.countInBackground(new CountCallback() {
			   		    public void done(int count, ParseException e) {
			    		    if (e == null) {
			    		        if (count>0) {
			    		        	ChatHolder.getInstance().setNewMessage(targetUserId);
			    			    	riderListAdapter.notifyDataSetChanged();
			    		        }
			    		    }
			    		}
			        });
					Log.d("tag","got checkin: "+c.getUser().getObjectId());
//					Log.d("tag","got checkin: "+c.getUser().getString("firstName"));
//					Log.d("tag","got checkin: "+c.getUser().getString("lastName"));
//					Log.d("tag","got checkin: "+c.getUser().getString("birthday"));
				}
				if(checkins == null || checkins.size() == 0){
					rlRiders.setVisibility(View.GONE);
					rlInvite.setVisibility(View.VISIBLE);
				}else{
					rlRiders.setVisibility(View.VISIBLE);
					rlInvite.setVisibility(View.GONE);
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
	
	private void sendRequestDialog() {
	    Bundle params = new Bundle();
	    params.putString("message", "I'm inviting you to Caltrix, the best way to meet on the train!");

	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {
	                @Override
	                public void onComplete(Bundle values,
	                    FacebookException error) {   
	                }

	            })
	            .build();
	    requestsDialog.show();
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btInvite){
			sendRequestDialog();
		}
	}
}
