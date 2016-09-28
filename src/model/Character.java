package model;

import java.awt.Point;

import util.Coord;

/**
 * 
 * 
 * @author Robert Campbell
 */
public abstract class Character {
	private Coord coord;
	private Zone zone;
	
	/**
	 * Creates a new character with the given parameters.
	 * 
	 * @param zone The initial zone of the character.
 	 * @param position The initial position of the character.
	 */
	public Character(Zone zone, Coord coord){
		this.zone = zone;
		this.coord = coord;
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
}
