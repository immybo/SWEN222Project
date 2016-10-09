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
import view.DrawDirection;

public class Player extends Character implements Storable, Serializable {
	public final boolean pupo; //!pupo --> yelo
	private Inventory inventory;
	private Weapon equipped;

	private transient Timer movementTimer;
	
	private transient Point toMove;
	private transient Enemy toAttack;
	
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
	
	/**
	 * A player ticking causes them to move
	 * by one tile or attack for one tick.
	 */
	@Override
	public void tick(){
		if(toAttack != null){
			if(equipped != null){
				toAttack.damage(equipped.getDamage());
			}
			else{
				toAttack.damage(1); // players always can damage a little bit... just not much
			}
			
			if(toAttack.isDead()){
				getZone().removeCharacter(toAttack);
				toAttack = null;
			}
		}
		else if(toMove != null){
			moveToPointTick(toMove);
		}
	}
	
	public void attack(Enemy victim){
		toAttack = victim;
	}
	
	public void equipWeapon(Weapon newWeapon){
		if(!inventory.containsItem(newWeapon))
			throw new IllegalArgumentException("Can't equip a weapon that isn't in a player's inventory!");

		inventory.removeItem(newWeapon);
		inventory.addItem(equipped);
		equipped = newWeapon;
	}
	
	public Weapon getEquipped(){
		return equipped;
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
	 * Schedules this player to move in the
	 * given direction by the given amount.
	 * Halts the player's movement if they
	 * can't move by the given amount.
	 */
	@Override
	public boolean moveIn(Direction dir, int amount){
		if(movementTimer != null) movementTimer.cancel();
		return moveInstant(dir, amount);
	}
	
	/**
	 * Instantly moves this player in the given direction
	 * by the given amount.
	 * 
	 * @param dir The direction to move in.
	 * @param amount The amount to move.
	 * @return Whether or not the player moved.
	 */
	private boolean moveInstant(Direction dir, int amount){
		boolean ok = super.moveIn(dir, amount);
		
		if(ok){
			for(Item item : getZone().getItems(this.getCoord().getPoint())){
				item.onCollision(this);
			}
		}
		
		return ok;
	}
	
	/**
	 * Schedules this player to move to the
	 * given point. Halts the player's movement
	 * if they can't move to that point.
	 */
	public void moveToPoint(Point newPoint){
		toMove = newPoint;
	}
	
	/**
	 * Performs one tick of moving this player to the
	 * given point.
	 * 
	 * Returns whether or not the player moved at all.
	 * 
	 * @param newPoint The point to move towards.
	 */
	private void moveToPointTick(Point newPoint){
		// Doing this every movement will be a bit costly, but
		// since we have quite a rough grid and slow ticks, it's fine
		Direction[] path = getZone().getPath(getCoord().getPoint(), newPoint);
		if(path == null){
			if(movementTimer != null)
				movementTimer.cancel();
			return; // Couldn't find a path
		}
		
		Direction nextDir = path[0];
		moveInstant(nextDir, 1);
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
	public String getDrawImagePath(DrawDirection d) {
		DrawDirection drawDir = DrawDirection.getCompositeDirection(d, this.getCoord().getFacing());
		String dir = "";
		if(drawDir == DrawDirection.NE) dir = "TR.png";
		else if(drawDir == DrawDirection.NW) dir = "TR.png";
		else if(drawDir == DrawDirection.SE) dir = "BR.png";
		else if(drawDir == DrawDirection.SW) dir = "BL.png";
		if(pupo)
			return ("images/pupo" + dir);
		else
			return ("images/yelo" + dir);
	}
}
