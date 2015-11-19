package org.jsweet.examples.blocksgame.util;


public class MobileElement extends AnimatedElement {

	public Point position;
	public double weight;
	public Vector speedVector = new Vector(0, 0);
	public double width = 0;
	public double height = 0;

	public MobileElement(Point position, double weight, double width, double height) {
		this.position = position;
		this.weight = weight;
		this.width = width;
		this.height = height;
	}

	public void moveTo(double x, double y) {
		this.position.x = x;
		this.position.y = y;
	}

	public void move(double dx, double dy) {
		this.position.x += dx;
		this.position.y += dy;
	}

	public Point getPosition() {
		return this.position;
	}

	public String toString() {
		return "mobile(" + this.position + ";" + this.speedVector + ")";
	}

}
