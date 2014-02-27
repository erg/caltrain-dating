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
import android.view.WindowManager;
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
import com.parse.ParseException;
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

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	// listener is the activity itself
    private OnProfileClickListener profileClickListener;

    // Define the events that the fragment will use to communicate
    public interface OnProfileClickListener {
        public void onProfileButtonClick(ParseUser rider);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnProfileClickListener) {
        	profileClickListener = (OnProfileClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
              + " must implement ChatFragment.OnItemSelectedListener");
        }
    }

    public static ChatFragment newInstance(String ownUserId, String chatToUserId, List<ChatModel> chatList) {
    	ChatFragment fragmentChat = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("OwnUserId", ownUserId);
        args.putString("ChatToUserId", chatToUserId);
        args.putSerializable("ChatList", (ArrayList<ChatModel>)chatList);
        fragmentChat.setArguments(args);
        return fragmentChat;
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
		return view;
    }
	    
	@SuppressWarnings("unchecked")
	public void initView(View v) {
		tvChatToTitle = (TextView)v.findViewById(R.id.tvChatToTitle);
	    lvChats = (ListView)v.findViewById(R.id.lvChats);
	    btnSend = (Button)v.findViewById(R.id.btnSendMessage);
	    btnSend.setOnClickListener(this);
	    btnBack = (Button)v.findViewById(R.id.btnBack);
	    btnBack.setOnClickListener(this);
	    btnRiderProfile = (ImageButton)v.findViewById(R.id.btnRiderProfile);
	    btnRiderProfile.setOnClickListener(this);
	    	
	    etMessage = (EditText)v.findViewById(R.id.etMessage);
 
	    riderOwnId = getArguments().getString("OwnUserId");
	    riderChatToId = getArguments().getString("ChatToUserId");
	    if (getArguments().getSerializable("ChatList")!=null)
	        chatList = (ArrayList<ChatModel>)getArguments().getSerializable("ChatList");
	    if (riderOwnId!=null)
	    	riderOwn = getParseUserById(riderOwnId);
	    if (riderChatToId!=null)
	    	riderChatTo = getParseUserById(riderChatToId);
	    tvChatToTitle.setText(getUserDisplayName(riderChatTo));
	    adapterChatView = new ChatViewAdapter(getActivity(), chatList);
		lvChats.setAdapter(adapterChatView);
       
		// Do we need to keep the chat history in Parse?
		// if (chatList.size()==0)
		//     fillChatListByParseQuery();
	}
	
	/* This part is just for demo
	 * 
	private String[] msgArray = new String[]{"Hi", "Hi", "How are you?", "Good", 
				"Are you on train 214?", "Yes, I am on car 5",
				"I am on car 6, want a date?", "Sure"};

    private String[] dateArray = new String[]{"2014-02-01 18:00", "2014-02-01 18:10", 
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
*/
	
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
    		    		chatList.add(chat);
    		    		adapterChatView.notifyDataSetChanged();
    		    	}	    	            // Access the array of results here
    	        } 
    	        else {
    	            Log.d("DEUG", "Error: " + e.getMessage());
    	        }
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
				ChatHolder.getInstance().saveOrUpdate(riderChatToId, chatList);
				getActivity().finish();
				break;
			case R.id.btnRiderProfile:
			    profileClickListener.onProfileButtonClick(riderChatTo);
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
		    if (riderOwn.getJSONArray("imgSrcs")!=null && riderOwn.getJSONArray("imgSrcs").length()>0)
		        chat.setChatImage(getUserImage(riderOwn));
			
		    chatList.add(chat);
		    adapterChatView.notifyDataSetChanged();
			
		    etMessage.setText("");
			
		    try {
		        JSONObject chatData = new JSONObject("{\"action\": \"com.codepath.caltraindating.CHAT\", \"message\": \"" + chatMessage +  
		    	   	                                 "\", \"name\": \"" + getUserDisplayName(riderOwn) + "\", \"image\": \"" + chat.getChatImage() +
		                                             "\"}");
		        // Log.d("DEBUG", "json message=" + chatData.toString());
	            ParsePush push = new ParsePush();
	            String channel = riderChatToId + "-" + riderOwnId;
	            push.setChannel(channel);
	            push.setData(chatData);
	            push.sendInBackground();
	        }
	        catch (JSONException je) {
		        Log.d("DEBUG", "send message error: " + je.getMessage());
	        }

		    // Do we want to save the chat in Parse?
            ChatInParse chatInParse = new ChatInParse(riderOwn, riderChatTo, chatTime, chatMessage);
			chatInParse.saveInBackground();

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
	
	private String getUserDisplayName(ParseUser user) {
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

	public void focusOnLatestMessge() {
	    lvChats.setSelection(lvChats.getCount() - 1);
	}

	public void addToChatList(ChatModel newchat) {
    	if (this.chatList==null)
    		this.chatList = new ArrayList<ChatModel>();
    	this.chatList.add(newchat);
    	// Log.d("DEBUG", "Let's see what's in the chat list now:" + chatList.toString());
    }
}
