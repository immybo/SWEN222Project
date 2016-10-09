package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;

/**
 * Represents a generic impassable entity that is able to be interacted with. Typically decorative in nature.
 *
 * @author Martin Chau
 *
 */
public class Furniture extends Entity implements Storable{

	public Furniture(Zone zone, Coord worldPosition, Inventory inventory, String description) {
		super(zone, worldPosition, inventory);
		this.addInteraction(new Inspect(description));
	}
	
	public Furniture(Element elem, Zone[] zones){
		super(elem, zones);
	}

	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Furniture)
			return super.equals(o);
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc);
		return elem;
	}
	
	
	public static class Factory implements StorableFactory<Furniture> {
		
		private Zone[] zones;
		
		public Factory (Zone[] zones){
			this.zones = zones;
		}
		
		public Furniture fromXMLElement(Element elem) {
			return new Furniture(elem, zones);
		}

	}
}
