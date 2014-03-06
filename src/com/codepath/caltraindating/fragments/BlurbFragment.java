package com.codepath.caltraindating.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.caltraindating.R;

public class BlurbFragment extends Fragment {

	public interface Listener {
		public void saveBlurb(String blurb);
		public String getBlurb();
		public void switchToRiders();
	}

	Listener listener;
	EditText etBlurb;
	Button btCancel;
	Button btSave;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_blurb, container, false);

		etBlurb = (EditText) v.findViewById(R.id.etBlurb);
		etBlurb.setText(listener.getBlurb());
		btCancel = (Button) v.findViewById(R.id.btCancel);
		btSave = (Button) v.findViewById(R.id.btSave);

		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.switchToRiders();
			}
		});

		btSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.saveBlurb(etBlurb.getText().toString());
				listener.switchToRiders();
			}
		});

		return v;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

}
