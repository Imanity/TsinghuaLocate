/*
 * Author : 王昭
 * Create Time : 2015.1.23
 */

package com.example.tsinghualocate;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class Location extends Application {
	private LocationClient mLocationClient = null;						//定位客户端类
	public String mData_1;												//经度、纬度、误差半径
	public String mData_2;												//方向、仰角、侧角，均以角度为单位，具体说明见http://blog.csdn.net/u010987330/article/details/43083205
	private MyLocationListenner myListener = new MyLocationListenner();	//位置改变监听器
	private String Data;												//返回的结果，返回一个字符串，有六个浮点数值，分别是经度、纬度、误差半径、方向、仰角、侧角，用“|”隔开
	
	private SensorManager sensorManager;								//传感管理器
	private MySensorEventListener mSensorEventListener;					//利用传感器计算出各个参数值的监听器

	
	/*private Sensor aSensor = null;
	private Sensor mSensor = null;
	private float[] acceleromterValues = new float[3];
	private float[] magneticFieldValues = new float[3];
	private float[] values = new float[3];
	private float[] rotate = new float[9];*/
	
	//private SendInformation sendInformation = new SendInformation();
	
	@Override
	public void onCreate() {
		Log.v("Location", "-- onCreate --");
		
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myListener);			//注册监听
        
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new MySensorEventListener();
        
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        
        //aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    	//sensorManager.registerListener(mSensorEventListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
    	//sensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    	
    	super.onCreate();
	}
    private void setLocationOption() {
    	LocationClientOption option = new LocationClientOption();
    	option.setLocationMode(LocationMode.Hight_Accuracy);			//高精度定位模式
    	option.setCoorType("bd09ll");									//返回定位结果是百度经纬度
    	option.setScanSpan(1000);										//设置发起定位请求的间隔时间为1000ms
    	option.setOpenGps(true);										//设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
    	mLocationClient.setLocOption(option);
    }
	
	public void start() {
		Log.v("Location", "-- start --");
		
		mLocationClient.start();
		setLocationOption();
	}
	
	public void stop() {
		mLocationClient.stop();
	}
	
	public String getData() {
		return Data;
	}
    
    private class MySensorEventListener implements SensorEventListener {
    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
    			float x = event.values[SensorManager.DATA_X];
    			float y = event.values[SensorManager.DATA_Y];
    			float z = event.values[SensorManager.DATA_Z];
    			String directInformation = x + "|" + y + "|" + z;
    			mData_2 = directInformation;
    		}
    		/*if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    			acceleromterValues = event.values;
    		}
    		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
    			magneticFieldValues = event.values;
    		}
    		
    		SensorManager.getRotationMatrix(rotate, null, acceleromterValues, magneticFieldValues);
    		SensorManager.getOrientation(rotate, values);
    		values[0] = (float)Math.toDegrees(values[0]);
    		values[1] = (float)Math.toDegrees(values[1]);
    		values[2] = (float)Math.toDegrees(values[2]);
    		String directInformation = "x:" + values[0] + " y:" + values[1] + " z:" + values[2];
			mData_2 = directInformation;*/
    	}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}
    }
	
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if(location == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append(location.getLatitude());
			sb.append("|");
			sb.append(location.getLongitude());
			sb.append("|");
			sb.append(location.getRadius());
			sb.append("|");
			mData_1 = sb.toString();
			Data = mData_1 + mData_2;
			//Log.v("DATA", Data);
		}
	}
}
