package model;

import java.awt.Point;
import java.util.List;

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
	private List<Item> items;
	
	/**
	 * Private constructor for a Zone.
	 * Zones can only be constructed from appropriate
	 * XML objects.
	 * 
	 * @param name The name of the new zone.
	 * @param tiles The tiles in the new zone.
	 */
	private Zone(String name, Tile[][] tiles){
		this.name = name;
		this.tiles = tiles;
	}
	
	/**
	 * Returns the tile at the given point.
	 * 
	 * @param point The point to get the tile at.
	 * @return The tile that is at the given point.
	 * @throws IllegalArgumentException If the point is out of the bounds of this zone.
	 */
	public Tile getTile(Point point){
		if(point.x > tiles[0].length || point.x < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid X: " + point.getX());
		if(point.y > tiles.length || point.y < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid Y: " + point.getY());
		
		return tiles[point.y][point.x];
	}
	
	@Override
	/**
	 * Returns the name of this zone.
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * Adds an item to be contained in (and therefore drawn on)
	 * this zone.
	 * 
	 * @param i The item to be added.
	 */
	public void addItem(Item i){
		items.add(i);
	}
	
	/**
	 * Removes an item to no longer be contained in (and therefore
	 * not drawn on) this zone.
	 * 
	 * @param i The item to be added.
	 */
	public void removeItem(Item i){
		items.remove(i);
	}
}
