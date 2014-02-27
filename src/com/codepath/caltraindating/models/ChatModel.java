package com.codepath.caltraindating.models;

import java.io.Serializable;

public class ChatModel implements Serializable {

	private static final long serialVersionUID = 5435489425351745526L;
	private String chatName;
	private String chatImage;
	private String chatTime;
	private String chatMessage;

	
	private boolean bComingMessage = true;
	
	public String getChatName() {
	    return chatName;
	}
	
	public void setChatName(String chatName) {
	    this.chatName = chatName;
	}
	
	public String getChatImage() {
		return chatImage;
	}

	public void setChatImage(String chatImage) {
		this.chatImage = chatImage;
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
	
	public ChatModel() {
	}
	
	public ChatModel(String chatName, String chatImage, String chatTime, String chatMessage, boolean bComingMessage) {
	    super();
	    this.chatName = chatName;
	    this.chatImage = chatImage;
	    this.chatTime = chatTime;
	    this.chatMessage = chatMessage;
	    this.bComingMessage = bComingMessage;
	}
	
	public String toString() {
		return "name=" + chatName + ", image=" + chatImage + ", time=" + chatTime + ", message=" + chatMessage + ", isComing=" + bComingMessage;
	}

}
