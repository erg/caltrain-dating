package com.codepath.caltraindating.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.models.ChatModel;

public class ChatViewAdapter extends BaseAdapter {
	
	public final static int IS_COMING_MESSAGE = 0;
	public final static int IS_SENDING_MESSAGE = 1;
	public final static int MESSAGE_TYPE_COUNT = 2;
    private static final String TAG = ChatViewAdapter.class.getSimpleName();

	private List<ChatModel> chatList;
    private Context context;
    private LayoutInflater layoutInflater;

    public ChatViewAdapter(Context context, List<ChatModel> chatList) {
    	super();
        this.context = context;
        this.chatList = chatList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
	public int getItemViewType(int position) {
	 	ChatModel chat = chatList.get(position);
	 	
	 	if (chat.isComingMessage()) { 
	 		return IS_COMING_MESSAGE;
	 	}
	 	else{
	 		return IS_SENDING_MESSAGE;
	 	}
	 	
	}

	@Override
	public int getViewTypeCount() {
		return MESSAGE_TYPE_COUNT;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatModel chat = chatList.get(position);
    	//	Log.d("DEBUG", "time="+chat.getChatTime()+", name="+chat.getChatName()+", messge="+chat.getChatMessage());
    	boolean bComingMessage = chat.isComingMessage();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null) {
	    	  if (bComingMessage) {
				  convertView = layoutInflater.inflate(R.layout.chat_coming_message, null);
			  }else{
				  convertView = layoutInflater.inflate(R.layout.chat_going_message, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvChatTime = (TextView) convertView.findViewById(R.id.tvChatTime);
			  viewHolder.tvChatName = (TextView) convertView.findViewById(R.id.tvChatName);
			  viewHolder.tvChatMessage = (TextView) convertView.findViewById(R.id.tvChatMessage);
			  viewHolder.bComingMessage = bComingMessage;
			  
			  convertView.setTag(viewHolder);  // Reuse the view
	    }
	    else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	    
	    viewHolder.tvChatTime.setText(chat.getChatTime());
	    viewHolder.tvChatName.setText(chat.getChatName());
	    viewHolder.tvChatMessage.setText(chat.getChatMessage());
	    
	    return convertView;
    }
    

    static class ViewHolder { 
        public TextView tvChatTime;
        public TextView tvChatName;
        public TextView tvChatMessage;
        public boolean bComingMessage = true;
    }


	@Override
    public int getCount() {
        return chatList.size();
    }

	@Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    

	
	
}
