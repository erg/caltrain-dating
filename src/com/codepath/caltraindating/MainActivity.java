package com.codepath.caltraindating;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.caltraindating.models.Train;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {
	
	LoginFragment loginFragment;
	CheckinFragment checkinFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Parse.initialize(this, getResources().getString(R.string.parseId), getResources().getString(R.string.parseKey));
		ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));
		loginFragment = new LoginFragment();
		checkinFragment = new CheckinFragment();
		Train.initSchedules(this);
		
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
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
		  // do stuff with the user
			switchToFragment(checkinFragment);
		} else {
		  // show the signup or login screen
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
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			  @Override
			  public void done(ParseUser user, com.parse.ParseException err) {
			    if (user == null) {
			      Log.d("MyApp", "Uh oh. The user cancelled the Facebook login. "+err.getMessage());
			    } else if (user.isNew()) {
			      Log.d("MyApp", "User signed up and logged in through Facebook!");
			      switchToFragment(checkinFragment);
			    } else {
			      Log.d("MyApp", "User logged in through Facebook!");
			      switchToFragment(checkinFragment);
			    }
			  }
			});
	}

}
