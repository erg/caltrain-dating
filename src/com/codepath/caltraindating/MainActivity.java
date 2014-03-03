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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

import com.codepath.caltraindating.fragments.ChatFragment;
import com.codepath.caltraindating.fragments.CheckinFragment;
import com.codepath.caltraindating.fragments.LoginFragment;
import com.codepath.caltraindating.fragments.RidersFragment;
import com.codepath.caltraindating.fragments.ViewProfileFragment;
import com.codepath.caltraindating.models.ChatInParse;
import com.codepath.caltraindating.models.ChatModel;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity 
                          implements CheckinFragment.Listener, RidersFragment.Listener, LoginFragment.Listener, 
                                     ChatFragment.OnProfileClickListener, ChatFragment.OnBackClickListener {
	
	public final static String LOGIN_FRAGMENT_TAG = "LOGIN";
	public final static String CHECKIN_FRAGMENT_TAG = "CHECKIN";
	public final static String RIDERS_FRAGMENT_TAG = "RIDERS";
	public final static String CHAT_FRAGMENT_TAG = "CHAT";
	public final static String PROFILE_FRAGMENT_TAG = "PROFILE";
	
	LoginFragment loginFragment;
	ViewProfileFragment myProfileFragment;
	CheckinFragment checkinFragment;
	RidersFragment ridersFragment;
	
	ParseUser currentUser = null;
	String currentUserId = null;
	
    BroadcastReceiver pushReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    ParseObject.registerSubclass(ChatInParse.class);
		Parse.initialize(this, getResources().getString(R.string.parseId), getResources().getString(R.string.parseKey));
		ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));
		loginFragment = new LoginFragment();
		loginFragment.setListener(this);
		myProfileFragment = new ViewProfileFragment();
		myProfileFragment.setListener(this);
		checkinFragment = new CheckinFragment();
		checkinFragment.setListener(this);
		ridersFragment = new RidersFragment();
		ridersFragment.setListener(this);
		
		Schedule.initSchedules(this);

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
        
        // Set Parse push receiver
        IntentFilter intentFilter = new IntentFilter("com.codepath.caltraindating.CHAT");
        pushReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
      	        String action = intent.getAction();
    	        String channel = intent.getExtras().getString("com.parse.Channel");
	            // Log.d("DEBUG", "got push action " + action + " on channel " + channel);
      	        // From channel, get the user id who sends the message
    	        String[] chatPartners = channel.split("-");
    	        String chatFromUserId = chatPartners[0];
    	        deliverChatMessage(intent, chatFromUserId);
           }
        };
        registerReceiver(pushReceiver, intentFilter);
		
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
		switchToFragment(profileFragment, PROFILE_FRAGMENT_TAG);
		
	}

	@Override
	public void onBackButtonClick() {
		switchToFragment(ridersFragment, RIDERS_FRAGMENT_TAG);
	}
	
	@Override
    public void onBackPressed() {
		ViewProfileFragment fProfile = (ViewProfileFragment)getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
        if (fProfile!=null && fProfile.isVisible() && fProfile.getPosition() == -1) {
        	ChatFragment chatFragment = ChatFragment.newInstance(getCurrentUserId(), fProfile.getProfileUserId());
			switchToFragment(chatFragment, "CHAT");
        } else {
        	ChatFragment fChat = (ChatFragment)getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
    		if (fChat!=null && fChat.isVisible()) {
    			switchToFragment(ridersFragment, RIDERS_FRAGMENT_TAG);
    		}
    		else 
        	    super.onBackPressed();
        }
    }
	
	public String getCurrentUserId() {
		return currentUserId;
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

	@Override
	protected void onStop() {
		unregisterReceiver(pushReceiver);
		super.onStop();
	}
	
	
	
}
