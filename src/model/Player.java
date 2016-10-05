package model;

import datastorage.Storable;
import datastorage.StorableFactory;

import org.w3c.dom.*;

import util.Coord;
import util.Direction;

public class Player extends Character implements Storable {
	public final boolean pupo; //!pupo --> yelo
	private Inventory inventory;
	
	public Player(Zone zone, Coord coord, boolean isPupo) {
		super (zone, coord);
		this.pupo = isPupo;
	}
	
	private Player(Zone[] zones, Element elem){
		super(elem, zones);
		this.pupo = Boolean.parseBoolean(elem.getAttribute("pupo"));
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Player){
			return ((Player)other).pupo == pupo && super.equals(other);
		}
		return false;
	}
	
	public Inventory getInventory(){
		return this.inventory;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc);
		elem.setNodeValue("playablecharacter");
		elem.setAttribute("pupo", pupo+"");
		return elem;
	}
	
	public static class Factory implements StorableFactory<Player> {
		private Zone[] zones;
		
		public Factory(Zone[] zones){
			this.zones = zones;
		}
		
		@Override
		public Player fromXMLElement(Element elem) {
			return new Player(zones, elem);
		}
	}
}
