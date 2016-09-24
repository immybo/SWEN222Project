package model;

import java.awt.Point;

/**
 * 
 * 
 * @author Robert Campbell
 */
public abstract class Character {
	private Point position;
	private Zone zone;
	
	/**
	 * Creates a new character with the given parameters.
	 * 
	 * @param zone The initial zone of the character.
 	 * @param position The initial position of the character.
	 */
	public Character(Zone zone, Point position){
		this.zone = zone;
		this.position = position;
	}
}
