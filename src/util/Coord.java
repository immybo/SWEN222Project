package util;

import java.awt.Point;
import java.io.Serializable;

/**
 * Represents a combination of Point for position and Direction for facing
 * 
 * @author Martin Chau
 *
 */
public class Coord implements Serializable {
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
	
	
	@Override
	public String toString(){
		return point.x + " " + point.y + " " + facing.getDirection();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Coord){
			Coord c = (Coord)other;
			return c.facing.equals(facing) && c.point.equals(point);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return facing.hashCode() + 3 * point.hashCode();
	}
	
	public static Coord fromString(String s){
		String[] split = s.split(" ");
		Point p = new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		Direction d = new Direction(Integer.parseInt(split[2]));
		return new Coord(d,p);
	}
	
	public static double getDistance(Point p1, Point p2){
		return Math.sqrt(Math.pow(p2.x-p1.x, 2) + Math.pow(p2.y-p1.y, 2));
	}
}
