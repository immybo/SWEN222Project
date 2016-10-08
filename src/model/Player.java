package model;

import datastorage.Storable;
import datastorage.StorableFactory;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.*;

import util.Coord;
import util.Direction;

public class Player extends Character implements Storable, Serializable {
	public final boolean pupo; //!pupo --> yelo
	private Inventory inventory;

	private transient Timer movementTimer;
	
	public Player(Zone zone, Coord coord, boolean isPupo) {
		super (zone, coord);
		this.pupo = isPupo;
		this.inventory = new Inventory(10);
	}
	
	private Player(Zone[] zones, Element elem){
		super(elem, zones);
		this.pupo = Boolean.parseBoolean(elem.getAttribute("pupo"));
		Inventory.Factory factory = new Inventory.Factory();
		this.inventory = factory.fromXMLElement((Element)elem.getChildNodes().item(0));
		// TODO parse the inventory from the element
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Player){
			Player p = (Player) o;
			if(this.inventory.equals(p.inventory) && this.pupo == p.pupo)
				return super.equals(o);
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
	public boolean moveIn(Direction dir){
		return moveIn(dir, 1);
	}
	
	/**
	 * As well as moving this Player by the given amount
	 * in the given direction, makes sure that any items
	 * at the new coord know that the player is on top of
	 * them.
	 */
	@Override
	public boolean moveIn(Direction dir, int amount){
		if(movementTimer != null) movementTimer.cancel();
		return moveInNoCancel(dir, amount);
	}
	
	private boolean moveInNoCancel(Direction dir, int amount){
		boolean ok = super.moveIn(dir, amount);
		
		if(ok){
			for(Item item : getZone().getItems(this.getCoord().getPoint())){
				item.onCollision(this);
			}
		}
		
		return ok;
	}
	
	/**
	 * Attempts to move this player to the given point.
	 * Halts movement if the player can't proceed any further.
	 */
	public void moveToPoint(Point newPoint){
		if(getCoord().getPoint().equals(newPoint)){
			if(movementTimer != null)
				movementTimer.cancel();
			return;
		}
		
		// Doing this every movement will be a bit costly, but
		// since we have quite a rough grid and slow ticks, it's fine
		Direction[] path = getZone().getPath(getCoord().getPoint(), newPoint);
		System.out.println("finding a path");
		if(path == null){
			System.out.println("no path found");
			if(movementTimer != null)
				movementTimer.cancel();
			return; // Couldn't find a path
		}
		System.out.println("next dir " + path[0].getDirection());
		
		Direction nextDir = path[0];
		moveInNoCancel(nextDir, 1);
		
		if(movementTimer == null){
			movementTimer = new Timer("movement timer", true);
			movementTimer.scheduleAtFixedRate(new TimerTask(){
				@Override
				public void run(){
					moveToPoint(newPoint);
				}
			}, 0, 500);
		}
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc);
		elem.setNodeValue("playablecharacter");
		elem.setAttribute("pupo", pupo+"");
		elem.appendChild(this.inventory.toXMLElement(doc));
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

	@Override
	public String getDrawImagePath() {
		if(pupo)
			return "images/pupo.png";
		else
			return "images/yelo.png";
	}
}
