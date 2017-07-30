package com.naughtycatt.javabean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class _User extends BmobUser {
	private String realname;
	private String position;
	private String nickname;
	private String introduction;
	private String gender;
	private BmobFile selfie;
	private Integer fan_num;
	private Integer attention_num;
	
	public String getRealname() {
        return realname;
    }
    public void setRealname(String realname) {
        this.realname = realname;
    }
    
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public BmobFile getSelfie() {
        return selfie;
    }
    public void setSelfie(BmobFile selfie) {
        this.selfie = selfie;
    }
    
    public Integer getFan_num() {
        return fan_num;
    }
    public void setFan_num(Integer fan_num) {
        this.fan_num = fan_num;
    }
    
    public Integer getAttention_num() {
        return attention_num;
    }
    public void setAttention_num(Integer attention_num) {
        this.attention_num = attention_num;
    }
}
