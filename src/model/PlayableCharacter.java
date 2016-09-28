package model;

import java.awt.Point;

import util.Coord;

public class PlayableCharacter extends Character {
	
	public final boolean pupo; //!pupo --> yelo
	public PlayableCharacter(Zone zone, Coord coord, boolean isPupo) {
		super (zone, coord);
		this.pupo = isPupo;
	}
}
