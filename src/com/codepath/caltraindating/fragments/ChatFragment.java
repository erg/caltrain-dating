package com.codepath.caltraindating.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.ChatViewAdapter;
import com.codepath.caltraindating.models.ChatModel;
import com.codepath.caltraindating.models.RiderModel;

public class ChatFragment extends Fragment implements OnClickListener {

    private Button btnSend;
	private Button btnBack;
	private ImageButton btnRiderProfile;
	private EditText etMessage;
	private ListView lvChats;
	private ChatViewAdapter adapterChatView;
	private List<ChatModel> chatList;
		
	private RiderModel riderChatTo;
		
	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

	// listener is the activity itself
    private OnProfileClickListener profileClickListener;

    // Define the events that the fragment will use to communicate
    public interface OnProfileClickListener {
        public void onProfileButtonClick(RiderModel rider);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("DEBUG", "attach " + activity.toString());
        if (activity instanceof OnProfileClickListener) {
        	profileClickListener = (OnProfileClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
              + " must implement ChatFragment.OnItemSelectedListener");
        }
    }

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	}
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_chat, container, false);
	    initView(view);
	        
	    initData();
		return view;
    }
	    
	public void initView(View v) {
	    lvChats = (ListView)v.findViewById(R.id.lvChats);
	    btnSend = (Button)v.findViewById(R.id.btnSendMessage);
	    btnSend.setOnClickListener(this);
	    btnBack = (Button)v.findViewById(R.id.btnBack);
	    btnBack.setOnClickListener(this);
	    btnRiderProfile = (ImageButton)v.findViewById(R.id.btnRiderProfile);
	    btnRiderProfile.setOnClickListener(this);
	    	
	    etMessage = (EditText)v.findViewById(R.id.etMessage);
	    if (chatList==null)
	    	chatList = new ArrayList<ChatModel>();
	}


	private String[]msgArray = new String[]{"Hi", "Hi", "How are you?", "Good", 
	   										"Are you on train 214?", "Yes, I am on car 5",
	   										"I am on car 6, want a date?", "Sure"};
	    
	private String[]dateArray = new String[]{"2014-02-01 18:00", "2014-02-01 18:10", 
	   										"2014-02-01 18:11", "2014-02-01 18:20", 
	   										"2014-02-01 18:30", "2014-02-01 18:35", 
	   										"2014-02-01 18:40", "2014-02-01 18:50"}; 
	private final static int COUNT = 8;
	    
	public void initData() {
	    if (chatList==null || chatList.size()==0) {
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    	for (int i = 0; i < COUNT; i++) {
	    		ChatModel chat = new ChatModel(); 
	   		    try {
					chat.setChatTime(getMessageTime(df.parse(dateArray[i])));
				} 
	   		    catch (ParseException e) {
					chat.setChatTime(getMessageTime(new Date()));
				}
	    		if (i % 2 == 0) {
	    			chat.setChatName("Tristan");
	    			chat.setComingMessage(false);
	    		} else {
	    			chat.setChatName("Isolde");
	    			chat.setComingMessage(true);
	    		}
	    		
	    		chat.setChatMessage(msgArray[i]);
	    		chatList.add(chat);
	    	}
	    }
	    adapterChatView = new ChatViewAdapter(getActivity(), chatList);
		lvChats.setAdapter(adapterChatView);
			
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnSendMessage:
				sendMessage();
				break;
			case R.id.btnBack:
				getActivity().finish();
				break;
			case R.id.btnRiderProfile:
				Log.d("DEBUG", "click profile on fragment");
			    profileClickListener.onProfileButtonClick(riderChatTo);
				break;
		}
	}
		
	private void sendMessage() {
		String contString = etMessage.getText().toString();
		if (contString.length() > 0) {
			ChatModel chat = new ChatModel();
			chat.setChatTime(getMessageTime(null));
			chat.setChatName("Tristan");
			chat.setComingMessage(false);
			chat.setChatMessage(contString);
				
			chatList.add(chat);
			adapterChatView.notifyDataSetChanged();
				
			etMessage.setText("");
				
			lvChats.setSelection(lvChats.getCount() - 1);
		}
	}
		
	private String getMessageTime(Date chatTime) {
	    if (chatTime == null) {
	        Calendar c = Calendar.getInstance();

	        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
	        String minute = String.valueOf(c.get(Calendar.MINUTE));
	        
	        StringBuffer sbBuffer = new StringBuffer();
	        sbBuffer.append(hour + ":" + minute); 
	        						
	        return sbBuffer.toString();
	    }
	    else {
	    	return sdf.format(chatTime);
	    }
	}
}
