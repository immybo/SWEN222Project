package model;

import java.awt.Point;

/**
 * A wall tile is a tile that acts as a boundary which will collide with any other object
 * 
 * @author Martin Chau
 */
public class WallTile extends Tile {

	public WallTile(Point position) {
		super(position);
	}
	
	public boolean collides(){
		return true;
	}
}
