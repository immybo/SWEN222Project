package util;

import java.awt.Point;

/**
 * Defines one of the four cardinal directions that can be moved in.
 * 
 * @author Robert Campbell
 */
public class Direction {
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
	
	public static Point move (Point initial, int direction, int amount){
		PointD moved = move(new PointD(initial.x, initial.y), direction, amount);
		return new Point((int)moved.X, (int)moved.Y);
	}
}
