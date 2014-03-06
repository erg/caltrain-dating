package com.codepath.caltraindating.fragments;

// import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.caltraindating.ChatHolder;
import com.codepath.caltraindating.R;
import com.codepath.caltraindating.adapters.ChatViewAdapter;
import com.codepath.caltraindating.models.ChatInParse;
import com.codepath.caltraindating.models.ChatModel;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ChatFragment extends Fragment implements OnClickListener {

    private Button btnSend;
	private Button btnBack;
	private ImageButton btnRiderProfile;
	private EditText etMessage;
	private ListView lvChats;
	private TextView tvChatToTitle;
	private ChatViewAdapter adapterChatView;
	
	private ArrayList<ChatModel> chatList;
	private String riderOwnId;
	private String riderChatToId;
	private ParseUser riderOwn;
	private ParseUser riderChatTo;

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("EEE h:mm a");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("MMM d h:mm a");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("MMM d, yyyy h:mm a");

	int currentYear;
	int currentMonth;
	int currentWeek;
	int currentDay;

	// listener is the activity itself
    private OnProfileClickListener profileClickListener;
    
    private OnBackClickListener backClickListener;

    // Define the events that the fragment will use to communicate
    public interface OnProfileClickListener {
        public void onProfileButtonClick(String riderChatToId);
    }

    // Define the events that the fragment will use to communicate
    public interface OnBackClickListener {
        public void onBackButtonClick();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnProfileClickListener) {
        	profileClickListener = (OnProfileClickListener) activity;
        	backClickListener = (OnBackClickListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
              + " must implement ChatFragment.OnItemSelectedListener");
        }
    }

    public void showProgressBar() {
	    getActivity().setProgressBarIndeterminateVisibility(true);
	}
	    
	public void hideProgressBar() {
	  	getActivity().setProgressBarIndeterminateVisibility(false);
	}

    public static ChatFragment newInstance(String ownUserId, String chatToUserId) {
    	ChatFragment fragmentChat = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("OwnUserId", ownUserId);
        args.putString("ChatToUserId", chatToUserId);
        fragmentChat.setArguments(args);
        return fragmentChat;
    }
    
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActivity().getActionBar().hide();
	    // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	}
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_chat, container, false);
	    initView(view);
		return view;
    }
	    
	@SuppressWarnings("unchecked")
	public void initView(View v) {
        showProgressBar();
	    riderOwnId = getArguments().getString("OwnUserId");
	    riderChatToId = getArguments().getString("ChatToUserId");
	    if (riderOwnId!=null)
	    	riderOwn = getParseUserById(riderOwnId);
	    if (riderChatToId!=null)
	    	riderChatTo = getParseUserById(riderChatToId);
        hideProgressBar();
		tvChatToTitle = (TextView)v.findViewById(R.id.tvChatToTitle);
	    lvChats = (ListView)v.findViewById(R.id.lvChats);
	    btnSend = (Button)v.findViewById(R.id.btnSendMessage);
	    btnSend.setOnClickListener(this);
	    btnBack = (Button)v.findViewById(R.id.btnBack);
	    btnBack.setOnClickListener(this);
	    btnRiderProfile = (ImageButton)v.findViewById(R.id.btnRiderProfile);
	    btnRiderProfile.setOnClickListener(this);
	    	
	    etMessage = (EditText)v.findViewById(R.id.etMessage);
 
        chatList = ChatHolder.getInstance().retrieve(riderChatToId);
	    if (chatList==null)
	    	chatList = ChatHolder.getInstance().initialize(riderChatToId);
	    tvChatToTitle.setText(getUserDisplayName(riderChatTo));
        
	    adapterChatView = new ChatViewAdapter(getActivity(), chatList);
		lvChats.setAdapter(adapterChatView);
		ChatHolder.getInstance().clearMessage(riderChatToId);
    	Calendar c=Calendar.getInstance();
    	
    	currentYear=c.get(c.YEAR);
    	currentMonth=c.get(c.MONTH);
    	currentWeek=c.get(c.WEEK_OF_YEAR);
    	currentDay = c.get(c.DAY_OF_WEEK);
     
		/*
		etMessage.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
		        //If the keyevent is a key-down event on the "enter" button
		        if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	sendMessage();
		        }
		        return false;
		    }
		});
		*/
		// Do we need to keep the chat history in Parse?
		if (chatList.size()==0) {
		     fillChatListByParseQuery();
			 showProgressBar();
		}
	}
	

	@SuppressWarnings("unused")
	private void fillChatListByParseQuery() {
    	ParseQuery<ChatInParse> queryFromOwn = ParseQuery.getQuery(ChatInParse.class);
    	queryFromOwn.whereEqualTo("riderChatFrom", riderOwn);
    	queryFromOwn.whereEqualTo("riderChatTo", riderChatTo);
    	ParseQuery<ChatInParse> queryFromChatTo = ParseQuery.getQuery(ChatInParse.class);
    	queryFromChatTo.whereEqualTo("riderChatFrom", riderChatTo);
    	queryFromChatTo.whereEqualTo("riderChatTo", riderOwn);
    	
    	List<ParseQuery<ChatInParse>> queries = new ArrayList<ParseQuery<ChatInParse>>();
    	queries.add(queryFromOwn);
    	queries.add(queryFromChatTo);
    	 
    	ParseQuery<ChatInParse> chatQuery = ParseQuery.or(queries);	  
    	chatQuery.include("riderChatFrom");
    	chatQuery.include("riderChatTo");
    	chatQuery.orderByAscending("chatTime");
    	chatQuery.findInBackground(new FindCallback<ChatInParse>() {
    		@Override
    	    public void done(List<ChatInParse> itemList, ParseException e) {
    	        if (e == null) {
    		    	for (int i = 0; i < itemList.size(); i++) {
    		    		ChatInParse cip = (ChatInParse)itemList.get(i);
    		    		ChatModel chat = new ChatModel(); 
 						chat.setChatTime(getMessageTime(cip.getChatTime()));
 						ParseUser riderChatFrom = cip.getRiderChatFrom();
 						String chatFromUserId = riderChatFrom.getObjectId();
 						chat.setChatName(getUserDisplayName(riderChatFrom));
 						try {
 						    chat.setChatImage(riderChatFrom.getJSONArray("imgSrcs").getString(0));
 						}
 						catch (JSONException je) {
 							chat.setChatImage(null);
 						}
    		    		if (chatFromUserId.equals(riderOwn.getObjectId())) {
    		    			chat.setComingMessage(false);
    		    		} else {
    		    			chat.setComingMessage(true);
    		    		}
    		    		chat.setChatMessage(cip.getChatMessage());
    		    		Log.d("DEBUG", "cip=" + cip.getObjectId() + ", chatto=" + riderChatToId + ", msg from=" + chatFromUserId);
    		    		
    		    			updateChatRead(cip.getObjectId());
    		    		
    		    		chatList.add(chat);
    		    		adapterChatView.notifyDataSetChanged();
    		    	}	    	            // Access the array of results here
    	        } 
    	        else {
    	            Log.d("DEUG", "Error: " + e.getMessage());
    	        }
    			hideProgressBar();

    	    }
    	});	    	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnSendMessage:
				sendMessage();
				break;
			case R.id.btnBack:
				backClickListener.onBackButtonClick();
				break;
			case R.id.btnRiderProfile:
			    profileClickListener.onProfileButtonClick(riderChatToId);
				break;
		}
	}
		
	private void sendMessage() {
		String chatMessage = etMessage.getText().toString();
		if (chatMessage.length() > 0) {
			
		    ChatModel chat = new ChatModel();
	  	    Date chatTime = new Date();
		    chat.setChatTime(getMessageTime(chatTime));
		    chat.setChatName(getUserDisplayName(riderOwn));
		    chat.setComingMessage(false);
		    chat.setChatMessage(chatMessage);
		    chat.setChatImage(getUserImage(riderOwn));
			
		    addToChatList(riderChatToId, chat);
		    
		    adapterChatView.notifyDataSetChanged();
		    focusOnLatestMessge();
		    etMessage.setText("");
			
		    try {
		    	/* This is for testing
		    	String returnMessage = chatMessage.substring(0, chatMessage.length()-1) + "!";
		        JSONObject chatData = new JSONObject("{\"action\": \"com.codepath.caltraindating.CHAT\", \"message\": \"" + returnMessage +  
		    	   	                                 "\", \"name\": \"" + getUserDisplayName(riderChatTo) + "\", \"image\": \"" + getUserImage(riderChatTo) +
		                                             "\"}");
		        
		    	*/
		        JSONObject chatData = new JSONObject("{\"action\": \"com.codepath.caltraindating.CHAT\", \"message\": \"" + chatMessage +  
                                                     "\", \"name\": \"" + getUserDisplayName(riderOwn) + "\", \"image\": \"" + 
		        		                             getUserImage(riderOwn) + "\"}");
		        
	            ParsePush push = new ParsePush();
	            String channel = riderOwnId + "-" + riderChatToId;
	      	    // For testing
	      	    // String channel = "m2lsqXmktG" + "-" + riderOwnId;
	            push.setChannel(channel);
	            push.setData(chatData);
	            push.sendInBackground();
	        }
	        catch (JSONException je) {
		        Log.w("WARN", "send message error: " + je.getMessage());
	        }

		    // Do we want to save the chat in Parse?
            ChatInParse chatInParse = new ChatInParse(riderOwn, riderChatTo, chatTime, chatMessage);
			chatInParse.saveInBackground();

		}
	}

	private void updateChatRead(String chatId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
		 
		// Retrieve the object by id
		query.getInBackground(chatId, new GetCallback<ParseObject>() {
		    public void done(ParseObject chat, ParseException e) {
		        if (e == null) {
		            // just update the isRead field
		            chat.put("isRead", true);
		            chat.saveInBackground();
		        }
		    }
		});	
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

	    	Calendar c2=Calendar.getInstance();
	    	c2.setTimeInMillis(chatTime.getTime());
	    	int chatYear=c2.get(c2.YEAR);
	    	int chatMonth = c2.get(c2.MONTH);
	    	int chatWeek = c2.get(c2.WEEK_OF_YEAR);
	    	int chatDay = c2.get(c2.DAY_OF_WEEK);

	    	if(chatYear == currentYear){
	    		if (chatMonth == currentMonth) {
	    	       if(chatWeek == currentWeek) {
	    	    	   if (chatDay == currentDay)
	    	    		   return sdf1.format(chatTime);
	    	    	   else
	    	    		   return sdf2.format(chatTime);
	    	       }
	    	       else
	    	    	   return sdf3.format(chatTime);
	    	    }
	    		else
	    			return sdf3.format(chatTime);
	    	}
	    	else
	    		return sdf4.format(chatTime);
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
            	Log.w("WARN", "sync done but no result found for id=" + userId);
            }
        }
        catch (ParseException pe) {
	        Log.w("WARN", "Parse error message: " + pe.getMessage());
		};
        return puSet;
	}
	
	private String getUserDisplayName(ParseUser user) {
		if(user == null) {
			Log.e("ERROR", "in ChatFragment, user is null");
			return "";
		}
	    StringBuffer displayName = new StringBuffer();
	    String firstName = user.getString("firstName");
	    if (firstName!=null && firstName.length()>0) {
	    	displayName.append(firstName).append(" ");
	    }
	    String lastName = user.getString("lastName");
	    if (lastName!=null && lastName.length()>0) {
	    	displayName.append(lastName);
	    }
	    return displayName.toString();
	}
	
	private String getUserImage(ParseUser user) {
		try {
	        if (user.getJSONArray("imgSrcs")!=null && riderOwn.getJSONArray("imgSrcs").length()>0)
	            return user.getJSONArray("imgSrcs").getString(0);
		    else
		    	return null;
		}
		catch (JSONException je) {
			return null;
		}
	}

	public ChatViewAdapter getAdapterChatView() {
		return adapterChatView;
	}

	
    public ArrayList<ChatModel> getChatList() {
		return chatList;
	}

    
	public String getRiderChatToId() {
		return riderChatToId;
	}

	public void focusOnLatestMessge() {
	    lvChats.setSelection(lvChats.getCount() - 1);
	}

	public void addToChatList(String chatToId, ChatModel newchat) {
    	this.chatList.add(newchat);
    	// this.chatList is pointing to ChatHolder.chatList
    	// ChatHolder.getInstance().addNewChat(chatToId, newchat, false);
    }
	
}
