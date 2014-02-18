package com.codepath.caltraindating;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.caltraindating.models.Schedule;
import com.codepath.caltraindating.models.Train;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements CheckinFragment.Listener, RidersFragment.Listener {
	
	LoginFragment loginFragment;
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
		checkinFragment = new CheckinFragment();
		ridersFragment = new RidersFragment();
		checkinFragment.setListener(this);
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
	
	public void fbLogin(View v){
		String[] params = {"user_birthday","user_photos"};
		ParseFacebookUtils.logIn(Arrays.asList(params),this, new LogInCallback() {
			  @Override
			  public void done(ParseUser user, com.parse.ParseException err) {
				  currentUser = user;
			    if (user == null) {
			      Log.d("tag", "Uh oh. The user cancelled the Facebook login. "+err.getMessage());
			      return;
			    }
			    Log.d("tag","user signed up: "+user.getObjectId());
			    getFacebookDataInBackground();
			  }
			});
	}
	
	private void getFacebookDataInBackground() {
		Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
		    @Override
		    public void onCompleted(GraphUser user, Response response) {
		      if (user != null) {
		        currentUser.put("fbId", user.getId());
		        currentUser.put("firstName", user.getFirstName());
		        currentUser.put("lastName", user.getLastName());
		        currentUser.put("birthday", user.getBirthday());
		        currentUser.saveInBackground();
		        switchToFragment(checkinFragment);
		      }else{
		    	  Log.e("tag", "error getting facebook data");
		      }
		    }
		  }).executeAsync();
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

}
