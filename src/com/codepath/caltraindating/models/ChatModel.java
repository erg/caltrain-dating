package com.codepath.caltraindating.models;

public class ChatModel {

	private RiderModel riderChatTo;
	private String chatName;
	private String chatTime;
	private String chatMessage;
	
	private boolean bComingMessage = true;
	
	public String getChatName() {
	    return chatName;
	}
	
	public void setChatName(String chatName) {
	    this.chatName = chatName;
	}
	
	public String getChatTime() {
	    return chatTime;
	}
	
	public void setChatTime(String chatTime) {
	    this.chatTime = chatTime;
	}
	
	public String getChatMessage() {
	    return chatMessage;
	}
	
	public void setChatMessage(String chatMessage) {
	    this.chatMessage = chatMessage;
	}
	
	public boolean isComingMessage() {
	    return bComingMessage;
	}
	
	public void setComingMessage(boolean bComingMessage) {
		this.bComingMessage = bComingMessage;
	}
	
	public RiderModel getRiderChatTo() {
		return riderChatTo;
	}

	public void setRiderChatTo(RiderModel riderChatTo) {
		this.riderChatTo = riderChatTo;
	}

	public ChatModel() {
	}
	
	public ChatModel(String chatName, String chatTime, String chatMessage, boolean bComingMessage) {
	    super();
	    this.chatName = chatName;
	    this.chatTime = chatTime;
	    this.chatMessage = chatMessage;
	    this.bComingMessage = bComingMessage;
	}
	
	

}
