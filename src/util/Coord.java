package util;

import java.awt.Point;

/**
 * Represents a combination of Point for position and Direction for facing
 * 
 * @author Martin Chau
 *
 */
public class Coord {
	public Coord(Direction facing, Point point) {
		this.facing = facing;
		this.point = point;
	}
	private Direction facing;
	private Point point;
	public Direction getFacing() {
		return facing;
	}
	public void setFacing(Direction facing) {
		this.facing = facing;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	
}
