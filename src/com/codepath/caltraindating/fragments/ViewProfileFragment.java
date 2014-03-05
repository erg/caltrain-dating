package com.codepath.caltraindating.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.codepath.caltraindating.CaltrainUtils;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.caltraindating.fragments.LoginFragment.Listener;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewProfileFragment extends Fragment {

	Listener listener = null;
	private SmartFragmentStatePagerAdapter adapterViewPager;

	View view;
	ViewPager vpPager;
	TextView tvFirstName;
	TextView tvAge;
	TextView tvBlurb;
	String age;
	ParseUser currentUser;
	
	boolean isUser;
	int position;
	
	public static ViewProfileFragment newInstance(boolean isUser, int position) {
		ViewProfileFragment myProfileFragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("isUser", isUser);
        args.putInt("position", position);
        myProfileFragment.setArguments(args);
        return myProfileFragment;
	}
	
	public static ViewProfileFragment newInstance(String userId) {
		ViewProfileFragment myProfileFragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("isUser", false);
        args.putInt("position", -1);
        args.putString("userId",userId);
        myProfileFragment.setArguments(args);
        return myProfileFragment;
	}
	
	public ParseUser getMyUser() {
		if(isUser) {
			return listener.getUser();
		} else {
			return RidersFragment.checkins.get(position).getUser();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		isUser = getArguments().getBoolean("isUser");
		position = getArguments().getInt("position");
		String currentUserId = getArguments().getString("userId");
		
		view = inflater.inflate(R.layout.fragment_my_profile, container,
				false);
		vpPager = (ViewPager)view.findViewById(R.id.vpImages);

		if (currentUserId==null)
		    currentUser = getMyUser();
		else
			currentUser = getParseUserById(currentUserId);
		
		tvFirstName = (TextView)view.findViewById(R.id.tvFirstName);
		tvFirstName.setText((String)currentUser.get("firstName"));

		tvAge = (TextView)view.findViewById(R.id.tvAge);
		age = CaltrainUtils.calculateAge(view.getContext(), (String)currentUser.get("birthday"));
		tvAge.setText(age);		
		
		tvBlurb = (TextView)view.findViewById(R.id.tvBlurb);
		tvBlurb.setText((String)currentUser.get("blurb")); // todo
		tvBlurb.setMovementMethod(new ScrollingMovementMethod());

		adapterViewPager = new ViewProfileFragment.MyPagerAdapter(
				this.getFragmentManager(), inflater, container,
				savedInstanceState, currentUser);

        vpPager.setAdapter(adapterViewPager);

		return view;
	}
	
	// Hide keyboard on viewing profile
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	// Like, what if they are in the app and have a birthday? Recalculate. 
	@Override
	public void onResume() {
		super.onResume();
		age = CaltrainUtils.calculateAge(view.getContext(), (String)currentUser.get("birthday"));
		tvAge.setText(age);		
	}
	
	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public String getProfileUserId() {
		return currentUser.getObjectId();
	}

	public int getPosition() {
		return position;
	}

	// Extend from SmartFragmentStatePagerAdapter now instead for more dynamic
	// ViewPager items
	public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
		ArrayList<String> pictureUrls;
		ArrayList<ProfileImageFragment> fragments;
		View view;
		ParseUser currentUser;
		Listener listener;

		public MyPagerAdapter(FragmentManager fragmentManager,
				LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState, ParseUser user) {
			super(fragmentManager);
			
			this.currentUser = user;
			view = inflater.inflate(R.layout.fragment_profile_image,
					container, false);
			
			this.pictureUrls = (ArrayList<String>) currentUser.get("imgBigSrcs");
			fragments = new ArrayList<ProfileImageFragment>();
			
			for (int i = 0; i < pictureUrls.size(); i++) {
				fragments.add(ProfileImageFragment.newInstance(i,
						pictureUrls.get(i)));
			}
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return pictureUrls.size();
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			//Toast.makeText(view.getContext(), "GET FRAGMENT" + position, Toast.LENGTH_SHORT).show();

			return fragments.get(position);
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

	}

	private ParseUser getParseUserById(final String userId) {
		ParseUser puSet = null;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        try {
            List<ParseUser> users = query.find();
            if (users!=null && users.size()>0) {
            	puSet = users.get(0);
            }
            else {
            	Log.d("DEBUG", "sync done but no result found for id=" + userId);
            }
        }
        catch (ParseException pe) {
	        Log.d("DEBUG", "Parse error message: " + pe.getMessage());
		};
        return puSet;
	}
}
