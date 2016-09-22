package model;

import java.awt.Point;

/**
 * A zone is a discrete section of the world.
 * Generally, things in zones will not be able to
 * 'see' or affect things in other zones.
 * 
 * @author Robert Campbell
 */
public class Zone {
	private String name;
	private Tile[][] tiles;
	
	private Zone(String name, Tile[][] tiles){
		this.name = name;
		this.tiles = tiles;
	}
	
	public Tile getTile(Point point){
		if(point.x > tiles[0].length || point.x < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid X: " + point.getX());
		if(point.y > tiles.length || point.y < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid Y: " + point.getY());
		
		return tiles[point.y][point.x];
	}
	
	@Override
	public String toString(){
		return name;
	}
}
