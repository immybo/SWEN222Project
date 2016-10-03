package model;

import java.io.Serializable;

import util.Coord;
import util.PointD;

/**
 * Defines static objects in the game world that can be interacted with.
 * 
 * @author Martin Chau
 */
public abstract class Entity extends Interactable implements Serializable, Drawable {
	
	public Entity(){}
	
	public Entity(Zone zone, Coord worldPosition, Inventory inventory, double size) {
		this.zone = zone;
		this.worldPosition = worldPosition;
		this.inventory = inventory;
		this.size = size;
	}

	private Zone zone;
	private Coord worldPosition;
	private Inventory inventory;
	private double size;
	
	
	/**
	 * Teleports this entity to another position
	 * @param coord The position to teleport this entity to. 
	 */
	public void teleportTo(Coord coord){
		this.worldPosition = coord;
	}

	public Coord getWorldPosition() {
		return worldPosition;
	}
	
	
	
	public abstract boolean isPassable();
	
	//TODO Player not done yet
	//abstract public void onCollision(Player p);
	
	private String drawID;
	
	@Override
	public String getDrawID() {
		return this.drawID;
	}

	@Override
	public void setDrawID(String drawID) {
		this.drawID = drawID;
		
	}
}
