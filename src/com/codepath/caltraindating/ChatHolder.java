package com.codepath.caltraindating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.codepath.caltraindating.models.ChatModel;

public class ChatHolder {
	  Map<String, ArrayList<ChatModel>> chatHistory = new HashMap<String, ArrayList<ChatModel>>();
	  Map<String, Boolean> newMessage = new HashMap<String, Boolean>();

	  public void saveOrUpdate(String id, ArrayList<ChatModel> updatedChat) {
	      chatHistory.put(id, updatedChat);
	  }

	  public ArrayList<ChatModel> retrieve(String id) {
		  if (chatHistory.containsKey(id)) {
	          return chatHistory.get(id);
		  }
		  else
			  return null;
	  }

      public ArrayList<ChatModel> initialize(String id) {
    	  ArrayList<ChatModel> chatList = new ArrayList<ChatModel>();
    	  chatHistory.put(id, chatList);
    	  return chatList;
      }
      
	  public void addNewChat(String id, ChatModel chat, boolean ifSetNewMessage) {
		  if (chatHistory.get(id)==null) {
			  Log.w("WARN", "It's impossible that chat history not intialized for user " + id);
		  }
		  else {
			  ArrayList<ChatModel> chatList = chatHistory.get(id);
			  chatList.add(chat);
		  }
		  if (ifSetNewMessage)
		      newMessage.put(id, true);
	  }
	  
	  public Boolean hasNewMessage(String id) {
		  if (newMessage.containsKey(id))
		      return newMessage.get(id);
		  else
			  return false;
	  }
	  
	  public void clearMessage(String id) {
		  newMessage.put(id, false);
	  }
	  
	  private static final ChatHolder holder = new ChatHolder();
	  public static ChatHolder getInstance() {return holder;}
}
