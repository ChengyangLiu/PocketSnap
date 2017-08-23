package com.naughtycatt.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobUser.BmobThirdUserAuth;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.WeiboUtilListener;

import com.naughtycatt.javabean._User;
import com.naughtycatt.javabean.third_part;
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
	private Platform weibo= ShareSDK.getPlatform(SinaWeibo.NAME);
	private List<Map.Entry> entry;
	private String str="";
	private Handler handler=new Handler(){
		@Override  
		public void handleMessage(Message msg) {  
	        switch (msg.arg1) {  
	        case 1:  // 成功  
	        	String snsType,accessToken,expiresIn,userId;
	        	snsType="weibo";
	        	accessToken=weibo.getDb().getToken();
	        	Long tmp;
	        	tmp=weibo.getDb().getExpiresIn();
	        	expiresIn=tmp.toString();
	        	userId=weibo.getDb().getUserId();
	        	login_third_part(snsType,accessToken,expiresIn,userId);
	            break;  
	        case 2:  // 失败  
	        	dialog_wait("出现异常，登录失败");
	        	break;
	          
	        case 3:  // 取消  
	        	break;  
	        }  
	    }  
	};  
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");
		
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
				weibo.SSOSetting(true);
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
				weibo.authorize();//单独授权
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
	
	
	private void login_third_part(String snsType,String accessToken,String expiresIn,String userId){
		BmobThirdUserAuth authInfo = new BmobThirdUserAuth(snsType, accessToken, expiresIn, userId);
	    BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

	        @Override
	        public void done(JSONObject userAuth,BmobException e) {
	        	//初始化用户信息
	        	InitializeUserInfo();
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
		
	/*初始化第三方注册用户的信息，防止数据异常*/
	private void InitializeUserInfo(){
		_User newUser = new _User();
		newUser.setFan_num(0);
		newUser.setAttention_num(0);
		newUser.setMobilePhoneNumberVerified(false);
		newUser.setEmailVerified(false);
		_User user = BmobUser.getCurrentUser(_User.class);
		newUser.update(user.getObjectId(),new UpdateListener() {
		    @Override
		    public void done(BmobException e) {
		        if(e==null){
		        	Intent intent = new Intent(User_Login.this,MainActivity.class);
					startActivity(intent);
					sendToast("登录成功");
					finish();
		        }else{
		        	sendToast("注册失败:" + e.getMessage());
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
