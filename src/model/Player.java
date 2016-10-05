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
		this.inventory = new Inventory(10);
	}
	
	private Player(Zone[] zones, Element elem){
		super(elem, zones);
		this.pupo = Boolean.parseBoolean(elem.getAttribute("pupo"));
		// TODO parse the inventory from the element
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
	
	/**
	 * As well as moving this Player to the given coord,
	 * makes sure that any items at the coord know that
	 * the player is on top of them.
	 */
	@Override
	public void setCoord(Coord coord){
		super.setCoord(coord);
		
		for(Item item : getZone().getItems(coord.getPoint())){
			item.onCollision(this);
		}
	}
	
	@Override
	public void moveIn(Direction dir){
		moveIn(dir, 1);
	}
	
	/**
	 * As well as moving this Player by the given amount
	 * in the given direction, makes sure that any items
	 * at the new coord know that the player is on top of
	 * them.
	 */
	@Override
	public void moveIn(Direction dir, int amount){
		super.moveIn(dir, amount);
		
		for(Item item : getZone().getItems(this.getCoord().getPoint())){
			item.onCollision(this);
		}
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
