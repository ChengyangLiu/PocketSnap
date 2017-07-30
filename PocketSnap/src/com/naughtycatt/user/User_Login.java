package com.naughtycatt.user;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class User_Login extends Activity{
	
	private EditText username_et;
	private EditText password_et;
	private Button login_bt;
	private TextView register_tv;
	private LinearLayout wechat_ll;
	private LinearLayout weibo_ll;
	private LinearLayout qq_ll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		username_et=(EditText) findViewById(R.id.username_et);
		password_et=(EditText) findViewById(R.id.password_et);
		login_bt=(Button) findViewById(R.id.login_bt);
		register_tv=(TextView) findViewById(R.id.register_tv);
		wechat_ll=(LinearLayout) findViewById(R.id.wechat_ll);
		weibo_ll=(LinearLayout) findViewById(R.id.weibo_ll);
		qq_ll=(LinearLayout) findViewById(R.id.qq_ll);
		
		login_bt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				login(username_et.getText().toString(),password_et.getText().toString());
			}
		});
		
		register_tv.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(User_Login.this,User_Register.class);
				startActivity(intent);
			}
		});
		
		wechat_ll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendToast("功能开发中···");
			}
		});
		
		weibo_ll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendToast("功能开发中···");
			}
		});
		
		qq_ll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendToast("功能开发中···");
			}
		});
	}
	
	private void login(String username, String password){
		BmobUser bu2 = new BmobUser();
		bu2.setUsername(username);
		bu2.setPassword(password);
		bu2.login(new SaveListener<BmobUser>() {

		    @Override
		    public void done(BmobUser bmobUser, BmobException e) {
		        if(e==null){
		        	Intent intent = new Intent(User_Login.this,MainActivity.class);
					startActivity(intent);
					sendToast("登录成功");
					finish();
		        }else{
		        	sendToast(e.getErrorCode()+e.getMessage());
		        }
		    }
		});
	}
	
	/*toast*/
	private void sendToast(String msg){
		Toast toast = Toast.makeText(User_Login.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
