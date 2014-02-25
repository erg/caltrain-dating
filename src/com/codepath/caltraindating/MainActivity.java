package com.codepath.caltraindating;


import java.security.MessageDigest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.codepath.caltraindating.fragments.CheckinFragment;
import com.codepath.caltraindating.fragments.LoginFragment;
import com.codepath.caltraindating.fragments.MyProfileFragment;
import com.codepath.caltraindating.fragments.RidersFragment;
import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements CheckinFragment.Listener, RidersFragment.Listener, LoginFragment.Listener {
	
	LoginFragment loginFragment;
	MyProfileFragment myProfileFragment;
	CheckinFragment checkinFragment;
	RidersFragment ridersFragment;
	ParseUser currentUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Parse.initialize(this, getResources().getString(R.string.parseId), getResources().getString(R.string.parseKey));
		ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));
		loginFragment = new LoginFragment();
		loginFragment.setListener(this);
		myProfileFragment = new MyProfileFragment();
		myProfileFragment.setListener(this);
		checkinFragment = new CheckinFragment();
		checkinFragment.setListener(this);
		ridersFragment = new RidersFragment();
		ridersFragment.setListener(this);
		
		Schedule.initSchedules(this);
		
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
			loginFragment.getFacebookDataInBackground(false);
			switchToFragment(checkinFragment);
		} else {
			switchToFragment(loginFragment);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commitAllowingStateLoss();
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
		switchToFragment(ridersFragment);
	}

	@Override
	public void setUser(ParseUser u) {
		currentUser = u;
	}

	@Override
	public void fbDataUpdated() {
		switchToFragment(checkinFragment);
//		switchToFragment(myProfileFragment);
	}
}
