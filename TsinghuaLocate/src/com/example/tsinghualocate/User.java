package com.example.tsinghualocate;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;

public class User {
	public static int MAX_SIZE = 100;

	private int WndWidth, WndHeight;
	public double minRange, maxRange;

	public Point Coordinate;
	double xAngle, yAngle, zAngle;
	public Building[] buildingInSight = new Building[MAX_SIZE];
	
	private static int sectionPointer = 0;
	public static int buildingPointer = 0;

	public User(int WndWidth, int WndHeight, double minRange, double maxRange) {
		this.WndWidth = WndWidth;
		this.WndHeight = WndHeight;
		this.minRange = minRange;
		this.maxRange = maxRange;
		for(int i = 0; i < MAX_SIZE; i++) {
			buildingInSight[i] = new Building();
		}
	}

	public void setPosition(double longitude, double latitude) {
		Coordinate = new Point(longitude, latitude);
		xAngle = yAngle = zAngle = 0;
		buildingPointer = 0;
	}

	public static double sqr(double x) {
		return x * x;
	}
	
	public static double angle(Point v1, Point v2) {
		return Math.acos((v1.xCoordinate * v2.xCoordinate + v1.yCoordinate * v2.yCoordinate) /
			(Math.sqrt(sqr(v1.xCoordinate) + sqr(v1.yCoordinate)) * (Math.sqrt(sqr(v2.xCoordinate) + sqr(v2.yCoordinate)))));
	}

	private void getDisplayCoordinate(Building b) {
		b.isOnScreen = false;
		if(Coordinate.getMinVector(b) != null) {
			double distance = Math.sqrt(sqr(Coordinate.getMinVector(b).xCoordinate) + sqr(Coordinate.getMinVector(b).yCoordinate));
			Point VisionVector = new Point(Math.cos(xAngle), Math.sin(xAngle));
			Point BuildingVector = Coordinate.getMinVector(b);
			if(angle(BuildingVector, VisionVector) <= Math.PI / 4 && zAngle > Math.PI / 4 && distance > minRange && distance < maxRange) {
				b.isOnScreen = true;
				double tanBuilding = BuildingVector.yCoordinate / BuildingVector.xCoordinate;
				double tanVision = VisionVector.yCoordinate / VisionVector.xCoordinate;
				double tanAngle = (tanVision - tanBuilding) / (1 + tanVision * tanBuilding);
				if(tanAngle >= 0)
					b.xPointOnScreen = (0.5 + angle(BuildingVector, VisionVector) * 2 / Math.PI) * WndWidth;
				else
					b.xPointOnScreen = (0.5 - angle(BuildingVector, VisionVector) * 2 / Math.PI) * WndWidth;
				double zAngleTmp = zAngle;
				if(Math.abs(yAngle) > Math.PI / 2)
					zAngleTmp = Math.PI - zAngle;
				b.yPointOnScreen = zAngleTmp / Math.PI * WndHeight;
				b.displayDepth = (maxRange - distance) / (maxRange - minRange);
			}
		}
	}

	public void refresh(double xAngle, double yAngle, double zAngle) {
		this.xAngle = xAngle;
		this.yAngle = yAngle;
		this.zAngle = zAngle;
		for(int i = 0; i < MAX_SIZE; i++) {
			if(buildingInSight[i].isInit == false)
				break;
			getDisplayCoordinate(buildingInSight[i]);
			if(buildingInSight[i].isOnScreen == true) {
				//buildingInSight[i].Display();
				//System.out.println("ScreenPoint : (" + buildingInSight[i].xPointOnScreen + "," + buildingInSight[i].yPointOnScreen + ")");
				//System.out.println("DisplayDepth : " + buildingInSight[i].displayDepth);
			}
		}
	}

	private String getUserSection(Activity activity) throws Exception {
		Building[] sections = new Building[MAX_SIZE];

		/*File inFile = new File("section-borders.txt");
		FileInputStream fis = new FileInputStream(inFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader bfR = new BufferedReader(isr);*/
		
		InputStreamReader isr = new InputStreamReader(activity.getResources().getAssets().open("section-borders.txt"), "gbk");
		BufferedReader bfR = new BufferedReader(isr);
		
		String temp = "";
		String[] tempArr = {};
		double longitude;
		double latitude;
		sectionPointer = 0;

		while((temp = bfR.readLine()) != null) {
			tempArr = temp.split(",");
			sections[sectionPointer] = new Building(tempArr[0]);
			sections[sectionPointer].pointer = 0;
			for(int i = 0; i < (tempArr.length - 1) / 2; i++){
				longitude = Double.parseDouble(tempArr[i * 2 + 1]);
				latitude = Double.parseDouble(tempArr[i * 2 + 2]);
				sections[sectionPointer].AddPoint(longitude, latitude);
			}
			sections[sectionPointer].SetPointNum((tempArr.length - 1) / 2);
			sectionPointer++;
		}

		bfR.close();

		int i;
		for (i = 0; i < sectionPointer; i++) {
			if(sections[i].isCoverPoint(Coordinate) == true) {
				return sections[i].name;
			}
		}
		return "00";
	}
	private void getBuildingInfoFromFile(String SectionNum, Activity activity) throws Exception {
		String FilePath = "section-" + SectionNum + ".txt";
		/*File inFile = new File(FilePath);
		FileInputStream fis = new FileInputStream(inFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader bfR = new BufferedReader(isr);*/

		InputStreamReader isr = new InputStreamReader(activity.getResources().getAssets().open(FilePath), "gbk");
		BufferedReader bfR = new BufferedReader(isr);
		
		String temp = "";
		String[] tempArr = {};
		double longitude;
		double latitude;

		while((temp = bfR.readLine()) != null) {
			tempArr = temp.split(",");
			buildingInSight[buildingPointer].InitSet(tempArr[0]);
			buildingInSight[buildingPointer].pointer = 0;
			for(int i = 0; i < (tempArr.length - 1) / 2; i++){
				longitude = Double.parseDouble(tempArr[i * 2 + 1]);
				latitude = Double.parseDouble(tempArr[i * 2 + 2]);
				buildingInSight[buildingPointer].AddPoint(longitude, latitude);
			}
			buildingInSight[buildingPointer].SetPointNum((tempArr.length - 1) / 2);
			buildingInSight[buildingPointer].isInit = true;
			//buildingInSight[buildingPointer].Display();
			buildingPointer++;
		}

		bfR.close();
	}

	public void getBuildings(Activity activity) throws Exception{
		char[] userLocation = getUserSection(activity).toCharArray();
		int x = userLocation[0] - '0';
		char y = userLocation[1];
		for(int i = 2; i <= 6; i++) {
			for(char j = 'A'; j <= 'F'; j++) {
				if(Math.abs(i - x) <= 1 && Math.abs((int)j - (int)y) <= 1) {
					char[] arr = new char[2];
					arr[0] = (char)(i + '0');
					arr[1] = j;
					String path = new String(arr);
					getBuildingInfoFromFile(path, activity);
				}
			}
		}
	}
}