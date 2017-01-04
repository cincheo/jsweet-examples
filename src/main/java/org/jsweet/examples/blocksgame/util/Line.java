package org.jsweet.examples.blocksgame.util;

import def.js.Error;

public class Line {

	private double slope = 0;
	private double yIntercept;
	private double x;
	private boolean vertical;

	public Line(Point p1, Point p2) {
		this.setPoints(p1, p2);
	}

	public void setPoints(Point p1, Point p2) {
		if (p1.x == p2.x) {
			this.x = p1.x;
			this.vertical = true;
		} else {
			this.slope = (p2.y - p1.y) / (p2.x - p1.x);
			this.yIntercept = p1.y - this.slope * p1.x;
		}
	}

	public boolean isVertical() {
		return this.vertical;
	}

	public boolean isHorizontal() {
		return this.slope == 0;
	}

	public double getSlope() {
		return this.slope;
	}

	public double getYIntercept() {
		return this.yIntercept;
	}

	public double getY(double x) {
		if (this.vertical) {
			throw new Error("invalid query on vertical lines");
		} else {
			return this.slope * x + this.yIntercept;
		}
	}

	public Point getPointForX(double x) {
		return new Point(x, this.getY(x));
	}

	public Point getPointForY(double y) {
		return new Point(this.getX(y), y);
	}

	public double getX(double y) {
		if (this.slope == 0) {
			throw new Error("invalid query on horizonal lines");
		} else if (this.vertical) {
			return this.x;
		} else {
			return (y - this.yIntercept) / this.slope;
		}
	}

	/**
	 * Can be called only when equation of the form x = a (vertical line) or
	 * when the line is a point.
	 */
	public double getXWhenVertical() {
		if (this.vertical) {
			return this.x;
		} else {
			throw new Error("invalid query on non-vertical lines");
		}
	}

}
