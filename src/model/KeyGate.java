package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import view.DrawDirection;

/**
 * Defines a gate which may be opened by a key.
 *
 * @author Robert Campbell
 */
public class KeyGate extends Gate implements Serializable, Storable {
	private static final long serialVersionUID = 887504501051715305L;
	private String keyID;

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
        if (super.getState() == State.OPEN) {
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
	public boolean isPassable() {
		return super.isPassable();
	}
}
