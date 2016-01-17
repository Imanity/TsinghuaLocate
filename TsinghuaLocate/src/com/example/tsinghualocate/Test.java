package com.example.tsinghualocate;

import android.app.Activity;


public class Test {
	
	public static Building[] test(String Data, Activity activity) throws Exception {
		//System.out.println(Data);
		
		String[] datas = Data.split("\\|");
		System.out.println(datas[0] + " " + datas[1] + " " + datas[2] + " " + datas[3] + " " + datas[4] + " " + datas[5]);
		
		double xCoordinate = Double.parseDouble(datas[0]);
		double yCoordinate = Double.parseDouble(datas[1]);
		
		MainActivity.currentUser.minRange = Double.parseDouble(datas[2]);
		
		double tmp = 375 - Double.parseDouble(datas[3]);
		if(tmp > 360)
			tmp -= 360;
		if(tmp < 0)
			tmp += 360;
		double xAngle = (tmp) * Math.PI / 180;
		double yAngle = Double.parseDouble(datas[4]) * Math.PI / 180;
		double zAngle = Double.parseDouble(datas[5]) * Math.PI / 180;
		
		MainActivity.currentUser.setPosition(yCoordinate, xCoordinate);
		MainActivity.currentUser.refresh(xAngle, yAngle, zAngle);
		
		
		//MainActivity.currentUser.setPosition(116.332388, 40.01513);
		//MainActivity.currentUser.refresh(Math.PI * 3 / 2, 0, Math.PI / 2);
		
		MainActivity.currentUser.getBuildings(activity);
		return MainActivity.currentUser.buildingInSight;
	}
}
