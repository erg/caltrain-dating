package com.codepath.caltraindating;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.codepath.caltraindating.models.ChatModel;

public class ChatHolder {
	  Map<String, WeakReference<ArrayList<ChatModel>>> chatHistory = new HashMap<String, WeakReference<ArrayList<ChatModel>>>();

	  public void saveOrUpdate(String id, ArrayList<ChatModel> updatedChat) {
		  if (chatHistory.get(id)!=null)
			  chatHistory.remove(id);
	      chatHistory.put(id, new WeakReference<ArrayList<ChatModel>>(updatedChat));
	  }

	  public ArrayList<ChatModel> retrieve(String id) {
		  if (chatHistory.containsKey(id)) {
	          WeakReference<ArrayList<ChatModel>> weakReference = chatHistory.get(id);
	          return weakReference.get();
		  }
		  else
			  return null;
	  }

	  private static final ChatHolder holder = new ChatHolder();
	  public static ChatHolder getInstance() {return holder;}
}
