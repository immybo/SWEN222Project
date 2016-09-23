package model;

import util.*;

/**
 * Defines something which exists on the world or
 * in an inventory. Items can (usually) be picked
 * up by a player, moving it from the world into
 * an inventory. Items can also be dropped, moving
 * from an inventory into the world.
 * 
 * @author Robert Campbell
 */
public abstract class Item {
	private boolean inInventory;
	private Zone zone;
	private PointD worldPosition;
	private Inventory inventory;
	private double size;
	
	/**
	 * Creates an item that initially resides in the world.
	 * 
	 * @param worldPosition The initial world position of this item.
	 * @param size The size of this item.
	 */
	public Item(Zone zone, PointD worldPosition, double size){
		this.zone = zone;
		this.worldPosition = worldPosition;
		this.inInventory = false;
		this.size = size;
	}
	
	/**
	 * Creates an item that initially resides in an inventory.
	 * 
	 * @param inventory The inventory that this item initially resides in.
	 * @param size The size of this item.
	 */
	public Item(Inventory inventory, double size){
		this.inventory = inventory;
		this.inInventory = true;
		this.size = size;
	}
	
	/**
	 * Returns whether or not this item is contained
	 * within an inventory. If it's not contained within
	 * an inventory, it's either contained within
	 * the world or does not exist in the game.
	 * 
	 * @return Whether or not this item is contained within an inventory.
	 */
	public boolean inInventory(){
		return inInventory;
	}
	
	/**
	 * Performs events that relate to this item being
	 * picked up from the world into an inventory.
	 * This should be called whenever this happens.
	 * 
	 * @param newInv The inventory for this item to be placed into.
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public void onPickUp(Inventory newInv){
		if(inInventory())
			throw new IllegalStateException("Attempting to pick up an item that is already contained in an inventory.");
		inInventory = true;
		inventory = newInv;
		zone.removeItem(this);
	}
	
	/**
	 * Performs events that relate to this item being
	 * dropped from an inventory into the world.
	 * This should be called whenever this happens.
	 * 
	 * @param newZone The zone for this item to be put in.
	 * @param worldPos The world position for this item to be put at.
	 * @throws IllegalStateException If this item isn't in an inventory.
	 */
	public void onDrop(Zone newZone, PointD worldPos) throws IllegalStateException {
		if(!inInventory())
			throw new IllegalStateException("Attempting to drop an item that isn't contained in an inventory.");
		inInventory = false;
		this.worldPosition = worldPos;
		this.zone = newZone;
		zone.addItem(this);
	}
	
	/**
	 * Returns the world position of this item. Throws 
	 * an exception if this item isn't in the world.
	 * 
	 * @return The world position of this item.
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public PointD getPosition() throws IllegalStateException {
		if(inInventory())
			throw new IllegalStateException("Attempting to get the world position of an item which is contained in an inventory.");
		return worldPosition;
	}
	
	/**
	 * Returns the inventory which this item is contained in.
	 * 
	 * @return The inventory which this item is contained in.
	 * @throws IllegalStateException If this item isn't in an inventory.
	 */
	public Inventory getInventory() throws IllegalStateException {
		if(!inInventory())
			throw new IllegalStateException("Attempting to get the inventory of an item which isn't contained in an inventory.");
		return inventory;
	}
	
	/**
	 * Teleports this item to the given world position.
	 * If this item isn't in the world, throws an exception.
	 * 
	 * @param newPos The new world position of this item.
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public void teleportTo(PointD newPos) throws IllegalStateException {
		if(inInventory())
			throw new IllegalStateException("Attempting to teleport an item which is in an inventory.");
		this.worldPosition = newPos;
	}
	
	/**
	 * @return The size of each side of this item.
	 */
	public double getSize(){
		return size;
	}
}
