package com.example.tsinghualocate;

public class Point {
	public double xCoordinate, yCoordinate;

	public Point() {
	}

	public Point(double X, double Y) {
		xCoordinate = X;
		yCoordinate = Y;
	}

	public boolean IsInLine(Point p1, Point p2) {
		if(yCoordinate == p1.yCoordinate && p1.yCoordinate == p2.yCoordinate &&
			((p1.xCoordinate < xCoordinate && xCoordinate < p2.xCoordinate) || (p2.xCoordinate < xCoordinate && xCoordinate < p1.xCoordinate))) {
			return true;
		} else if(xCoordinate == p1.xCoordinate && p1.xCoordinate == p2.xCoordinate &&
			((p1.yCoordinate < yCoordinate && yCoordinate < p2.yCoordinate) || (p2.yCoordinate < yCoordinate && yCoordinate < p1.yCoordinate))) {
			return true;
		} else if(((p1.yCoordinate < yCoordinate && yCoordinate < p2.yCoordinate) || (p2.yCoordinate < yCoordinate && yCoordinate < p1.yCoordinate)) &&
			((p1.xCoordinate < xCoordinate && xCoordinate < p2.xCoordinate) || (p2.xCoordinate < xCoordinate && xCoordinate < p1.xCoordinate))) {
			if((yCoordinate - p1.yCoordinate) / (p2.yCoordinate - p1.yCoordinate) - (xCoordinate - p1.xCoordinate) / (p2.xCoordinate - p1.xCoordinate) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public Point getMinVector(Building b) {
		if(b.isCoverPoint(this)) {
			return null;
		}
		double currentDistance = 0, distance;
		Point CurrentResult = new Point();
		Point Result = new Point();
		Point average = new Point();
		double xSum = 0, ySum = 0;
		for(int i = 0; i < b.pointNum; i++) {
			xSum += b.Coordinates[i].xCoordinate;
			ySum += b.Coordinates[i].yCoordinate;
			CurrentResult.xCoordinate = (b.Coordinates[i].xCoordinate - xCoordinate) * 8.517986243e4;
			CurrentResult.yCoordinate = (b.Coordinates[i].yCoordinate - yCoordinate) * 1.11195e5;
			distance = User.sqr(CurrentResult.xCoordinate) + User.sqr(CurrentResult.yCoordinate);
			if(distance < currentDistance || currentDistance == 0) {
				currentDistance = distance;
				Result = CurrentResult;
			}
		}
		average.xCoordinate = xSum / b.pointNum;
		average.yCoordinate = ySum / b.pointNum;
		CurrentResult.xCoordinate = (average.xCoordinate - xCoordinate) * 8.517986243e4;
		CurrentResult.yCoordinate = (average.yCoordinate - yCoordinate) * 1.11195e5;
		distance = User.sqr(CurrentResult.xCoordinate) + User.sqr(CurrentResult.yCoordinate);
		if(distance < currentDistance || currentDistance == 0) {
			currentDistance = distance;
			Result = CurrentResult;
		}
		return Result;
	}
}
