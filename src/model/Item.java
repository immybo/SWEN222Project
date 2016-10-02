package model;

import java.awt.Point;
import java.io.Serializable;

import org.w3c.dom.*;

import datastorage.*;
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
public abstract class Item extends Drawable implements Storable, Serializable {
	private boolean inInventory;
	private Point worldPosition;
	
	// Unique identifier for this item
	private long id;
	private static long nextID = 0;
	
	/**
	 * Creates an item that initially resides in the world.
	 * 
	 * @param worldPosition The initial world position of this item.
	 */
	public Item(Point worldPosition){
		this.worldPosition = worldPosition;
		this.inInventory = false;
		
		this.id = nextID++;
	}
	
	/**
	 * Creates an item that initially resides in an inventory.
	 */
	public Item(){
		this.inInventory = true;
		
		this.id = nextID++;
	}
	
	/**
	 * Constructor to create an item from an XML element.
	 */
	protected Item(Element elem){
		this.inInventory = Boolean.parseBoolean(elem.getAttribute("inInventory"));
		this.id = Long.parseLong(elem.getAttribute("ID"));
		// Make sure we don't have overlaps with any items created in the future
		if(id >= nextID)
			nextID = id + 1;
		
		if(!inInventory){
			this.worldPosition = new Point(
					Integer.parseInt(elem.getAttribute("worldX")),
					Integer.parseInt(elem.getAttribute("worldY"))
				);
		}
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
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public void onPickUp(){
		if(inInventory())
			throw new IllegalStateException("Attempting to pick up an item that is already contained in an inventory.");
		inInventory = true;
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
	public void onDrop(Zone newZone, Point worldPos) throws IllegalStateException {
		if(!inInventory())
			throw new IllegalStateException("Attempting to drop an item that isn't contained in an inventory.");
		inInventory = false;
		this.worldPosition = worldPos;
	}
	
	/**
	 * Returns the world position of this item. Throws 
	 * an exception if this item isn't in the world.
	 * 
	 * @return The world position of this item.
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public Point getPosition() throws IllegalStateException {
		if(inInventory())
			throw new IllegalStateException("Attempting to get the world position of an item which is contained in an inventory.");
		return worldPosition;
	}
	
	/**
	 * Teleports this item to the given world position.
	 * If this item isn't in the world, throws an exception.
	 * 
	 * @param newPos The new world position of this item.
	 * @throws IllegalStateException If this item is in an inventory.
	 */
	public void teleportTo(Point newPos) throws IllegalStateException {
		if(inInventory())
			throw new IllegalStateException("Attempting to teleport an item which is in an inventory.");
		this.worldPosition = newPos;
	}
	
	/**
	 * @return The unique ID of this item.
	 */
	public long getID(){
		return id;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = doc.createElement("item");
		elem.setAttribute("inInventory", inInventory+"");
		elem.setAttribute("ID", id+"");
		if(!inInventory){
			elem.setAttribute("worldX", worldPosition.x+"");
			elem.setAttribute("worldY", worldPosition.y+"");
		}
		return elem;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Item){
			return ((Item)other).id == this.id; // Simple ID check for equal items
		}
		return false;
	}
}
