package model;

import java.io.Serializable;

import util.Coord;
import util.PointD;
import view.Drawable;

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
	
	public Zone getZone(){
		return zone;
	}
	
	public abstract boolean isPassable();
	
	//TODO Player not done yet
	//abstract public void onCollision(Player p);
	
	private String drawImagePath;
	
	@Override
	public String getDrawImagePath() {
		return this.drawImagePath;
	}

	public void setDrawImagePath(String drawImagePath) {
		this.drawImagePath = drawImagePath;
		
	}

	@Override
	public double getDepth() {
		return worldPosition.getPoint().getY();
	}

	@Override
	public PointD getDrawPosition() {
		return new PointD(worldPosition.getPoint().getX(),worldPosition.getPoint().getY());
	}
}
