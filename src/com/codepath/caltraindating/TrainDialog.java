package com.codepath.caltraindating;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.codepath.caltraindating.adapters.StopAdapter;
import com.codepath.caltraindating.models.Stop;
import com.codepath.caltraindating.models.Train;

public class TrainDialog extends Dialog implements OnClickListener{
	
	Activity context;
	final Long timeWindow = (long) 3600000*24;
	Listener listener = null;
	Button done;
	Stop selected = null;
	

	public interface Listener {
		public void onStopSelected(Stop s);
	}
	public TrainDialog(Activity context) {
		super(context);
		this.context = context;
	}
	public TrainDialog(Activity context, Listener l){
		super(context);
		this.context = context;
		listener = l;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_train_nums);
		setTitle("What train are you on?");
		
		done = (Button)findViewById(R.id.btTrainPick);
		done.setOnClickListener(this);
		
		Spinner trainPick = (Spinner)findViewById(R.id.spTrainPick);
		final StopAdapter trainAdapter = new StopAdapter(context,android.R.layout.simple_spinner_item,Train.getStopsByTimePretty(timeWindow,null),StopAdapter.FORMAT_LONG);
		trainPick.setAdapter(trainAdapter);
		trainPick.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
					selected = (Stop) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		Spinner stationPick = (Spinner)findViewById(R.id.spStationPick);
		ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,Train.stopNamesList);
		stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationPick.setAdapter(stationAdapter);
		stationPick.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				trainAdapter.clear();
				for(Stop s: Train.getStopsByTimePretty(timeWindow,pos)){
					trainAdapter.add(s);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btTrainPick){
			if(selected != null){
				listener.onStopSelected(selected);
				dismiss();
			}
		}
	}

}
