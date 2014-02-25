package com.codepath.caltraindating.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.caltraindating.CaltrainUtils;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.caltraindating.fragments.LoginFragment.Listener;
import com.parse.ParseUser;

public class MyProfileFragment extends Fragment {

	Listener listener = null;
	private SmartFragmentStatePagerAdapter adapterViewPager;

	View view;
	ViewPager vpPager;
	TextView tvFirstName;
	TextView tvAge;
	TextView tvBlurb;
	String age;
	ParseUser currentUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_my_profile, container,
				false);
		vpPager = (ViewPager)view.findViewById(R.id.vpImages);

		currentUser = listener.getUser();

		tvFirstName = (TextView)view.findViewById(R.id.tvFirstName);
		tvFirstName.setText((String)currentUser.get("firstName"));

		tvAge = (TextView)view.findViewById(R.id.tvAge);
		age = CaltrainUtils.calculateAge(view.getContext(), (String)currentUser.get("birthday"));
		tvAge.setText(age);		
		
		tvBlurb = (TextView)view.findViewById(R.id.tvBlurb);
		//tvBlurb.setText((String)currentUser.get("blurb")); // todo
		tvBlurb.setMovementMethod(new ScrollingMovementMethod());

		adapterViewPager = new MyProfileFragment.MyPagerAdapter(
				this.getFragmentManager(), inflater, container,
				savedInstanceState, listener);

        vpPager.setAdapter(adapterViewPager);
		return view;
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
				Bundle savedInstanceState, Listener listener) {
			super(fragmentManager);
			
			this.listener = listener;
			this.currentUser = listener.getUser();

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

}
