package com.naughtycatt.user;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

import com.naughtycatt.javabean.Essay;
import com.naughtycatt.javabean._User;
import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;

public class User_Register extends Activity{
	private EditText username_et;
	private EditText password_et;
	private EditText email_et;
	private EditText phone_et;
	private Button register_bt;
	private Button goback_bt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");
		
		username_et=(EditText) findViewById(R.id.username_et);
		password_et=(EditText) findViewById(R.id.password_et);
		email_et=(EditText) findViewById(R.id.email_et);
		phone_et=(EditText) findViewById(R.id.phone_et);
		register_bt=(Button) findViewById(R.id.register_bt);
		goback_bt=(Button) findViewById(R.id.goback_bt);
		
		register_bt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//添加用户进数据库，云端数据库有自动检验功能
				String password=password_et.getText().toString();
				String phone=phone_et.getText().toString();
				String email=email_et.getText().toString();
				if(password.length()<6){
					sendToast("密码位数过低，请输入6位以上密码");
				}
				else if(!email.contains("@")){
					sendToast("邮箱格式错误");
				}
				else if(phone.length()!=11){
					sendToast("手机号位数错误");
				}
				
				else{
					InsertUser(username_et.getText().toString(),
							password,
	            			phone,
	            			email
	            			);	
				}
			}
		});
		
		goback_bt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	
	/*添加用户进入数据库*/
	public void InsertUser(String username,String password, String phone, String email){
		_User  user=new _User();
    	user.setUsername(username);
    	user.setPassword(password);
    	user.setMobilePhoneNumberVerified(true);
    	user.setMobilePhoneNumber(phone);
    	user.setEmailVerified(true);
    	user.setEmail(email);
    	user.setFan_num(0);
    	user.setAttention_num(0);
    	
    	user.signUp(new SaveListener<_User>() {
			@Override
			public void done(_User  arg0, BmobException e) {
				if(e==null){
					sendToast("注册成功！");
					finish();
				}
				else{
					sendToast("注册失败:"+e.getErrorCode()+e.getMessage());
				}
			}
    	});
    	
    }
	
	/*toast*/
	private void sendToast(String msg){
		Toast toast = Toast.makeText(User_Register.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
}
