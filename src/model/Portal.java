package model;

import util.Coord;
/**
 * Type of Entity that when interacted with will teleport the player to the other portal in the world.
 *
 * @author Aikon
 */

public class Portal extends Entity {
	private Portal pairPortal;
	private String portalID;

	public Portal(Zone zone, Coord worldPosition, String portalID) {
		super(zone, worldPosition, null);
		this.portalID = portalID;
		this.addInteraction(new Inspect("Some kind of portal... I wonder where it leads."));
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

	public String getPortalID(){
		return this.portalID;
	}

	@Override
	public boolean isPassable() {
		return false;
	}

}
