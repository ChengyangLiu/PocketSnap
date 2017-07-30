package com.naughtycatt.javabean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Essay extends BmobObject{
	private String username;
	private String title;
	private String content;
	private BmobFile photo;
	private int like_num;
	private int comment_num;
	
	public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    public BmobFile getPhoto() {
        return photo;
    }
    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
    
    public int getLike_num() {
        return like_num;
    }
    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }
    
    public int getComment_num() {
        return comment_num;
    }
    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }
    
}
