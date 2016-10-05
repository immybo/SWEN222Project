package model;

import util.Coord;
/**
 * Type of Entity that when interacted with will teleport the player to the other portal in the world.
 * 
 * @author Aikon
 */

public class Portal extends Entity {
	private Portal pairPortal;
	
	public Portal(Zone zone, Coord worldPosition, Inventory inventory, double size) {
		super(zone, worldPosition, inventory, size);
	}
	
	/**
	 * Sets the portal to be connected to another portal and sets up the interactions.
	 * This must be done before a player ever tries to interact with the portal.
	 * @param portal
	 */
	public void setPairPortal(Portal portal){
		this.pairPortal = portal;
	}
	
	public Portal getPairPortal(){
		return this.pairPortal;
	}

	@Override
	public boolean isPassable() {
		return false;
	}

}
