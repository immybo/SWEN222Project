package model;

import util.Coord;

/**
 * A Slime is just a simple Enemy.
 * They don't attack back.
 * 
 * @author Robert Campbell
 */
public class Slime extends Enemy{
	public Slime(Zone zone, Coord coord) {
		super(zone, coord, 10);
	}

	@Override
	public String getDrawImagePath() {
		return "images/slime.png";
	}
}
