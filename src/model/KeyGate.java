package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import model.Gate.State;
import util.Coord;
import view.DrawDirection;

/**
 * Defines a gate which may be opened by a key.
 *
 * @author Robert Campbell
 * @author Martin Chau
 */
public class KeyGate extends Gate implements Serializable, Storable {
	private static final long serialVersionUID = 887504501051715305L;
	private String keyID;
	private boolean passable = false;
	
	/**
	 * Constructor for a keyGate
	 * @param initial State the keyGate created with
	 * @param zone Zone the keyGate is in
	 * @param worldPosition Position of keyGate
	 * @param keyID Unique code that the gate can be unlocked with
	 */
	public KeyGate(State initial, Zone zone, Coord worldPosition, String keyID) {
		super(initial, zone, worldPosition);
		this.keyID = keyID;
		this.addInteraction(new Inspect("Looks like a gate... Is that a key hole?"));
		this.addInteraction(new UseKey(this));
	}
	
	public KeyGate(Element elem, Zone[] zones){
		super(zones, elem);
		this.keyID = elem.getAttribute("keyID");
	}

	/**
	 * Returns whether or not this KeyGate is opened
	 * by the given key.
	 */
	public boolean openedBy(Key key){
		return key.getKeyID().equals(this.keyID);
	}
	
	@Override
    public String getDrawImagePath(DrawDirection d) {
		DrawDirection drawDir = DrawDirection.getCompositeDirection(d, this.getCoord().getFacing());
		String dir = "";
		if(drawDir == DrawDirection.NE || drawDir == DrawDirection.SW) dir = "TR.png";
		else if(drawDir == DrawDirection.NW || drawDir == DrawDirection.SE) dir = "TL.png";
        if (this.isPassable()) {
            return "images/"+ keyID + "GateOpen" + dir;
        } else {
            return "images/"+ keyID + "GateClosed" + dir;
        }
    }
	
	@Override
	public boolean equals(Object o){
		if(o instanceof KeyGate){
			KeyGate kg = (KeyGate) o;
			if(this.keyID.equals(kg.keyID))
				return super.equals(o);
		}
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "KeyGate");
		elem.setAttribute("keyID", keyID);
		return elem;
	}
	
	
	public static class Factory implements StorableFactory<KeyGate> {
		
		private Zone[] zones;
		
		public Factory (Zone[] zones){
			this.zones = zones;
		}
		
		public KeyGate fromXMLElement(Element elem) {
			return new KeyGate(elem, zones);
		}

	}
	
	@Override
	public boolean isPassable(){
		return passable;
	}
	
	/**
	 * Manually set the keyGate passability to the provided boolean
	 * @param passable Boolean of passability, true for passable.
	 */
	public void setPassable(boolean passable){
		this.passable = passable;
	}


}
