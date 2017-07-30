package com.naughtycatt.setting;

import com.naughtycatt.javabean._User;
import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;
import com.naughtycatt.user.User_Info;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Setting_Suggestion extends Activity{
	private EditText suggestion_et;
	private Button submit_bt;
	private Button goback_bt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);
		
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");
		
		suggestion_et=(EditText) findViewById(R.id.suggestion_et);
		submit_bt=(Button) findViewById(R.id.submit_bt);
		goback_bt=(Button) findViewById(R.id.goback_bt2);
		
		submit_bt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				_User user = BmobUser.getCurrentUser(_User.class);
				
				Intent data=new Intent(Intent.ACTION_SENDTO);    
				data.setData(Uri.parse("mailto:315136649@qq.com"));    
				data.putExtra(Intent.EXTRA_SUBJECT, "[意见反馈]from "+user.getUsername());    
				data.putExtra(Intent.EXTRA_TEXT, suggestion_et.getText().toString());    
				startActivityForResult(data,1); 
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			sendToast("信件已发送，衷心的感谢您的建议！");
			finish();
		}
	}
	
	private void sendToast(String msg){
		Toast toast = Toast.makeText(Setting_Suggestion.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
