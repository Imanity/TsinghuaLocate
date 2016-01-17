package com.example.tsinghualocate;

import java.io.IOException;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	public static final int REFRESH = 0x000001;
    private Handler mHandler = null;
    private MyThread mThread = null;
    
    private PaintSurfaceView mPaintSurfaceView = null;
    private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private boolean previewRunning = true;
	public static ImageView mImageView = null;
	
	public static User currentUser;
	public static String BuildingInfo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		Constants.WndWidth = metric.widthPixels;
		Constants.WndHeight = metric.heightPixels;
        currentUser = new User(Constants.WndWidth, Constants.WndHeight, 0, 200);
        
        mSurfaceView = (SurfaceView)findViewById(R.id.SurfaceView_Camera);
        mSurfaceHolder = mSurfaceView.getHolder();  
        mSurfaceHolder.addCallback(new MySurfaceViewCallback());  
        //mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        mSurfaceHolder.setFixedSize(500, 350);  
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        
        ((Location)getApplication()).start();
        
        mPaintSurfaceView = (PaintSurfaceView)findViewById(R.id.SurfaceView_Paint);  
        mPaintSurfaceView.setVisibility(View.VISIBLE);
        mPaintSurfaceView.activity = this;
        
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setVisibility(View.INVISIBLE);
        mImageView.bringToFront();
        
        /*draw.setOnTouchListener(new OnTouchListener() {
        	@Override
        	public boolean onTouch(View arg0, MotionEvent event) {
        		new MyThread().start();
        		return true;
        	}
        });*/
		
		//draw.setMinimumWidth(Constants.WndWidth);
        //draw.setMinimumHeight(Constants.WndHeight);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == REFRESH) {
	        		//mPaintSurfaceView.invalidate();
					mPaintSurfaceView.mLocation = (Location)getApplication();
				}
				super.handleMessage(msg);
			}
		};
		
		mThread = new MyThread();
		mThread.start();
		mPaintSurfaceView.drawLine();
    }
    
    private class MySurfaceViewCallback implements SurfaceHolder.Callback {  
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                int height) {  
        }  
  
        @SuppressWarnings("deprecation")  
        @Override  
        public void surfaceCreated(SurfaceHolder holder) {  
        	mCamera = Camera.open(); // 取得摄像头  
            Parameters param = mCamera.getParameters();
            param.setPreviewFrameRate(5); // 一秒5帧  
            param.setPictureFormat(PixelFormat.JPEG); // 图片形式  
            param.set("jpen-quality", 80);  
            mCamera.setParameters(param);  
            //mCamera.setDisplayOrientation(90); // 纠正摄像头自动旋转，纠正角度，如果引用，则摄像角度偏差90度  
  
            try {  
            	mCamera.setPreviewDisplay(holder);  
            } catch(IOException e) {
            	e.printStackTrace();
            }  
  
            mCamera.startPreview(); // 进行预览  
            previewRunning = true; // 已经开始预览  
        }  
  
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {  
            if (mCamera != null) {  
                if (previewRunning) {  
                	mCamera.stopPreview(); // 停止预览  
                    previewRunning = false;  
                }  
                mCamera.release();  
            }  
        }  
    }  
    
    public class MyThread extends Thread {
    	public Location mLocation;
    	private boolean isRunning = true;
    	
    	public void stopRequest() {
    		isRunning = false;
    	}
    	
    	public void run() {
    		while(true) {
    			mLocation = (Location)getApplication();
    			if(mLocation != null) {
    				break;
    			}
    			try {
    				Thread.sleep(100);
    			} catch(InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    		Log.v("MyThread", "Location OK!");
    		while(isRunning == true && Thread.currentThread().isInterrupted() == false) {
    			Message msg = new Message();
    			msg.what = REFRESH;
    			mHandler.sendMessage(msg);
    			try {
    				Thread.sleep(Constants.Interval);
    			} catch(InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    @Override
    protected void onResume() {
    	if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}
    	super.onResume();
    }
    
    @Override
    public void onDestroy() {
    	Log.v("MainActivity", "-- onDestroy --");
    	((Location)getApplication()).stop();
    	mThread.stopRequest();
    	mPaintSurfaceView.mThread.stopRequest();
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}
