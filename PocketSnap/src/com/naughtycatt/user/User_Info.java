package com.naughtycatt.user;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.naughtycatt.javabean._User;
import com.naughtycatt.pocketsnap.MainActivity;
import com.naughtycatt.pocketsnap.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class User_Info extends Activity implements OnClickListener{
	private LinearLayout selfie,nickname,gender,position,introduction,realname,email,phone;
	private ImageView selfie_iv;
	private TextView nickname_tv,gender_tv,position_tv,introduction_tv,realname_tv,
	email_tv,phone_tv;
	private File mPhotoFile;
	private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		preferences = getSharedPreferences("naughtycatt", MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");

		initialize();
		selfie.setOnClickListener(this);
		nickname.setOnClickListener(this);
		gender.setOnClickListener(this);
		position.setOnClickListener(this);
		introduction.setOnClickListener(this);
		realname.setOnClickListener(this);
		
	}
	
	private void initialize(){
		selfie=(LinearLayout) findViewById(R.id.selfie);
		nickname=(LinearLayout) findViewById(R.id.nickname);
		gender=(LinearLayout) findViewById(R.id.gender);
		position=(LinearLayout) findViewById(R.id.position);
		introduction=(LinearLayout) findViewById(R.id.introduction);
		realname=(LinearLayout) findViewById(R.id.realname);
		selfie_iv=(ImageView) findViewById(R.id.selfie_iv);
		nickname_tv=(TextView) findViewById(R.id.nickname_tv);
		gender_tv=(TextView) findViewById(R.id.gender_tv);
		position_tv=(TextView) findViewById(R.id.position_tv);
		introduction_tv=(TextView) findViewById(R.id.introduction_tv);
		realname_tv=(TextView) findViewById(R.id.realname_tv);
		email_tv=(TextView) findViewById(R.id.email_tv2);
		phone_tv=(TextView) findViewById(R.id.phone_tv2);
		
		_User user = BmobUser.getCurrentUser(_User.class);
		if(user.getSelfie()==null){
			//Ĭ��ͷ��
		}
		else{
			//����ͷ��
			String wifi = preferences.getString("WIFI_MODE", null);
			
			/*Bitmap cache_addr=getDiskBitmap(Environment.getExternalStorageDirectory()
					+ "/cache/"
					+user.getSelfie().getFilename());
			if(cache_addr!=null){//�����д��ڸ�ͼƬ��ӻ������
				selfie.setImageBitmap(cache_addr);
				sendToast("�ӻ������ͼƬ��");
			}
			else{
				if(!isWifi(MainActivity.this)&&wifi.equals("YES")){
					sendToast("��ǰ���ڷ�WiFi���绷���У�ͼƬ�ѽ�ֹ����");
				}
				else{
					new ImageDownloadTask(selfie,user.getSelfie().getFilename())
					.execute(user.getSelfie().getFileUrl());
					sendToast("���������ͼƬ��");
				}
			}*/
			
			
			if(!isWifi(User_Info.this)&&wifi.equals("YES")){
			}
			else{
				new ImageDownloadTask((ImageView) selfie_iv.findViewById(R.id.selfie_iv))
				.execute(user.getSelfie().getFileUrl());
			}
		}
		nickname_tv.setText(user.getNickname());
		gender_tv.setText(user.getGender());
		position_tv.setText(user.getPosition());
		introduction_tv.setText(user.getIntroduction());
		realname_tv.setText(user.getRealname());
		email_tv.setText(user.getEmail());
		String phone=user.getMobilePhoneNumber();
		phone=phone.substring(0, 3)+"****"+phone.substring(7, 11);
		phone_tv.setText(phone);
	}
	
	@Override
	public void onClick(View v) {
		boolean flag=true;
		Intent intent = new Intent(User_Info.this,User_Update.class);
		switch(v.getId()){
		case R.id.selfie:
			OpenAlbum();
			flag=false;
			break;
		case R.id.nickname:
			intent.putExtra("title", "�ǳ�");
			intent.putExtra("content",nickname_tv.getText().toString());
			break;
		case R.id.gender:
			intent.putExtra("title", "�Ա�");
			intent.putExtra("content",gender_tv.getText().toString());
			break;
		case R.id.position:
			intent.putExtra("title", "����");
			intent.putExtra("content",position_tv.getText().toString());
			break;
		case R.id.introduction:
			intent.putExtra("title", "���˼��");
			intent.putExtra("content",introduction_tv.getText().toString());
			break;
		case R.id.realname:
			intent.putExtra("title", "��ʵ����");
			intent.putExtra("content",realname_tv.getText().toString());
			break;
		default :
			flag=false;
			break;
		}
		if(flag==true){
			startActivityForResult(intent, 1);
		}
	}
	
	/*�첽���̼߳���ͼƬ��Դ*/
	class ImageDownloadTask extends AsyncTask<String,Void,Bitmap> {
		private ImageView mImageView;
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;    //�����صĽ��
			String url = params[0];  //��ȡURL
			URLConnection connection;   //�������Ӷ���
			InputStream is;    //����������
			BitmapFactory.Options opt = new BitmapFactory.Options(); 
			opt.inSampleSize = 1;
			try {
				connection = new URL(url).openConnection();
				is = connection.getInputStream();   //��ȡ������
				BufferedInputStream buf = new BufferedInputStream(is);
				//����������
				bitmap = BitmapFactory.decodeStream(buf,null,opt);
				is.close();
				buf.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//���ظ�������õķ���
			return bitmap;
		}
		
		ImageDownloadTask(ImageView mImageView){
			//���ع��캯������ȡ�ؼ�
			this.mImageView=mImageView;
		}

		@Override
		protected void onPreExecute() {
			//����ǰUI����
			
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			//���غ�UI����
			mImageView.setImageBitmap(result);
		}
	}
	
	/*�������*/
    private void OpenAlbum(){
    	Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 2);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			Intent intent = new Intent(User_Info.this,User_Info.class);
			startActivity(intent);
			finish();
		}
		else if(requestCode==2 && null != data){
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			mPhotoFile = new File(picturePath);
			Bitmap photo=BitmapFactory.decodeFile(picturePath);
			Bitmap compress_bitmap=compressImage(photo);
			//��ѹ�����ͼƬ�浽cache��
			saveImageToGallery(User_Info.this, compress_bitmap, mPhotoFile.getName().toString());
			//��ȡѹ������ļ�
			mPhotoFile = new File(Environment.getExternalStorageDirectory()
					+ "/cache/" + mPhotoFile.getName().toString());
			//sendToast(picturePath);
			upload_selfie(mPhotoFile);
		}
	}
	
	public void upload_selfie(File file){  
    	final BmobFile bmobFile = new BmobFile(file);
    	bmobFile.uploadblock(new UploadFileListener() {

			@Override
			public void done(BmobException e) {
				if(e==null){
					_User newUser = new _User();
					_User user = BmobUser.getCurrentUser(_User.class);
					newUser.setSelfie(new BmobFile(mPhotoFile.getName().toString(),null,bmobFile.getFileUrl()));
					newUser.update(user.getObjectId(),new UpdateListener() {
					    @Override
					    public void done(BmobException e) {
					        if(e==null){
					            sendToast("ͷ���ϴ��ɹ�");
					            Intent intent = new Intent(User_Info.this,User_Info.class);
								startActivity(intent);
								finish();
					        }else{
					        	sendToast("�ϴ�ʧ��:" + e.getErrorCode()+e.getMessage());
					        }
					    }
					});
		
				}
				else{
					sendToast("�ϴ�ͼƬʧ�ܣ�"+e.getErrorCode()+e.getMessage());
				}
			}
    		
    	});
    } 
	
	/*ѹ��ͼƬ*/
	public static Bitmap compressImage(Bitmap image) {  
		  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
	    int options = 90;  
	  
	    while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��  
	        baos.reset(); // ����baos�����baos  
	        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
	        options -= 10;// ÿ�ζ�����10  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ  
	    return bitmap;  
	} 
	
	/*����ͼƬ�������ļ���*/
	public void saveImageToGallery(Context context, Bitmap bmp, String fileName) {
	    // ���ȱ���ͼƬ
	    File appDir = new File(Environment.getExternalStorageDirectory(), "cache");
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    String path=Environment.getExternalStorageDirectory().toString()+"/cache";
	    File file = new File(appDir, fileName);
	    try {
	        FileOutputStream fos = new FileOutputStream(file);
	        bmp.compress(CompressFormat.JPEG, 100, fos);
	        fos.flush();
	        fos.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
		}
	    
	    // ��ΰ��ļ����뵽ϵͳͼ��
	    try {
	        MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    // ���֪ͨͼ�����
	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
	}
	
	/*�жϵ�ǰ�Ƿ�ΪWiFi����*/
	public boolean isWifi(Context mContext) {  
	    ConnectivityManager connectivityManager = (ConnectivityManager) mContext  
	    		.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	    if (activeNetInfo != null  
	            && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
	        return true;  
	    }  
	    return false;  
	}  
	
	private void sendToast(String msg){
		Toast toast = Toast.makeText(User_Info.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
