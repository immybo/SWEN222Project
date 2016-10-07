package util;

import java.awt.Point;
import java.io.Serializable;

/**
 * Defines one of the four cardinal directions that can be moved in.
 * 
 * @author Robert Campbell
 */
public class Direction implements Serializable {
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	private int direction;
	public Direction(int direction){
		this.direction = direction;
	}
	public int getDirection(){
		return this.direction;
	}
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	public static PointD move(PointD initial, int direction, double amount){
		if(direction == NORTH)
			return initial.add(0, -amount);
		else if(direction == EAST)
			return initial.add(amount, 0);
		else if(direction == SOUTH)
			return initial.add(0, amount);
		else if(direction == WEST)
			return initial.add(-amount, 0);
		
		throw new IllegalArgumentException(direction + " is not a valid direction!");
	}
	/**
	 * Returns a point after the point has been moved in the given direction
	 * 
	 * @param initial Original point 
	 * @param direction Direction to move
	 * @param amount Amount to move
	 * @return point after movement
	 */
	public static Point move(Point initial, Direction direction, int amount){
		if(direction.direction == NORTH)
			return new Point(initial.x, initial.y - amount);
		else if(direction.direction == EAST)
			return new Point(initial.x + amount, initial.y);
		else if(direction.direction == SOUTH)
			return new Point(initial.x, initial.y + amount);
		else if(direction.direction == WEST)
			return new Point(initial.x - amount, initial.y);
		
		throw new IllegalArgumentException(direction + " is not a valid direction!");
	}
	
	/**
	 * Given a direction, returns the opposite direction
	 * @param direction Original direction
	 * @return opposite direction
	 */
	public static Direction oppositeDirection(Direction direction){
		if(direction.getDirection() == NORTH) return new Direction(SOUTH);
		if(direction.getDirection() == EAST) return new Direction(WEST);
		if(direction.getDirection() == SOUTH) return new Direction(NORTH);
		if(direction.getDirection() == WEST) return new Direction(EAST);
		throw new IllegalArgumentException(direction.getDirection() + " is not a valid direction!");
	}
	
	/**
	 * Given a start point and an end point that are adjacent,
	 * returns the direction that must be moved in from the start
	 * point to arrive at the end point.
	 */
	public static Direction directionFrom(Point start, Point end){
		if(Coord.getDistance(start, end) != 1d)
			throw new IllegalArgumentException("Can't get direction from two non-adjacent points.");
		
		if(end.x == start.x + 1) return new Direction(Direction.EAST);
		else if(end.x == start.x - 1) return new Direction(Direction.WEST);
		else if(end.y == start.y + 1) return new Direction(Direction.SOUTH);
		else if(end.y == start.y - 1) return new Direction(Direction.NORTH);
		
		else throw new IllegalArgumentException("Can't classify that as a direction.");
	}
	
	@Override
	public boolean equals(Object other){
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other instanceof Direction){
			return ((Direction)other).direction == direction;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return 7 * direction;
	}
}
