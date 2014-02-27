package com.codepath.caltraindating.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class ChatInParse extends ParseObject {

	public ChatInParse() {
        super();
    }

    // Add a constructor that contains core properties
    public ChatInParse(ParseUser riderChatFrom, ParseUser riderChatTo, Date chatTime, String chatMessage) {
        super();
        setRiderChatFrom(riderChatFrom);
        setRiderChatTo(riderChatTo);
        setChatTime(chatTime);
        setChatMessage(chatMessage);
        put("isSent", false);
        put("isRead", false);
      }

    public String getChatMessage() {
        return getString("chatMessage");
    }

    public void setChatMessage(String chatMessage) {
        put("chatMessage", chatMessage);
    }

    public Date getChatTime() {
        return getDate("chatTime");
    }

    public void setChatTime(Date chatTime) {
        put("chatTime", chatTime);
    }
    
    public ParseUser getRiderChatFrom()  {
        return (ParseUser)getParseObject("riderChatFrom");
    }

    public void setRiderChatFrom(ParseUser riderChatFrom) {
        put("riderChatFrom", riderChatFrom);
    }
  
    public ParseUser getRiderChatTo()  {
	    return getParseUser("riderChatTo");
    }

    public void setRiderChatTo(ParseUser riderChatTo) {
	    put("riderChatTo", riderChatTo);
	}  
}
