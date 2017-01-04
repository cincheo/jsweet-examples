package org.jsweet.examples.blocksgame.util;

import def.js.Math;

public class Point {
	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Point times(double factor) {
		this.x *= factor;
		this.y *= factor;
		return this;
	}

	public Point clone() {
		return new Point(this.x, this.y);
	}

	public String toString() {
		return "POINT(" + this.x + "," + this.y + ")";
	}

	public double distance(Point point) {
		return Math.sqrt((this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y));
	}

	public boolean equals(Point point) {
		return this.x == point.x && this.y == point.y;
	}

	public Vector to(Point endPoint) {
		return new Vector(endPoint.x - this.x, endPoint.y - this.y);
	}

	public Vector toCoords(double endX, double endY) {
		return new Vector(endX - this.x, endY - this.y);
	}

}
