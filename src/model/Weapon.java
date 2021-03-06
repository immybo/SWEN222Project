package model;

import java.awt.Point;

import org.w3c.dom.Element;

/**
 * A Weapon is an item that can be used by the
 * player for attacking enemies.
 * 
 * @author Robert Campbell
 */
public abstract class Weapon extends Item{
	private static final long serialVersionUID = 2838526635614165306L;

	public Weapon(boolean stackable) {
		super(stackable);
	}
	
	/**
	 * Constructor for a weapon
	 * 
	 * @param worldPosition Position in world
	 * @param stackable Whether the weapon is stackable
	 */
	public Weapon(Point worldPosition, boolean stackable){
		super(worldPosition, stackable);
	}
	
	protected Weapon(Element elem){
		super(elem);
	}
	
	/**
	 * Returns the damage value of this weapon
	 */
	public abstract int getDamage();
}
