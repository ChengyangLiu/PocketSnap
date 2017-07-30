package com.naughtycatt.javabean;

import cn.bmob.v3.BmobObject;

public class Fan extends BmobObject{
	private String username;
	private String fan;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFan() {
		return fan;
	}
	public void setFan(String fan) {
		this.fan = fan;
	}
}
