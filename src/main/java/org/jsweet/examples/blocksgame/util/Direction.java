package org.jsweet.examples.blocksgame.util;

import def.js.Math;

public class Direction {

	public final static double SQRT1_2 = Math.sqrt(1 / 2);

	public static Direction NONE = new Direction(0, 0, new Vector(0, 0));
	public static Direction WEST = new Direction(-1, 0, new Vector(-1, 0));
	public static Direction EAST = new Direction(1, 0, new Vector(1, 0));
	public static Direction NORTH = new Direction(0, -1, new Vector(0, -1));
	public static Direction SOUTH = new Direction(0, 1, new Vector(0, 1));
	public static Direction NORTH_WEST = new Direction(-1, -1, new Vector(-Direction.SQRT1_2, -Direction.SQRT1_2));
	public static Direction NORTH_EAST = new Direction(1, -1, new Vector(Direction.SQRT1_2, -Direction.SQRT1_2));
	public static Direction SOUTH_WEST = new Direction(-1, 1, new Vector(-Direction.SQRT1_2, Direction.SQRT1_2));
	public static Direction SOUTH_EAST = new Direction(1, 1, new Vector(Direction.SQRT1_2, Direction.SQRT1_2));

	public static Direction NORTH_NORTH_WEST = new Direction(-1, -2, new Vector(-Math.cos(Math.PI / 3), -Math.sin(Math.PI / 3)));
	public static Direction NORTH_NORTH_EAST = new Direction(1, -2, new Vector(Math.cos(Math.PI / 3), -Math.sin(Math.PI / 3)));
	public static Direction SOUTH_SOUTH_WEST = new Direction(-1, 2, new Vector(-Math.cos(Math.PI / 3), Math.sin(Math.PI / 3)));
	public static Direction SOUTH_SOUTH_EAST = new Direction(1, 2, new Vector(Math.cos(Math.PI / 3), Math.sin(Math.PI / 3)));
	public static Direction[] straightDirections = { Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH };
	public static Direction[] oblicDirections = { Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_WEST, Direction.SOUTH_EAST };

	public double x;
	public double y;
	public Vector normalized;

	public Direction(double x, double y, Vector normalized) {
		this.x = x;
		this.y = y;
		this.normalized = normalized;
	}

	public String toString() {
		return "direction(" + this.x + "," + this.y + ")(" + this.normalized.x + "," + this.normalized.y + ")";
	}

	public boolean isStraight() {
		return !(this.x != 0 && this.y != 0);
	}

}
