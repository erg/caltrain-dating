package com.codepath.caltraindating;


import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.codepath.caltraindating.fragments.BlurbFragment;
import com.codepath.caltraindating.fragments.ChatFragment;
import com.codepath.caltraindating.fragments.CheckinFragment;
import com.codepath.caltraindating.fragments.LoginFragment;
import com.codepath.caltraindating.fragments.RidersFragment;
import com.codepath.caltraindating.fragments.ViewProfileFragment;
import com.codepath.caltraindating.models.ChatModel;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity 
                          implements CheckinFragment.Listener, RidersFragment.Listener, LoginFragment.Listener, 
                                     ChatFragment.OnProfileClickListener, ChatFragment.OnBackClickListener,
                                     BlurbFragment.Listener {
	
	public final static String LOGIN_FRAGMENT_TAG = "LOGIN";
	public final static String CHECKIN_FRAGMENT_TAG = "CHECKIN";
	public final static String RIDERS_FRAGMENT_TAG = "RIDERS";
	public final static String CHAT_FRAGMENT_TAG = "CHAT";
	public final static String PROFILE_FRAGMENT_TAG = "PROFILE";
	public final static String BLURB_FRAGMENT_TAG = "BLURB";
	
	LoginFragment loginFragment;
	ViewProfileFragment myProfileFragment;
	CheckinFragment checkinFragment;
	RidersFragment ridersFragment;
	BlurbFragment blurbFragment;
	
	ParseUser currentUser = null;
	String currentUserId = null;
    BroadcastReceiver pushReceiver;
        
    double longitude;
    double latitude;

    // XXX: Does this fix it? Also, how slow is making new ones each time vs saving it in the class?
    private void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter("com.codepath.caltraindating.CHAT");
        pushReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
      	        String action = intent.getAction();
    	        String channel = intent.getExtras().getString("com.parse.Channel");
	            // Log.d("DEBUG", "got push action " + action + " on channel " + channel);
    	        if (channel.equals("CHECK-IN")) {
    	        	CheckInNotice(intent);
    	        }
    	        else {
      	            // From channel, get the user id who sends the message
    	            String[] chatPartners = channel.split("-");
    	            String chatFromUserId = chatPartners[0];
    	            deliverChatMessage(intent, chatFromUserId);
    	        }
           }
        };
        
        registerReceiver(pushReceiver, intentFilter);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		loginFragment = new LoginFragment();
		loginFragment.setListener(this);
		
		myProfileFragment = new ViewProfileFragment();
		myProfileFragment.setListener(this);
		
		checkinFragment = new CheckinFragment();
		checkinFragment.setListener(this);
		
		ridersFragment = new RidersFragment();
		ridersFragment.setListener(this);
		
		blurbFragment = new BlurbFragment();
		blurbFragment.setListener(this);

		Schedule.initSchedules(this);

		ParseAnalytics.trackAppOpened(getIntent());

		//helpful for finding your hashkey, keep here
		try {
		    PackageInfo info = getPackageManager().getPackageInfo(
		            "com.codepath.caltraindating", PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) {
		        MessageDigest md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        Log.i("MY KEY HASH:",
		                Base64.encodeToString(md.digest(), Base64.DEFAULT));
		    }
		} catch (Exception e) {}
		
		currentUser = ParseUser.getCurrentUser();

		if (currentUser != null) {
			currentUserId = currentUser.getObjectId();
			loginFragment.getFacebookDataInBackground(false);
			switchToFragment(checkinFragment, CHECKIN_FRAGMENT_TAG);
		} else {
			switchToFragment(loginFragment, LOGIN_FRAGMENT_TAG);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void switchToFragment(Fragment newFrag, String fragTag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag, fragTag)
                .commitAllowingStateLoss();
    }

	public void slideRightToFragment(Fragment newFrag, String fragTag) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();       
		transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
		transaction.replace(R.id.fragment_container, newFrag, fragTag);
		transaction.commitAllowingStateLoss();
    }

	public void slideLeftToFragment(Fragment newFrag, String fragTag) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();       
		transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
		transaction.replace(R.id.fragment_container, newFrag, fragTag);
		transaction.commitAllowingStateLoss();
    }
	
	public void switchToRiders() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();       
		//transaction.setCustomAnimations(R.anim.fadein, R.anim.fadein);
		transaction.replace(R.id.fragment_container, this.ridersFragment, RIDERS_FRAGMENT_TAG);
		transaction.commitAllowingStateLoss();
	}
	
	// XXX: Do this instead?
	// http://stackoverflow.com/questions/7992216/android-fragment-handle-back-button-press
	public void switchToFragmentBack(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .addToBackStack("fooTag").commitAllowingStateLoss();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	@Override
	public ParseUser getUser() {
		return currentUser;
	}

	@Override
	public void checkedIn(Train train) {
		ridersFragment.setCurrentTrain(train);
		switchToFragment(ridersFragment, RIDERS_FRAGMENT_TAG);
	}

	@Override
	public void setUser(ParseUser u) {
		currentUser = u;
	}

	@Override
	public void fbDataUpdated() {
		switchToFragment(checkinFragment, CHECKIN_FRAGMENT_TAG);
	}
	
	@Override
	public void onProfileButtonClick(String riderId) {
		ViewProfileFragment profileFragment = ViewProfileFragment
				.newInstance(riderId);
		slideLeftToFragment(profileFragment, PROFILE_FRAGMENT_TAG);
		
	}

	@Override
	public void onBackButtonClick() {
		slideRightToFragment(ridersFragment, RIDERS_FRAGMENT_TAG);
	}
	
	@Override
    public void onBackPressed() {
		ViewProfileFragment fProfile = (ViewProfileFragment)getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
        if (fProfile!=null && fProfile.isVisible() && fProfile.getPosition() == -1) {
        	ChatFragment chatFragment = ChatFragment.newInstance(getCurrentUserId(), fProfile.getProfileUserId());
        	slideRightToFragment(chatFragment, "CHAT");
        } else {
        	ChatFragment fChat = (ChatFragment)getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
    		if (fChat!=null && fChat.isVisible()) {
    			slideRightToFragment(ridersFragment, RIDERS_FRAGMENT_TAG);
    		}
    		else 
        	    super.onBackPressed();
        }
    }
	
	public String getCurrentUserId() {
		return currentUserId;
	}

	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	private String getCurrentMessageTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		return sdf.format(new Date());
	}
	
	public void deliverChatMessage(Intent i, String chatFromUserId) {
        try {
            JSONObject json = new JSONObject(i.getExtras().getString("com.parse.Data"));
 
	        ChatModel chat = new ChatModel();
            chat.setChatMessage(json.getString("message"));
            // Show the time when this message is received, not the time when message is sent
            chat.setChatTime(getCurrentMessageTime());
            chat.setComingMessage(true);
            chat.setChatName(json.getString("name"));
            if (json.getString("image")!=null && !json.getString("image").equals(""))
                chat.setChatImage(json.getString("image"));
            // Don't know why, but currentChatHistory is associated with chatList already
            // currentChatHistory.add(chat);
 
            
    		ChatFragment fChat = (ChatFragment)getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
    		if (fChat!=null && fChat.isVisible() && fChat.getRiderChatToId().equals(chatFromUserId)) {
    			fChat.addToChatList(chatFromUserId, chat);
    			fChat.getAdapterChatView().notifyDataSetChanged();
    			fChat.focusOnLatestMessge();
    		}
    		else {
    			if (ChatHolder.getInstance().retrieve(chatFromUserId)==null)
    				ChatHolder.getInstance().initialize(chatFromUserId);
    			ChatHolder.getInstance().addNewChat(chatFromUserId, chat, true);
    		}
    		RidersFragment fRiders = (RidersFragment)getSupportFragmentManager().findFragmentByTag(RIDERS_FRAGMENT_TAG);
    		if (fRiders!=null &&fRiders.isVisible()) {
    			fRiders.setMessageNotice(chatFromUserId);
    			fRiders.getRiderListAdapter().notifyDataSetChanged();
    		}
        }
        catch (JSONException je) {
        	Log.w("WARN", "Parse push data error, mostly due to JSON object: " + je.getMessage());
        }
		
	}
	
	public void CheckInNotice(Intent i) {
        try {
            JSONObject json = new JSONObject(i.getExtras().getString("com.parse.Data"));

            String userId = json.getString("user");
            if (userId.equals(currentUserId)) {
            	Log.d("DEBUG", "SELF CHECKIN");
            	return;
            }
    		RidersFragment fRiders = (RidersFragment)getSupportFragmentManager().findFragmentByTag(RIDERS_FRAGMENT_TAG);
    		if (fRiders!=null &&fRiders.isVisible()) {
    			Toast.makeText(this, "New rider just checked in, pull to refresh", Toast.LENGTH_SHORT).show();
    		}
        }
        catch (JSONException je) {
        	Log.w("WARN", "Parse push data error, mostly due to JSON object: " + je.getMessage());
        }
		
	}

	public void onEditBlurb(MenuItem item) {
		switchToFragment(blurbFragment, BLURB_FRAGMENT_TAG);
	}

	@Override
	public void saveBlurb(String blurb) {
		Log.d("DEBUG", "Saving blurb: " + blurb);
	}
	
	// Must register here according to:
	// http://stackoverflow.com/questions/7887169/android-when-to-register-unregister-broadcast-receivers-created-in-an-activity
	@Override
	protected void onResume() {
		registerReceivers();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(pushReceiver);
		super.onPause();
	}
	
}
