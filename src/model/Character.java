package model;

import java.awt.Point;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;

/**
 * 
 * 
 * @author Robert Campbell
 */
public abstract class Character implements Serializable, Storable {
	private Coord coord;
	private Zone zone;
	
	/**
	 * Creates a new character with the given parameters.
	 * 
	 * @param zone The initial zone of the character.
 	 * @param position The initial position of the character.
	 */
	public Character(Zone zone, Coord coord){
		this.zone = zone;
		this.coord = coord;
	}
	
	/**
	 * Creates a new character from an XML element.
	 * 
	 * @param elem The element to create the new character from.
	 * @param zones The list of all zones to find the one that this character belongs to from.
	 */
	protected Character(Element elem, Zone[] zones){
		this.coord = Coord.fromString(elem.getAttribute("coord"));
		long zoneID = Long.parseLong(elem.getAttribute("zoneID"));
		for(Zone z : zones){
			if(z.getID() == zoneID)
				this.zone = z;
		}
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Character){
			return ((Character)other).zone.equals(zone) && ((Character)other).coord.equals(coord);
		}
		return false;
	}
	
	public Element toXMLElement(Document doc){
		Element elem = doc.createElement("character");
		elem.setAttribute("coord", coord.toString());
		elem.setAttribute("zoneID", zone.getID()+"");
		return elem;
	}
}
