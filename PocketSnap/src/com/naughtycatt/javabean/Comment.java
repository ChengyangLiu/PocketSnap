package com.naughtycatt.javabean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
	private String username;
	private String essayID;
	private String content;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEssayID() {
		return essayID;
	}
	public void setEssayID(String essayID) {
		this.essayID = essayID;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
