package com.echo.dzmc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.ViewFlipper;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    /**
     * ���ݿ�����·��
     */
    private static String db_path;
    private static String db_name;
    
    public DbAdapter db;
    
    public Cursor usersCursor;
    
    public ListView UsersListView;
    
    private ViewFlipper vf;
    private MyWebView wv;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //��������Ŀ¼
        //String sss = getApplicationContext().getFilesDir().getAbsolutePath();
    	
    	//���ݿ�洢Ŀ¼
    	db_path = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.DB_PATH);
    	db_name = getString(R.string.DB_NAME);
        db = new DbAdapter(this,db_path,db_name);
        db.open();    

    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        
        // Set up the drawer.fdskl fdsjalkfsa fsd;fds 
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
 
        //��ͼ�л�
        vf = (ViewFlipper)findViewById(R.id.viewFlipper1);
        wv  = new MyWebView(this,vf);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.setWebChromeClient(new WebChromeClient());
        vf.addView(wv);
        
        Intent intent = getIntent();  
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {  
          String query = intent.getStringExtra(SearchManager.QUERY); 
          
          Log.e("fdsja",query);
          //doMySearch(query);  
        } 
        
    }
    
    protected void OnDestory(){
    	db.close();
    }

    
    @Override
    public void onNavigationDrawerItemSelected(int position,long uid) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position,uid))
                .commit();    
    }

    public void onSectionAttached(int number,long uid) {
		UsersListView = (ListView)findViewById(R.id.listView1);
		
		usersCursor = db.getUsersByUnitID(uid);
		startManagingCursor(usersCursor); 
		
		mTitle = "�� "+ usersCursor.getCount() +" ��";
		
		String[] from = new String[] { "����","ְ��","��Ƭ","ְ��","_id" };
		int[] to  = new int[] {R.id.tvName,R.id.tvZW,R.id.imageView1};
		
		SimpleCursorAdapter users = new SimpleCursorAdapter(this,
				R.layout.users_row_view, usersCursor, from, to, 0);
		
		users.setViewBinder(new ViewBinder (){
		    public boolean setViewValue(View view, Cursor c,int i){
		    	byte[] b = c.getBlob(i);
		        if(view instanceof ImageView && b instanceof byte[]){
		            ImageView iv = (ImageView) view;
		            if (b.length != 0){
		            	try {
		            		Bitmap bmp = byteToBmp(b);
		            		iv.setImageBitmap(bmp);
		            	}catch(Exception e){
		            		Log.e("��ʾ", e.toString());
		            	}
		            }
		            return true;
		        }else{
		            return false;
		        }
		    }
		});
		UsersListView.setAdapter(users);
		UsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		   	    //��ʾ������Ϣ
		    	String s = "file:///android_asset";
		    	wv.loadDataWithBaseURL(s, getUserInfoData(id), "text/html", "UTF-8", "");
		    	vf.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_in));
		        vf.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_out));
		        vf.showNext();
		    }
		});
    }

	@SuppressWarnings("deprecation")
	public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber,long uid) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putLong("unitId", uid);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER),getArguments().getLong("unitId"));
        }
    }
    
    
    /**
     * �ֽ�ת��Ϊbitmap
     * @param data �ֽ�
     * @return ����bitmap
     */
    public static Bitmap byteToBmp(byte[] data) {  
    	try {  
            return BitmapFactory.decodeByteArray(data, 0, data.length);  
        } catch (Exception e) { 
        	Log.e("��ʾ",e.toString());
            return null;  
        }  
    }
    
    /**
     * ȡ�øɲ���ϸ��Ϣ������ģ��
     * @param id �ɲ�ID
     * @return ģ��string
     */
    public String getUserInfoData(long id){
    	String res = "";
    	try {
			InputStream is = getResources().getAssets().open("demo.html");
			
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			res = EncodingUtils.getString(buffer,"UTF-8");
						
			Cursor c = db.getUserInfoByID(id);
			
			startManagingCursor(c);
			c.moveToFirst();
			
			res = res.replace("@name", c.getString(1));
			Bitmap b = byteToBmp(c.getBlob(2));
			if (b!=null){
				b = compressImage(b);
				res = res.replace("@zp", imgToBase64(b));
			}else{
				res = res.replace("@zp", "");
			}
			
			res = res.replace("@sex", c.getString(3));
			res = res.replace("@birthday", c.getString(4));
			res = res.replace("@mz", c.getString(5));
			res = res.replace("@jg", c.getString(6));
			res = res.replace("@csd", c.getString(7));
			res = res.replace("@rdsj", c.getString(8));
			res = res.replace("@cjgzsj", c.getString(9));
			res = res.replace("@jkzk", "");
			res = res.replace("@qrzjy", c.getString(11));
			res = res.replace("@qrzbyyx", c.getString(12));
			res = res.replace("@zzjy", c.getString(13));
			res = res.replace("@zzbyyx", c.getString(14));
			res = res.replace("@zw", c.getString(15));
			res = res.replace("@jl", getBRString(c.getString(16)));
			res = res.replace("@jcqk", getBRString(c.getString(17)));
			res = res.replace("@ndkh", getBRString(c.getString(18)));
			res = res.replace("@dxpx", getBRString(c.getString(19)));
			//res = res.replace("@rmly", "");
		
			
			//������
			c = db.getRelateById(id);
			if (c!=null){
				int i = 1;
				while(c.moveToNext()){
					res = res.replace("@cw"+i, c.getString(1));
					res = res.replace("@xm"+i, c.getString(2));
					res = res.replace("@csny"+i, c.getString(3));
					res = res.replace("@zzmm"+i, c.getString(4));
					res = res.replace("@gzdw"+i, c.getString(5));
					i++;
				}
				
				for(int z = c.getCount();z<6;z++){
					res = res.replace("@cw"+z, "");
					res = res.replace("@xm"+z, "");
					res = res.replace("@csny"+z, "");
					res = res.replace("@zzmm"+z, "");
					res = res.replace("@gzdw"+z, "");
				}
			}
			is.close();

		} catch (IOException e) {
			Log.e("����",e.toString());
		}
		return res;
    }
    
    /**
     * �滻�ַ��еĻ���Ϊ<br>
     * @param s  ԭ�ַ���
     * @return �滻����ַ���
     */
    public static String getBRString(String s){
    	return s.replace("\r\n","<br>");
    }
    
    /**
     * Imageת��ΪBase64��
     * @param bitmap ԭͼƬ
     * @param imgFormat ͼƬ��ʽ
     * @return ͼƬBase64��
     */
    public static String imgToBase64(Bitmap bitmap) {  
        ByteArrayOutputStream out = null;  
        try {  
            out = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
  
            out.flush();  
            out.close();  
  
            byte[] imgBytes = out.toByteArray();  
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
        } catch (Exception e) {  
            return null;  
        } finally {  
            try {  
                out.flush();  
                out.close();  
            } catch (IOException e) {  
                Log.e("��ʾ",e.toString());
            }  
        }  
    }  
    
    /**
     * ѹ��ͼƬ��100k����
     * @param image ԭͼƬ
     * @return ѹ�����ͼƬ
     */
    private static Bitmap compressImage(Bitmap image) {  
        Bitmap bitmap = null;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
        }  
        try{
        	ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
            bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
            
        }catch(Exception e){
        	Log.e("��ʾ",e.toString());
        }
        return bitmap;  
    }
    

}
