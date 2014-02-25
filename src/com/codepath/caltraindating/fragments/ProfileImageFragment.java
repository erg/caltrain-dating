package com.codepath.caltraindating.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.caltraindating.R;
import com.squareup.picasso.Picasso;

public class ProfileImageFragment extends Fragment {
	
	int page;
	String url;
	ImageView imPicture;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
		imPicture = (ImageView)view.findViewById(R.id.imPicture);
		page = this.getArguments().getInt("page");
		url = this.getArguments().getString("url");
	    Picasso.with(view.getContext()).load(url).into(imPicture);
	    return view;
	}
	
    public static ProfileImageFragment newInstance(int page, String url) {
    	ProfileImageFragment fragment = new ProfileImageFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

}
