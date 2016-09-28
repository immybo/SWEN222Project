package model;

import java.awt.Point;

public class MainCharacter extends Character {

	public final boolean isPupo; //if !pupo --> yelo
	public MainCharacter(Zone zone, Point position, boolean isPupo) {
		super(zone, position);
		this.isPupo = isPupo;
	}
	

	
	
}
