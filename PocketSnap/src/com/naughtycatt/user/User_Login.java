package com.naughtycatt.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.WeiboUtilListener;

import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class User_Login extends Activity {
	
	private EditText username_et;
	private EditText password_et;
	private Button login_bt;
	private TextView register_tv;
	private LinearLayout wechat_ll;
	private LinearLayout weibo_ll;
	private LinearLayout qq_ll;
	private Dialog progressDialog;
	private Platform weibo;
	private List<Map.Entry> entry;
	private String str="";
	private Handler handler=new Handler(){
		@Override  
		public void handleMessage(Message msg) {  
	        switch (msg.arg1) {  
	        case 1:  // 成功  
	        	sendToast("succ");
	        	dialog_wait(msg.obj.toString());
	            break;  
	        case 2:  // 失败  
	        	dialog_wait("failure");
	        	break;
	          
	        case 3:  // 取消  
	        	dialog_wait("cancel");
	        	break;  
	        }  
	    }  
	};  
	
	
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
				 weibo= ShareSDK.getPlatform(SinaWeibo.NAME);
				//回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
				weibo.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					Message msg = new Message();  
			        msg.arg1 = 2;   
			        msg.obj=arg0;
			        handler.sendMessage(msg);  
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> res) {
					 Iterator ite =res.entrySet().iterator();
					    while (ite.hasNext()) {
					        Map.Entry entry = (Map.Entry)ite.next();
					        Object key = entry.getKey();
					        Object value = entry.getValue();
					        str=str+(String)key+":"+(String)value+"\n";
					    }
					Message msg = new Message();  
			        msg.arg1 = 1;  
			        msg.obj=arg0;
			        handler.sendMessage(msg);  
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					Message msg = new Message();  
					msg.obj=arg0;
			        msg.arg1 = 3;  
			        handler.sendMessage(msg);  

				}

				});
				//authorize与showUser单独调用一个即可
				weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
				weibo.showUser(weibo.getDb().getUserId());
				//移除授权
				//weibo.removeAccount(true);
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
	
	public void dialog_wait(String msg){
		progressDialog = new Dialog(User_Login.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tv = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        tv.setText(msg);
        progressDialog.show();
	}

}
