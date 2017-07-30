package com.naughtycatt.pocketsnap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.naughtycatt.javabean.Essay;
import com.naughtycatt.javabean._User;
import com.naughtycatt.setting.Setting_Suggestion;
import com.naughtycatt.user.User_Info;
import com.naughtycatt.user.User_Login;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements OnItemClickListener{

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mMenuTitles;
	private GetPhotoFragment f_GP;  
    private PocketSnapFragment f_PS; 
    private CollectionFragment f_col;
    private InformationFragment f_info;
    private SettingFragment f_set;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private File cache;
    private boolean flag_refresh=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		preferences = getSharedPreferences("naughtycatt", MODE_WORLD_READABLE);
		editor = preferences.edit();
		cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }
		
		//����Bmob�ƺ��
		Bmob.initialize(this, "5091e30425ec59d7cd6daa6c9ecde047");
		
		_User bmobUser = BmobUser.getCurrentUser(_User.class);
		if(bmobUser != null){
		    // �����û�ʹ��Ӧ��
			mTitle = mDrawerTitle = getTitle();
			mMenuTitles = getResources().getStringArray(R.array.menu_array);
			initDrawerLayout();
			initDrawerList();

			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
			
			mDrawerList.setItemChecked(0, true);
			setTitle(mMenuTitles[0]);
			setDefaultFragment();

			//ʹ��ActionBarDrawerToggle��Ϊ������
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.drawable.ic_drawer, R.string.drawer_open,
					R.string.drawer_close) {
				@Override
				public void onDrawerClosed(View drawerView) {
					getActionBar().setTitle(mTitle);
					invalidateOptionsMenu();
				}

				@Override
				public void onDrawerOpened(View drawerView) {
					getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu();
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			
		}else{
			Intent intent = new Intent(MainActivity.this,User_Login.class);
			startActivity(intent);
			finish();
		}
	}
	
	/*��ʼ����ർ���˵�����*/
	private void initDrawerList() {
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mMenuTitles));
		mDrawerList.setOnItemClickListener(this);
	}

	/*��ʼ�����˵�����*/
	private void initDrawerLayout() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
			case R.id.action_refresh:
				setDefaultFragment();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	

	/*��ർ�����ݱ�ѡ��*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		/*Toast toast = Toast.makeText(MainActivity.this, mMenuTitles[position], Toast.LENGTH_SHORT);
		toast.show();//����*/
		selectItem(position);
	}

	/*��ת����������Ӧ��fragment*/
	private void selectItem(int position) {
		FragmentManager fm = getSupportFragmentManager();  
        // ����Fragment����  
        FragmentTransaction transaction = fm.beginTransaction();  
        
		switch(position){
		case 0://PocketSnap������
			if (f_PS == null) 
			{  
				f_PS = new PocketSnapFragment();  
	        }  
	        transaction.replace(R.id.content_layout, f_PS); 
			break;
		case 1://getPhoto�ϴ���������Ƭ�Ľ���
			if (f_GP == null) 
			{  
				f_GP = new GetPhotoFragment();  
	        }  
	        transaction.replace(R.id.content_layout, f_GP); 
	        //sendToast(Environment.getExternalStorageDirectory().toString());
			break;
		case 2://collection�����ղؽ���
			if (f_col == null) 
			{  
				f_col = new CollectionFragment();  
	        }  
	        transaction.replace(R.id.content_layout, f_col); 
			break;
		case 3://information������Ϣ����
			if (f_info == null) 
			{  
				f_info = new InformationFragment();  
	        }  
	        transaction.replace(R.id.content_layout, f_info); 
			break;
		case 4://setting���ý���
			if (f_set == null) 
			{  
				f_set = new SettingFragment();  
	        }  
	        transaction.replace(R.id.content_layout, f_set);
			break;
		}

		transaction.commit(); 
		
		mDrawerList.setItemChecked(position, true);
		setTitle(mMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	
	/*����Ĭ�ϵ�fragment����*/
	 private void setDefaultFragment()  
	 {  
	        FragmentManager fm = getSupportFragmentManager();  
	        FragmentTransaction transaction = fm.beginTransaction();  
	        f_PS = new PocketSnapFragment();  
	        transaction.replace(R.id.content_layout, f_PS);  
	        transaction.commit();  
	    }
	 
	 
	 /*����actionbae�ı���*/
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	/*PocketSnap����������*/
	public class PocketSnapFragment extends Fragment implements OnScrollListener{
		private View mFooterView;
		private LinearLayout mloadingLinear;//���ڼ���ʱ��ʾ��view
		private TextView mLoadFinishTextView;//����ȫ�����ݺ���ʾ��view
		private final int LOAD_STATE_IDLE=0;//û���ڼ��أ����ҷ������ϻ�������û����
		private final int LOAD_STATE_LOADING=1;//���ڼ���״̬
		private final int LOAD_STATE_FINISH=2;//��ʾ�������ϵ�ȫ�����ݶ��Ѽ������
		private int loadState=LOAD_STATE_IDLE;//��¼���ص�״̬
		private List<Essay> essay=new ArrayList<Essay>();
		private int skip=0;//ҳ��
		private ListView mListview = null;
		private PaginationAdapter mAdapter;
		
	    @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	        return inflater.inflate(R.layout.fragment_pocketsnap, container, false);  
	    }  
	    
	    @Override  
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState); 
			//Bmob.initialize(MainActivity.this, "5091e30425ec59d7cd6daa6c9ecde047");
			flag_refresh=true;
			mFooterView = getLayoutInflater(savedInstanceState).inflate(R.layout.loadmore, null);
			mloadingLinear=(LinearLayout) mFooterView.findViewById(R.id.loading_linear);
			mLoadFinishTextView=(TextView) mFooterView.findViewById(R.id.load_finish_textview);
			mListview = (ListView) findViewById(R.id.listview_pocketsnap);
			mListview.addFooterView(mFooterView);// �����б�ײ���ͼ			
			mAdapter = new PaginationAdapter(essay);;
			mListview.setAdapter(mAdapter);
			//����setOnScrollListener���Զ�����onscroll������
			mListview.setOnScrollListener(this);
		}
		
	    
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			Log.i("onS", "firstVisibleItem"+firstVisibleItem+" visibleItemCount"+visibleItemCount+" totalItemCount"+totalItemCount);
			if (firstVisibleItem+visibleItemCount==totalItemCount) {
				if(loadState==LOAD_STATE_IDLE)
				{//����δ��������״̬���������ݿ���������ʱ�����������������
					Log.i("onScroll", "firstVisibleItem"+firstVisibleItem+" visibleItemCount"+visibleItemCount+" totalItemCount"+totalItemCount);
					//�����������״̬
			    	loadState=LOAD_STATE_LOADING;
					//��ҳ��ѯ
					queryEssayByDate(skip);
					skip++;
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			Log.i("onScrollStateChanged", scrollState+"");
		}
		
		
		/*��ҳ��ѯEssay��ÿҳX��*/
	    private void queryEssayByDate(Integer skip){
	    	//ע�͵��Ĳ�ѯ�������Ǳ�9015���󣬲�֪��Ϊʲô������������jar��������
	    	/*BmobQuery<Essay> query = new BmobQuery<Essay>();
	    	//����ǰskip*5������
	    	query.setSkip(5*skip);
	    	//����5������
	    	query.setLimit(5);
	    	//���ݴ������ڽ�������
	    	query.order("-createdAt");
	    	
	    	query.findObjects(new FindListener<Essay>() {
				@Override
				public void done(List<Essay> object, BmobException e) {
					if(e==null){
						sendToast("��ѯ�ɹ�����"+object.size()+"�����ݡ�");
			            for (Essay essay : object) {
			            	//����ѯ�������ݼ��뵽adapter��
							//mAdapter.addEssayItem(essay);
			            	TextView title_tv = (TextView) findViewById(R.id.title_tv);
							title_tv.setText(title_tv.getText().toString()+"@"+essay.getTitle());
			            }
					}
					else{
						Log.i("bmob","ʧ�ܣ�"+e);
			            sendToast("ʧ�ܣ�"+e);
			            //handler.sendEmptyMessage(0x2222);
					}
				}
	    	});*/
	    	
	    	/*select top m * from tablename where 
	    	 * id not in (select top n id from tablename order by id desc) */
	    	
	    	Integer from=3*skip;
	    	String bql ="select * from Essay limit "
	    			+ from.toString()
	    			+ ",3"
	    			+ " order by createdAt desc";
	    	//������1
	    	skip++;
	    	new BmobQuery<Essay>().doSQLQuery(bql,new SQLQueryListener<Essay>(){

	    	    @Override
	    	    public void done(BmobQueryResult<Essay> result, BmobException e) {
	    	        if(e ==null){
	    	            List<Essay> list = (List<Essay>) result.getResults();
	    	            if(list!=null && list.size()>0){
	    	            	//String str="";
	    	                for(Essay essay : list) {
	    	                	//str+=essay.getPhoto().getFileUrl();
	    	                	mAdapter.addEssayItem(essay);
	    	                }
	    	                //sendToast(str);
	    	                mAdapter.notifyDataSetChanged();
	    	                //���ݿ����������ݣ���ָ�δ��������״̬
	    	                loadState=LOAD_STATE_IDLE;
	    	            }else{
	    	                Log.i("smile", "��ѯ�ɹ��������ݷ���");
	    	                //���ݿ��������ݣ�����Ϊ������״̬
	    	                loadState=LOAD_STATE_FINISH;
	    	                sendToast(MainActivity.this,"������ȫ���������");
	    	                mloadingLinear.setVisibility(View.GONE);
							mLoadFinishTextView.setVisibility(View.VISIBLE);
	    	            }
	    	        }else{
	    	            Log.i("smile", "�����룺"+e.getErrorCode()+"������������"+e.getMessage());
	    	            sendToast(MainActivity.this,"�����룺"+e.getErrorCode()+"������������"+e.getMessage());
	    	        }
	    	    }
	    	});
	    }
		
		class PaginationAdapter extends BaseAdapter {

			List<Essay> essayList;
 
			public PaginationAdapter(List<Essay> essayList) {
				this.essayList = essayList;
			}

			public int getCount() {
				return essayList==null?0:essayList.size();
			}

			public Object getItem(int position) {
				return essayList.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public void addEssayItem(Essay essayItems) {
				essayList.add(essayItems);
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater(getArguments()).inflate(R.layout.list_item_snap,
							null);
				}
				//ͼ�ı���
				TextView title_tv = (TextView) convertView.findViewById(R.id.title_tv);
				title_tv.setText(essayList.get(position).getTitle());
				//��������
				TextView publish_date_tv = (TextView) convertView.findViewById(R.id.publish_date);
				publish_date_tv.setText(essayList.get(position).getCreatedAt());
				
				//ͼƬ
				String wifi = preferences.getString("WIFI_MODE", null);
				//����Ŀ¼
				
				Bitmap cache_addr=null;
				try{
					cache_addr=getDiskBitmap(Environment.getExternalStorageDirectory()
							+ "/cache/"
							+essayList.get(position).getPhoto().getFilename());
				}catch(Exception e){
					cache_addr=null;
				}
				if(cache_addr!=null){//�����д��ڸ�ͼƬ��ӻ������
					ImageView im = (ImageView) convertView.findViewById(R.id.photo);
					im.setImageBitmap(cache_addr);
					//sendToast(MainActivity.this,"�ӻ������ͼƬ��");
				}
				else{
					if(!isWifi(MainActivity.this)&&wifi.equals("YES")){
						//����WiFi����ģʽ�ҵ�ǰû��WiFi��������ͼƬ
						sendToast(MainActivity.this,"��ǰ���ڷ�WiFi���绷���У�ͼƬ�ѽ�ֹ����");
					}
					else{//���������ͼƬ������ͼƬ���浽���ػ���Ŀ¼��
						new ImageDownloadTask((ImageView) convertView.findViewById(R.id.photo)
								,essayList.get(position).getPhoto().getFilename())
						.execute(essayList.get(position).getPhoto().getFileUrl());
						//sendToast(MainActivity.this,"���������ͼƬ��");
					}
				}
				
				//����
				TextView content_tv = (TextView) convertView.findViewById(R.id.content);
				content_tv.setText(essayList.get(position).getContent());
				
				//�û���
				TextView username_tv = (TextView) convertView.findViewById(R.id.author_name);
				username_tv.setText(essayList.get(position).getUsername());
				
				//ϲ����
				TextView likes_iv = (TextView) convertView.findViewById(R.id.likes_num);
				Integer temp=essayList.get(position).getLike_num();
				likes_iv.setText(temp.toString());
				//������
				TextView comments_iv = (TextView) convertView.findViewById(R.id.comments_num);
				temp=essayList.get(position).getComment_num();
				comments_iv.setText(temp.toString());

				return convertView;
			}
		}
	  
	}  
	
	
	/*�ϴ�ͼƬ���*/
	public class GetPhotoFragment extends Fragment  {  
		private boolean flag=false;
	    private EditText title_et,content_et;
	    private ImageView photo_iv;
	    private Button launch_bt;
	    private Bitmap photo;
		private File mPhotoFile;
	    private int CAMERA_RESULT = 100;
		private int RESULT_LOAD_IMAGE = 200;
		private String saveDir = Environment.getExternalStorageDirectory()+ "/PocketSnap";
		private String photo_name;
		private Essay essay=new Essay();
		
		/*���벼��*/
	    @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	        return inflater.inflate(R.layout.fragment_getphoto, container, false);  
	    }  
	    
	    /*����fragment�е��¼�*/
	    @Override  
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState); 
		    
			title_et=(EditText) findViewById(R.id.essay_title);
			content_et=(EditText) findViewById(R.id.essay_content);
			photo_iv=(ImageView) findViewById(R.id.essay_photo);
			launch_bt=(Button)findViewById(R.id.launch_bt);
			flag_refresh=false;
			
			photo_iv.setOnClickListener(new OnClickListener()
			{//�ϴ�ͼƬ
				@Override
				public void onClick(View v)
				{//ѡ��ͼƬ��Դ����ᣬ���
					final String[] items = getResources().getStringArray(R.array.photo_array); 
					
					//�洢��ַ
					File savePath = new File(saveDir);
					if (!savePath.exists()) {
						savePath.mkdirs();
					}
					
	                new AlertDialog.Builder(MainActivity.this)  
	                        .setTitle("�ϴ���Ƭ")  
	                        .setItems(items, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch(which){
									case 0://���
										OpenAlbum();
										break;
									case 1://���
										OpenCamera();
										break;
									case 2://ȡ��
										break;
									}
								} 
	                        	
	                        }).show();  
				}
			});		
			
			launch_bt.setOnClickListener(new OnClickListener()
			{//����������
				@Override
				public void onClick(View v)
				{
					if(title_et.getText().toString().equals("")||content_et.getText().toString().equals("")||flag==false){
						sendToast(MainActivity.this,"�뽫������д����");
					}
					else{
						//launch_bt.setEnabled(false);
						launch_bt.setVisibility(View.INVISIBLE);
						_User bmobUser = BmobUser.getCurrentUser(_User.class);
						InsertEssay(bmobUser.getUsername(),
								title_et.getText().toString(),
								content_et.getText().toString(),
								mPhotoFile);
						//launch_bt.setEnabled(true);
						launch_bt.setVisibility(View.VISIBLE);
						
					}
				}
			});	
		}
	    

	    
	    /*�������*/
	    private void OpenAlbum(){
	    	Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
	    }
	    
	    /*�������*/
	    private void OpenCamera(){
	    	destoryImage();
			String state = Environment.getExternalStorageState();
			//sendToast(state);
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");       
				String date=sDateFormat.format(new java.util.Date());
				photo_name="PocketSnap"+date+".jpg";
				mPhotoFile = new File(saveDir, photo_name);
				mPhotoFile.delete();
				if (!mPhotoFile.exists()) {
					try {
						mPhotoFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(getApplication(), "��Ƭ����ʧ��!",
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				Intent intent = new Intent(
						"android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(mPhotoFile));
				startActivityForResult(intent, CAMERA_RESULT);
			} else {
				Toast.makeText(getApplication(), "SDcard��Ч��û�в���!",
						Toast.LENGTH_SHORT).show();
			}
		}
	    
	    /*�������������ķ��ؽ��*/
	    @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
				if (mPhotoFile != null && mPhotoFile.exists()) {
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inSampleSize = 8;
					photo = BitmapFactory.decodeFile(mPhotoFile.getPath(),
							bitmapOptions);
					photo_iv.setImageBitmap(photo);
					flag=true;
				}
			}
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
					&& null != data) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				//sendToast(picturePath+"\n"+Environment.getExternalStorageDirectory()+ "/PocketSnap");
				mPhotoFile = new File(picturePath);
				
				photo=BitmapFactory.decodeFile(picturePath);
				photo_iv.setImageBitmap(photo);
				flag=true;
			}
		}
	    
	    /*��Essay�������һƪͼ��*/
	    public void InsertEssay(String username,String title, String content, File mPhotoFile){
	    	
	    	essay.setUsername(username);
	    	essay.setLike_num(0);
	    	essay.setComment_num(0);
	    	essay.setTitle(title);
	    	essay.setContent(content);
	    	//sendToast(mPhotoFile.getPath().toString());
	    	//���ļ��ϴ������ݿ�
	    	upload(mPhotoFile);
	    	
	    }
	    
	    /* ��ͼƬ�ϴ�  */  
	    public void upload(File file){  
	    	final BmobFile bmobFile = new BmobFile(file);
	    	bmobFile.uploadblock(new UploadFileListener() {

				@Override
				public void done(BmobException e) {
					if(e==null){
						essay.setPhoto(new BmobFile(mPhotoFile.getName().toString(),null,bmobFile.getFileUrl()));
				    	//�����ݴ洢������
				    	essay.save(new SaveListener<String>() {
							@Override
							public void done(String arg0, BmobException e) {
								if(e==null){
									sendToast(MainActivity.this,"�����ɹ���");
									title_et.setText("");
									content_et.setText("");
									photo_iv.setImageDrawable (getResources().getDrawable((R.drawable.add_char)));
								}
								else{
									sendToast(MainActivity.this,e.getErrorCode()+e.getMessage());
								}
							}
				    	});
					}
					else{
						sendToast(MainActivity.this,"�ϴ�ͼƬʧ�ܣ�"+e.getErrorCode()+e.getMessage());
					}
				}
	    		
	    	});
	    } 
	    
	    
	    /*�ͷ�ͼƬ��Դ*/
	    @Override
		public void onDestroy() {
			destoryImage();
			super.onDestroy();
		}

		private void destoryImage() {
			if (photo != null) {
				photo.recycle();
				photo = null;
			}
		}
	}  
	
	/*�ղذ��*/
	public class CollectionFragment extends Fragment  {  
		private ImageView tm;
	    @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	        return inflater.inflate(R.layout.fragment_collection, container, false);  
	    }  
	    
	    @Override  
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState); 
			
	    }
	  
	} 
	
	/*������Ϣ���*/
	public class InformationFragment extends Fragment implements OnClickListener{
		
		private ImageView selfie;
		private TextView username;
		private TextView my_introduction;
		private TextView focus_num;
		private TextView fan_num;
	    private LinearLayout goto_user_info;
	    private LinearLayout goto_user_attention;
	    private LinearLayout goto_user_fan;
	    private LinearLayout my_essays;
	    private LinearLayout my_comments;
	    private LinearLayout my_collection;
	    
	    
	    @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	        return inflater.inflate(R.layout.fragment_information, container, false);  
	    }  
	    
	    @Override  
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState); 
		    
			flag_refresh=false;
			initialize();
			goto_user_info.setOnClickListener(this);
			goto_user_attention.setOnClickListener(this);
			goto_user_fan.setOnClickListener(this);
			my_essays.setOnClickListener(this);
			my_comments.setOnClickListener(this);
			my_collection.setOnClickListener(this);	
		}
	    
	    private void initialize(){
			selfie=(ImageView) findViewById(R.id.selfie);
			username=(TextView) findViewById(R.id.username);
			my_introduction=(TextView) findViewById(R.id.my_introduction);
			focus_num=(TextView) findViewById(R.id.focus_num);
			fan_num=(TextView) findViewById(R.id.fan_num);
			goto_user_info=(LinearLayout) findViewById(R.id.goto_user_info);
			goto_user_attention=(LinearLayout) findViewById(R.id.goto_user_attention);
			goto_user_fan=(LinearLayout) findViewById(R.id.goto_user_fan);
			my_essays=(LinearLayout) findViewById(R.id.my_essays);
			my_comments=(LinearLayout) findViewById(R.id.my_comments);
			my_collection=(LinearLayout) findViewById(R.id.my_collection);
			
			_User user = BmobUser.getCurrentUser(_User.class);
			if(user.getSelfie()==null){
				//Ĭ��ͷ��
				//sendToast("Ĭ��ͷ��");
			}
			else{
				//���̼߳���ͷ��		
				String wifi = preferences.getString("WIFI_MODE", null);
				Bitmap cache_addr=null;
				try{
					cache_addr=getDiskBitmap(Environment.getExternalStorageDirectory()
							+ "/cache/"
							+user.getSelfie().getFilename());
				}catch(Exception e){
					
				}
				if(cache_addr!=null){//�����д��ڸ�ͼƬ��ӻ������
					selfie.setImageBitmap(cache_addr);
					//sendToast(MainActivity.this,"�ӻ������ͼƬ��");
				}
				else{
					if(!isWifi(MainActivity.this)&&wifi.equals("YES")){
						sendToast(MainActivity.this,"��ǰ���ڷ�WiFi���绷���У�ͼƬ�ѽ�ֹ����");
					}
					else{
						
						new ImageDownloadTask(selfie,user.getSelfie().getFilename())
						.execute(user.getSelfie().getFileUrl());
						//sendToast(MainActivity.this,"���������ͼƬ��");
					}
				}
			}
			if(user.getIntroduction()==null){
				//Ĭ�����ҽ���
				//sendToast("Ĭ�Ͻ���");
			}
			else{//�������ҽ���
				my_introduction.setText(user.getIntroduction());
			}
			username.setText(user.getUsername());
			focus_num.setText(user.getAttention_num().toString());
			fan_num.setText(user.getFan_num().toString());
	    }

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goto_user_info:
				Intent intent = new Intent(MainActivity.this,User_Info.class);
				startActivity(intent);
				break;
			case R.id.goto_user_attention:
				
				break;
			case R.id.goto_user_fan:
				
				break;
			case R.id.my_essays:
				
				break;
			case R.id.my_comments:
				
				break;
			case R.id.my_collection:
				
				break;
			}
			
		}
	} 
	
	/*���ð��*/
	class SettingFragment extends Fragment implements OnClickListener{  
		
		private LinearLayout WIFI_mode;
		private LinearLayout night_mode;
		private Switch load_mode_switch;
		private Switch night_mode_switch;
		private LinearLayout remark;
		private LinearLayout shareApp;
		private LinearLayout suggestion;
		private LinearLayout clear_mem;
		private TextView used_mem;
		private Button exit;
		
	    @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	        return inflater.inflate(R.layout.fragment_setting, container, false);  
	    }
	    
	    @Override  
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState); 
		    
			flag_refresh=false;
			initialize();
			WIFI_mode.setOnClickListener(this);
			night_mode.setOnClickListener(this);
			remark.setOnClickListener(this);
			shareApp.setOnClickListener(this);
			suggestion.setOnClickListener(this);
			clear_mem.setOnClickListener(this);
			exit.setOnClickListener(this);
			
			load_mode_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (isChecked) {
						editor.putString("WIFI_MODE", "YES");
						editor.commit();
					} 
					else {
						editor.putString("WIFI_MODE", "NO");
						editor.commit();
					}
				}
			});
			
			night_mode_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (isChecked) {
						editor.putString("NIGHT_MODE", "YES");
						editor.commit();
						//��ҹ��ģʽ
						
					} 
					else {
						editor.putString("NIGHT_MODE", "NO");
						editor.commit();
						//�ر�ҹ��ģʽ
				    
					}
				}
			});
		}

	    private void initialize(){
	    	WIFI_mode=(LinearLayout) findViewById(R.id.WIFI_mode);
	    	night_mode=(LinearLayout) findViewById(R.id.night_mode);
	    	load_mode_switch=(Switch) findViewById(R.id.load_mode_switch);
	    	night_mode_switch=(Switch) findViewById(R.id.night_mode_switch);
	    	remark=(LinearLayout) findViewById(R.id.remark);
	    	shareApp=(LinearLayout) findViewById(R.id.shareApp);
	    	suggestion=(LinearLayout) findViewById(R.id.suggestion);
	    	clear_mem=(LinearLayout) findViewById(R.id.clear_mem);
	    	used_mem=(TextView) findViewById(R.id.used_mem);
	    	exit=(Button) findViewById(R.id.exit);
	    	
	    	//���㻺���С
	    	double cache_file_size=0;
	    	File appDir = new File(Environment.getExternalStorageDirectory(), "cache");
		    if (appDir.exists()) {
		    	cache_file_size=getFolderSize(appDir);
		    }
		    DecimalFormat df = new DecimalFormat("#.00");
		    used_mem.setText(df.format(cache_file_size)+"M");
	    	
	    	String wifi = preferences.getString("WIFI_MODE", null);
	    	String night = preferences.getString("NIGHT_MODE", null);
	    	if(wifi==null||wifi.equals("NO")){
	    		//δ����wifiģʽ
	    		load_mode_switch.setChecked(false);
	    		
	    	}
	    	else{
	    		load_mode_switch.setChecked(true);
	    		
	    	}
	    	
	    	if(night==null||night.equals("NO")){
	    		//δ����ҹ��ģʽ
	    		night_mode_switch.setChecked(false);
	    	}
	    	else{
	    		night_mode_switch.setChecked(true);
	    	}
	    }
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.WIFI_mode:
				String wifi = preferences.getString("WIFI_MODE", null);
				if(wifi==null||wifi.equals("NO")){
					load_mode_switch.setChecked(true);
					editor.putString("WIFI_MODE", "YES");
					editor.commit();
				}
				else{
					load_mode_switch.setChecked(false);
					editor.putString("WIFI_MODE", "NO");
					editor.commit();
				}
				break;
			case R.id.night_mode:
				String night = preferences.getString("NIGHT_MODE", null);
				if(night==null||night.equals("NO")){
					night_mode_switch.setChecked(true);
					editor.putString("NIGHT_MODE", "YES");
					editor.commit();
				}
				else{
					night_mode_switch.setChecked(false);
					editor.putString("NIGHT_MODE", "NO");
					editor.commit();
				}
				break;
			case R.id.remark:
				sendToast(MainActivity.this,"��App���ڲ��Խ׶Σ���δ���ߣ���ʱ�������֣���л����֧�֣�");
				break;
			case R.id.shareApp:
				sendToast(MainActivity.this,"API������...");
				break;
			case R.id.suggestion:
				Intent sug = new Intent(MainActivity.this,Setting_Suggestion.class);
				startActivity(sug);
				break;
			case R.id.clear_mem:
				cache = new File(Environment.getExternalStorageDirectory(), "cache");
				if(cache.exists()){
					File[] files = cache.listFiles();
			        for(File file :files){
			            file.delete();
			        }
			        cache.delete();
				}
				used_mem.setText("0");
				sendToast(MainActivity.this,"�������ͷ�");
				break;
			case R.id.exit:
				BmobUser.logOut();
				Intent exit = new Intent(MainActivity.this,MainActivity.class);
				startActivity(exit);
				finish();
				break;
			}		
			
		}  
	  
	} 

	/*�첽���̼߳���ͼƬ��Դ*/
	public class ImageDownloadTask extends AsyncTask<String,Void,Bitmap> {
		private ImageView mImageView;
		private String mFileName;
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;    //�����صĽ��
			String url = params[0];  //��ȡURL
			URLConnection connection;   //�������Ӷ���
			InputStream is;    //����������
			BitmapFactory.Options opt = new BitmapFactory.Options(); 
			opt.inSampleSize = 4;
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
		
		ImageDownloadTask(ImageView mImageView,String mFileName){
			//���ع��캯������ȡ�ؼ�
			this.mImageView=mImageView;
			this.mFileName=mFileName;
		}

		@Override
		protected void onPreExecute() {
			//����ǰUI����
			
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			//���غ�UI����
			mImageView.setImageBitmap(result);
			saveImageToGallery(MainActivity.this, result, mFileName);
		}
	}
	
	/*�ӱ��ض�ȡͼƬ*/
	public Bitmap getDiskBitmap(String pathString)  
	{  
	    Bitmap bitmap = null;  
	    try  
	    {  
	        File file = new File(pathString);  
	        if(file.exists())  
	        {  
	            bitmap = BitmapFactory.decodeFile(pathString);  
	        }  
	        else{
	        	return null;
	        }
	    } catch (Exception e)  {  
	    	
	    }  
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
	
	/*ͳ���ļ��д�С*/
	 public static double getFolderSize(java.io.File file){    
		 
		 double size = 0;    
	     try {  
	    	 java.io.File[] fileList = file.listFiles();     
	         for (int i = 0; i < fileList.length; i++){     
	        	 if (fileList[i].isDirectory()){     
	        		 size = size + getFolderSize(fileList[i]);    
	   
	        	 }
	        	 else{     
	        		 size = size + fileList[i].length();    
	        	 }     
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }     
	       return size/1048576.0;       
	    } 
	
	/*toast*/
	public void sendToast(Context mContext,String msg){
		Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
