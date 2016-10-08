package model;

import java.io.Serializable;

import org.w3c.dom.Element;

import util.Coord;

/**
 * An Enemy is a special type of Character
 * who can be attacked and killed by a Player
 * with a Weapon.
 * 
 * @author Robert Campbell
 */
public abstract class Enemy extends Character implements Serializable {
	private int health;
	private int maxHealth;
	
	public Enemy(Zone zone, Coord coord, int maxHealth){
		super(zone, coord);
		this.maxHealth = maxHealth;
		this.health = maxHealth;
	}

	protected Enemy(Element elem, Zone[] zones) {
		super(elem, zones);
	}
	
	public int getRemainingHealth(){
		return health;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public boolean isDead(){
		return health <= 0;
	}
	
	public void damage(int amount){
		health -= amount;
		System.out.println("remaininghealth: " + health);
	}
}
