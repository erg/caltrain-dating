package com.codepath.caltraindating;

import java.util.Date;

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
import com.codepath.caltraindating.models.RiderModel;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class ChatActivity extends FragmentActivity implements ChatFragment.OnProfileClickListener {
    FragmentPagerAdapter fpAdapter;
    ViewPager vpChatting;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
        vpChatting = (ViewPager) findViewById(R.id.vpChatting);
        fpAdapter = new ChatPagerAdapter(getSupportFragmentManager());
        vpChatting.setAdapter(fpAdapter);
        vpChatting.setCurrentItem(0);
        
        Parse.initialize(this, "aqs7oI3BEXEXxaUpr8eRMFjASRtUUdErzg0LvY53", "VHOa2jLezZiXLQuwJAAiiSm3yMq2ofcLSS1qZ8oK");
        
        // Test creation of object
      	// ParseObject testObject = new ParseObject("ChatObject");
      	// testObject.put("chatMessage", "Hello!");
      	// testObject.put("chatTime", new Date());
      	// testObject.put("chatName", "Isolde");
      	// testObject.put("riderId", 123456);
      	// testObject.saveInBackground();        
      	
      	PushService.setDefaultPushCallback(this, ChatActivity.class);
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
	
	public static class ChatPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 2;

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
				    return new ChatFragment();
			    case 1:
				    return new RiderProfileFragment();
			    default:
				    return null;
			}
		}
	}

	@Override
	public void onProfileButtonClick(RiderModel rider) {
		Log.d("DEBUG", "Profile button click");
		vpChatting.setCurrentItem(1);
		// TODO: Should Check if RiderProfileFragment already invoked, if not, user rider info to bring up profile
		
	}

	
}
