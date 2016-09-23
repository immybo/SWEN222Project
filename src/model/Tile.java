package model;

import java.awt.*;

/**
 * A tile is a background object.
 * Tiles generally have some texture and may
 * or may not collide with characters/items.
 * 
 * @author Robert Campbell
 */
public abstract class Tile {
	private Zone zone;
	private Point position;
	
	public Tile(Zone zone, Point position){
		this.zone = zone;
		this.position = position;
	}
	
	public Zone getZone(){
		return zone;
	}
	
	public Point getPosition(){
		return position;
	}
	
	/**
	 * Tiles can either collide with collidable objects,
	 * or not collide with them.
	 * 
	 * @return Whether or not this tile collides with objects.
	 */
	public abstract boolean collides();
}
