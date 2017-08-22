package com.naughtycatt.user;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import com.naughtycatt.javabean._User;
import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class User_Update extends Activity{
	
	private EditText update_et;
	private String title;
	private String content;
	private TextView goback_tv;
	private TextView title_tv;
	private TextView save_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");
		
		update_et=(EditText)findViewById(R.id.suggestion_et);
		Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        title=bundle.getString("title");
        content=bundle.getString("content");
        ActionBar actionBar=getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_save);//设置自定义的布局：actionbar_custom
            goback_tv=(TextView)findViewById(R.id.goback_tv);
            title_tv=(TextView)findViewById(R.id.title);
            save_tv=(TextView)findViewById(R.id.save_tv);
            title_tv.setText(title);
            update_et.setText(content);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            invalidateOptionsMenu();
            goback_tv.setOnClickListener(new OnClickListener()
    		{
    			@Override
    			public void onClick(View v)
    			{
    				finish();
    			}
    		});
            save_tv.setOnClickListener(new OnClickListener()
    		{
    			@Override
    			public void onClick(View v)
    			{
    				String new_s=update_et.getText().toString();
    				if(new_s.equals("")){
    					sendToast("输入内容不能为空！");
    				}
    				else{
    					sendToast("正在更新");
    					updateUserInfo(new_s);
    				}
    			}
    		});
        }
	}
	

	
	private void updateUserInfo(String new_s){
		_User newUser = new _User();
		
		switch(title){
		case "昵称":
			newUser.setNickname(new_s);
			break;
		case "性别":
			newUser.setGender(new_s);
			break;
		case "地区":
			newUser.setPosition(new_s);
			break;
		case "个人简介":
			newUser.setIntroduction(new_s);
			break;
		case "真实姓名":
			newUser.setRealname(new_s);
			break;
		}
		
		_User user = BmobUser.getCurrentUser(_User.class);
		newUser.update(user.getObjectId(),new UpdateListener() {
		    @Override
		    public void done(BmobException e) {
		        if(e==null){
		            sendToast("更新成功！");
		            finish();
		        }else{
		        	sendToast("更新失败:" + e.getMessage());
		        }
		    }
		});
	}
	
	private void sendToast(String msg){
		Toast toast = Toast.makeText(User_Update.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
