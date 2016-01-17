package com.example.tsinghualocate;

public class Building {
	public static int MAX_SIZE = 20;
	public int pointer = 0;
	public int pointNum = 0;

	public String name;
	public Point[] Coordinates;
	public boolean isInit = false;
	public boolean isOnScreen = false;
	public double xPointOnScreen, yPointOnScreen;
	public double displayDepth;

	public Building() {
	}

	public Building(String name) {
		Coordinates = new Point[MAX_SIZE];
		this.name = name;
	}

	public void InitSet(String name) {
		Coordinates = new Point[MAX_SIZE];
		this.name = name;
	}

	public void AddPoint(double longitude, double latitude) {
		Coordinates[pointer] = new Point(longitude, latitude);
		pointer++;
	}

	public void SetPointNum(int num) {
		pointNum = num;
	}

	public boolean isCoverPoint(Point p) {
		boolean inside = false;
		double angle = 0;
		for(int i = 0, j = pointNum - 1; i < pointNum; j = i++) {
			if(Coordinates[i].xCoordinate == p.xCoordinate && Coordinates[i].yCoordinate == p.yCoordinate) {
				inside = true;
				break;
			} else if(p.IsInLine(Coordinates[i], Coordinates[j])) {
				inside = true;
				break;
			}
			double x1, y1, x2, y2;
			x1 = Coordinates[i].xCoordinate - p.xCoordinate;
			y1 = Coordinates[i].yCoordinate - p.yCoordinate;
			x2 = Coordinates[j].xCoordinate - p.xCoordinate;
			y2 = Coordinates[j].yCoordinate - p.yCoordinate;
			double radian = Math.atan2(y1, x1) - Math.atan2(y2, x2);
			radian = Math.abs(radian);
			if(radian > Math.PI) {
				radian = 2 * Math.PI - radian;
			}
			angle += radian;
		}
		if(Math.abs(Math.PI * 2 - angle) < 1e-7) {
			inside = true;
		}
		return inside;
	}

	public void Display() {
		System.out.println(name);
		//for(int i = 0; i < pointNum; i++)
			//System.out.println(Coordinates[i].xCoordinate + " " + Coordinates[i].yCoordinate);
	}
}
