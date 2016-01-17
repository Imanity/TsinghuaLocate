package com.example.tsinghualocate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class PaintSurfaceView extends SurfaceView implements SurfaceHolder.Callback {  
	  
    protected SurfaceHolder mSurfaceHolder;
    public MyThread mThread;  
    public int mWidth;
    public int mHeight;
    public Location mLocation;
    
	public int exist = 1;
	public float[] displayX;
	public float[] displayY;
	public int[] RectLength;
	
	private Building[] buildings;
    
	private boolean isTouched = false;	//是否已经点击屏幕
	private String information = "";
	private int showInfoTime = -1;	//显示信息动画计时器
	private int showMapTime = -1;
	private boolean showMap = false;
	
	public Activity activity;
	
    public PaintSurfaceView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        mSurfaceHolder = this.getHolder();  
        mSurfaceHolder.addCallback(this);  
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);  
        setZOrderOnTop(true);  
    } 
  
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {  
        mWidth = w;  
        mHeight = h;  
    }  
  
    public void surfaceCreated(SurfaceHolder arg0) {  
          
    }  
  
    public void surfaceDestroyed(SurfaceHolder arg0) {  
  
    }  
    
    public void clearDraw()  
    {  
        Canvas canvas = mSurfaceHolder.lockCanvas();  
        canvas.drawColor(Color.BLUE);  
        mSurfaceHolder.unlockCanvasAndPost(canvas);  
    }  
    
    public void drawLine()   
    {
    	Log.v("drawLine", "drawLine running!");
    	mThread = new MyThread(mSurfaceHolder);
    	mThread.start();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(showMap && showMapTime > 20) {
    		MainActivity.mImageView.setVisibility(View.INVISIBLE);
    		showMap = false;
    		showMapTime = -1;
    		return true;
    	}
    	
    	if(isTouched == true) {
    		if(showInfoTime > 5) {
    			MainActivity.BuildingInfo = "";
    			isTouched = false;
        		showInfoTime = -1;
    		}
    		return true;
    	}
    	isTouched = false;
    	int index = -1, minRectLength = 10000;
    	float x = event.getX(), y = event.getY();
    	
    	if(x >= Constants.WndWidth - 100 && y <= 100 && !showMap) {
    		MainActivity.mImageView.setVisibility(View.VISIBLE);
    		showMapTime = 0;
    		showMap = true;
    		return true;
    	}
    	
    	for(int i = 0; i < User.buildingPointer; ++i) {
    		if(buildings[i].isOnScreen == true
    				&& displayX[i] - RectLength[i] < x && x < displayX[i] + RectLength[i]
    				&& displayY[i] - RectLength[i] < y && y < displayY[i] + RectLength[i]
    				&& RectLength[i] < minRectLength) {
    			minRectLength = RectLength[i];
    			index = i;
    		}
    	}
    	
    	if(index != -1) {
			try {
				InputStreamReader isrI = new InputStreamReader(activity.getResources().getAssets().open("TsingHuaBuildings" + File.separator + buildings[index].Coordinates[0].xCoordinate + ".txt"), "gbk");
				BufferedReader bfRI = new BufferedReader(isrI);
				information = bfRI.readLine();
			} catch(Exception e) {
				e.printStackTrace();
			}
			isTouched = true;
			if(showInfoTime == -1)
				showInfoTime = 0;
			AlertDialog dialog = new AlertDialog.Builder(activity)
				.setMessage(information)
				.create();
			dialog.show();
			dialog.setCanceledOnTouchOutside(true);
			//dialog.getWindow().setWindowAnimations(R.style.mystyle);
    	}
    	return true;
    }
    
    public class MyThread extends Thread {  
        private SurfaceHolder mySurfaceHolder;  
        private boolean isRunning;
      
        public MyThread(SurfaceHolder holder) {  
        	mySurfaceHolder = holder;  
            isRunning = true;  
        }  
      
        @Override  
        public void run() { 
            Canvas canvas = null; 
            Log.v("PaintSurfaceView", "PSV MyThread running!");
            while(isRunning) {  
                try {
                	if(isTouched && showInfoTime <= 6 && showInfoTime != -1)
                		showInfoTime++;
                	if(showMapTime != -1 && showMapTime < 100)
                		showMapTime++;
					//Log.v("DATA", mLocation.getData());
					if(mLocation == null || mLocation.getData() == null) {
						Log.v("getData", "mLocation = null || getData() = null");
						Thread.sleep(100);
						continue;
					}
					//Log.v("data", mLocation.getData());
	        		buildings = Test.test(mLocation.getData(), (Activity)getContext());
	        		if(buildings == null) {
	        			return;
	        		}
                    canvas = mySurfaceHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    
                    RectLength = new int[User.buildingPointer];
                    displayX = new float[User.buildingPointer];
                    displayY = new float[User.buildingPointer];
                    
	        		for(int i = 0; i < User.buildingPointer; ++i) {
	        			if(buildings[i].isOnScreen == true) {
			        		displayX[i] = (float)buildings[i].xPointOnScreen;
			        		displayY[i] = (float)buildings[i].yPointOnScreen;
			        		RectLength[i] = (int)(150 * buildings[i].displayDepth);
			        		
		                    Paint p = new Paint();
		            		p.setTextSize(30);
		            		//canvas.drawCircle(currentX, currentY, 10, p);
		            		p.setStrokeWidth(1F);
		            		p.setStyle(Style.FILL);
		            		p.setColor(Color.BLACK);
		            		canvas.drawText(buildings[i].name, displayX[i] - 50, displayY[i] + RectLength[i] + 40, p);
		            		p.setColor(Color.WHITE);
		            		canvas.drawText(buildings[i].name, displayX[i] - 48, displayY[i] + RectLength[i] + 42, p);
		            		p.setStrokeWidth(5F);
		            		p.setStyle(Style.STROKE);
		            		if(RectLength[i] > 110)
		            			p.setColor(Color.rgb(0, 255, 0));
		            		else if(RectLength[i] > 90)
		            			p.setColor(Color.rgb(180, 255, 0));
		            		else if(RectLength[i] > 60)
		            			p.setColor(Color.rgb(255, 255, 0));
		            		else if(RectLength[i] > 40)
		            			p.setColor(Color.rgb(255, 180, 0));
		            		else
		            			p.setColor(Color.rgb(255, 0, 0));
		            		canvas.drawRect(displayX[i] - RectLength[i], displayY[i] - RectLength[i], displayX[i] + RectLength[i], displayY[i] + RectLength[i], p);
	        			}
	        		}
	        		
	        		Paint q = new Paint();
	        		double face = MainActivity.currentUser.xAngle;
            		String direction;
	        		if(face > Math.PI / 8 && face <= Math.PI * 3 / 8)
            			direction = new String("东北");
            		else if(face > Math.PI * 3 / 8 && face <= Math.PI * 5 / 8)
            			direction = new String("北");
            		else if(face > Math.PI * 5 / 8 && face <= Math.PI * 7 / 8)
            			direction = new String("西北");
            		else if(face > Math.PI * 7 / 8 && face <= Math.PI * 9 / 8)
            			direction = new String("西");
            		else if(face > Math.PI * 9 / 8 && face <= Math.PI * 11 / 8)
            			direction = new String("西南");
            		else if(face > Math.PI * 11 / 8 && face <= Math.PI * 13 / 8)
            			direction = new String("南");
            		else if(face > Math.PI * 13 / 8 && face <= Math.PI * 15 / 8)
            			direction = new String("东南");
            		else if(face > Math.PI * 15 / 8 || face <= Math.PI / 8)
            			direction = new String("东");
            		else
            			direction = new String("未知");
	        		q.setTextSize(30);
            		q.setStrokeWidth(1F);
            		q.setStyle(Style.FILL);
            		q.setColor(Color.BLACK);
            		canvas.drawText("当前朝向：" + direction, 20, 35, q);
            		q.setColor(Color.WHITE);
            		canvas.drawText("当前朝向：" + direction, 22, 37, q);
                    Thread.sleep(Constants.Interval);  
                } catch (Exception e) { 
                    e.printStackTrace();
                } finally {  
                    if (canvas != null) {  
                        // 解除锁定，并提交修改内容  
                    	mySurfaceHolder.unlockCanvasAndPost(canvas);  
                    }  
                }  
            }  
        }
      
        public void stopRequest() {
        	isRunning = false;
        }  
    }
}  