package com.codepath.caltraindating;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.codepath.caltraindating.fragments.ChatFragment;
import com.codepath.caltraindating.fragments.RiderProfileFragment;
import com.codepath.caltraindating.models.ChatInParse;
import com.codepath.caltraindating.models.ChatModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class ChatActivity extends FragmentActivity implements ChatFragment.OnProfileClickListener {
    FragmentPagerAdapter fpAdapter;
    ViewPager vpChatting;
    
    String riderOwnId;
    String riderChatToId;
    ArrayList<ChatModel> currentChatHistory;
    
    BroadcastReceiver pushReceiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    ParseObject.registerSubclass(ChatInParse.class);
	    // Parse.initialize(this, "aqs7oI3BEXEXxaUpr8eRMFjASRtUUdErzg0LvY53", "VHOa2jLezZiXLQuwJAAiiSm3yMq2ofcLSS1qZ8oK");
	    Parse.initialize(this, getResources().getString(R.string.parseId), getResources().getString(R.string.parseKey));
	    
        // TODO: get passed riderOwnId and riderChatToId from last activity 
        this.riderOwnId = "nEcX6PawED";
        this.riderChatToId = "m2lsqXmktG";
        String channel = this.riderChatToId + "-" + this.riderOwnId;
        this.currentChatHistory = ChatHolder.getInstance().retrieve(riderChatToId);
        if (this.currentChatHistory==null) {
        	this.currentChatHistory = new ArrayList<ChatModel>();
          	PushService.subscribe(this, channel, ChatActivity.class);
        }
        // Set Parse push receiver
        IntentFilter intentFilter = new IntentFilter("com.codepath.caltraindating.CHAT");
        pushReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
      	        // String action = intent.getAction();
    	        // String channel = intent.getExtras().getString("com.parse.Channel");
	            // Log.d("DEBUG", "got action " + action + " on channel " + channel + " with:");
    	        try {
    	            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
    	 
      	            ChatModel chat = new ChatModel();
    	            chat.setChatMessage(json.getString("message"));
    	            chat.setChatTime(getCurrentMessageTime());
    	            chat.setComingMessage(true);
    	            chat.setChatName(json.getString("name"));
    	            if (json.getString("image")!=null && !json.getString("image").equals(""))
    	                chat.setChatImage(json.getString("image"));
    	            // Don't know why, but currentChatHistory is associated with chatList already
    	            // currentChatHistory.add(chat);
    	    
        	        ChatFragment f = (ChatFragment)getChatFragment();
        	        if (f!=null) {
        	        	f.addToChatList(chat);
        	        	f.getAdapterChatView().notifyDataSetChanged();
        	        	f.focusOnLatestMessge();
        	        }
        	        else {
        	        	// TODO: Add notification to rider list view
        	        }
    	        }
    	        catch (JSONException je) {
    	        	Log.d("DEBUG", "Parse push data error, mostly due to JSON object: " + je.getMessage());
    	        }
    	        
           }
        };
        registerReceiver(pushReceiver, intentFilter);

	    setContentView(R.layout.activity_chat);
        vpChatting = (ViewPager) findViewById(R.id.vpChatting);
        fpAdapter = new ChatPagerAdapter(getSupportFragmentManager());
        vpChatting.setAdapter(fpAdapter);
        vpChatting.setCurrentItem(0);
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
        
        /* Following code is used as a parse query testing
         * 
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", "user1@test.com");
        query.findInBackground(new FindCallback<ParseUser>() {
          public void done(List<ParseUser> objects, ParseException e) {
            if (e == null) {
		        	Log.d("DEBUG", "done with object=" + objects.get(0).getUsername());
		    } else {
		            Log.d("DEBUG", "Can't get Parse User for id=0" + "message: " + e.getMessage());
		    }
		  }
		});	   
        */		     
      	
        // We already set the call back above, so no need for default callback
      	// PushService.setDefaultPushCallback(this, ChatActivity.class);
      	ParseAnalytics.trackAppOpened(getIntent());
      	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

    @Override
    public void onBackPressed() {
        if (vpChatting.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
        	vpChatting.setCurrentItem(vpChatting.getCurrentItem() - 1);
        }
    }
	
	public class ChatPagerAdapter extends FragmentPagerAdapter {
		private int NUM_ITEMS = 2;

		public ChatPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			    case 0:
			    	return ChatFragment.newInstance(riderOwnId, riderChatToId, currentChatHistory);
			    	// return new ChatFragment();
			    case 1:
				    return new RiderProfileFragment();
			    default:
				    return null;
			}
		}
	}

	@Override
	public void onProfileButtonClick(ParseUser rider) {
		vpChatting.setCurrentItem(1);
		// TODO: Should Check if RiderProfileFragment already invoked, if not, user rider info to bring up profile
		
	}

	public void setCurrentChatHistory(ArrayList<ChatModel> currentChatHistory) {
		this.currentChatHistory = currentChatHistory;
	}

	public ArrayList<ChatModel> getCurrentChatHistory() {
		return currentChatHistory;
	}

	public String getRiderOwnId() {
		return riderOwnId;
	}

	public String getRiderChatToId() {
		return riderChatToId;
	}

	private String getCurrentMessageTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		return sdf.format(new Date());
	}
	
    public Fragment getChatFragment() {
    	if (vpChatting.getCurrentItem()==0)
    		return (ChatFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.vpChatting+":0");
    	else
    		return null;
    }

	@Override
	protected void onStop() {
		super.onStop();
		ChatHolder.getInstance().saveOrUpdate(riderChatToId, currentChatHistory);
		unregisterReceiver(pushReceiver);
	}
    
    
}
