package model;

import java.awt.Point;

/**
 * A floor tile is a simple tile which doesn't collide with
 * anything else.
 * 
 * @author Robert Campbell
 */
public class FloorTile extends Tile {
	public FloorTile(Point position) {
		super(position);
	}
	
	public boolean collides(){
		return false;
	}
}
