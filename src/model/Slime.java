package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import datastorage.StorableFactory;
import util.Coord;
import view.DrawDirection;

/**
 * A Slime is just a simple Enemy.
 * They don't attack back.
 * 
 * @author Robert Campbell
 */
public class Slime extends Enemy {
	private static final long serialVersionUID = 1218720728494030495L;

	public Slime(Zone zone, Coord coord) {
		super(zone, coord, 10);
	}

	public Slime(Element elem, Zone[] zones) {
		super(elem, zones);
	}

	@Override
	public String getDrawImagePath(DrawDirection d) {
		DrawDirection drawDir = DrawDirection.getCompositeDirection(d, this.getCoord().getFacing());
		String dir = "";
		switch (drawDir) {
		case NE: dir = "TR"; break;
		case NW: dir = "TL"; break;
		case SE: dir = "BR"; break;
		case SW: dir = "BL"; break;
		default: throw new IllegalStateException();
		}
		return ("images/redSlime" +  dir + ".png");
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Slime");
		return elem;
	}
	
	public static class Factory implements StorableFactory<Slime> {
		private Zone[] zones;
		public Factory(Zone[] zones){
			this.zones = zones;
		}
		
		@Override
		public Slime fromXMLElement(Element elem) {
			return new Slime(elem, zones);
		}
		
	}
}
