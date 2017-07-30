package com.naughtycatt.javabean;

import cn.bmob.v3.BmobObject;

public class Collection extends BmobObject{
	private String username;
	private String essayID;
	
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
}
