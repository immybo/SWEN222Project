 package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import view.DrawDirection;
/**
 * Type of Entity that when interacted with will teleport the player to the other portal in the world.
 *
 * @author Martin Chau
 */

public class Portal extends Entity implements Storable{
	private static final long serialVersionUID = -8322710806750756876L;
	private Portal pairPortal;
	private String portalID;
	/**
	 * Constructor for a portal
	 * 
	 * @param zone Zone the portal is in
	 * @param worldPosition Position of the portal
	 * @param portalID Unique code to link with another portal. 
	 */
	public Portal(Zone zone, Coord worldPosition, String portalID) {
		super(zone, worldPosition, null);
		this.portalID = portalID;
		this.addInteraction(new Inspect("Some kind of portal... I wonder where it leads."));
		this.addInteraction(new UsePortal(this));
	}
	
	public Portal(Element elem, Zone[] zone){
		super(elem, zone);
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
	public String getDrawImagePath(DrawDirection d){
		return "images/portalTR.png";
	}
	@Override
	public int getYOffset() {
		return 39;
	}

	@Override
	public boolean isPassable() {
		return false;
	}
	
	public boolean equals(Object o){
		if(o instanceof Portal){
			Portal p = (Portal) o;
			if(this.portalID.equals(p.portalID) && this.pairPortal.portalID.equals(p.pairPortal.portalID))
				return super.equals(o);
		}
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Portal");	//FIXME can't save other portal, so how to load it??
		elem.setAttribute("portalID", portalID);
		return elem;
	}
	
	
	public static class Factory implements StorableFactory<Portal> {
		
		private Zone[] zones;
		
		public Factory (Zone[] zones){
			this.zones = zones;
		}
		
		public Portal fromXMLElement(Element elem) {
			String portalID = elem.getAttribute("portalID");
			long zoneID = Long.parseLong(elem.getAttribute("zoneID"));
			Zone zone = zones[0];
			for(Zone z : zones){
				if(z.getID() == zoneID)
					zone = z;
			}
			Coord coord = Coord.fromString(elem.getAttribute("coord"));
			return new Portal(zone, coord, portalID); //FIXME some how set the pairPortal for this portal
		}

	}

}
