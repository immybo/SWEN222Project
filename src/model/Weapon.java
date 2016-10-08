package model;

import java.awt.Point;

import org.w3c.dom.Element;

/**
 * A Weapon is an item that can be used by the
 * player for attacking enemies.
 * 
 * @author Robert Campbell
 */
public abstract class Weapon extends Item {

	public Weapon(boolean stackable) {
		super(stackable);
	}
	
	public Weapon(Point worldPosition, boolean stackable){
		super(worldPosition, stackable);
	}
	
	protected Weapon(Element elem){
		super(elem);
	}
	
	public abstract int getDamage();
}
