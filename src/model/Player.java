package model;

import datastorage.Storable;
import datastorage.StorableFactory;

import java.awt.Point;
import java.io.Serializable;
import java.util.Timer;

import org.w3c.dom.*;

import util.Coord;
import util.Direction;
import view.DrawDirection;

/**
 * There are two Players in the game world: Pupo and Yelo.
 * Each Player may move around, attack enemies, and interact
 * with objects.
 * 
 * @author Robert Campbell
 */
public class Player extends Character implements Storable, Serializable {
	private static final long serialVersionUID = -3708487301391970342L;
	public final boolean pupo; //!pupo --> yelo
	private Inventory inventory;
	private Weapon equipped;
	
	private transient Point toMove;
	private transient Enemy toAttack;
	/**
	 * Constructor for a player/ main character
	 * 
	 * @param zone Zone player is in 
	 * @param coord Position of player
	 * @param isPupo True for pupo, False for yelo
	 */
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
			if(toMove.equals(getCoord().getPoint()))
				return;
			
			moveToPointTick(toMove);
		}
	}
	
	public void attack(Enemy victim){
		toAttack = victim;
	}
	/**
	 * Manually equip the provided weapon
	 * @param newWeapon Weapon to be equipped
	 */
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
		return moveInstant(dir, amount);
	}
	
	@Override
	public boolean moveForward(){
		return moveIn(this.getCoord().getFacing(), 1);
	}
	
	@Override
	public boolean moveBackwards(){
		return moveIn(Direction.oppositeDirection(this.getCoord().getFacing()), 1);
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
				if(item.getPosition() == null)
					getZone().removeItem(item);
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
		tick();
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
			toMove = null;
			return; // Couldn't find a path
		}
		
		Direction nextDir = path[0];
		this.getCoord().setFacing(nextDir);
		moveInstant(nextDir, 1);
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Player");
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
		switch (drawDir) {
		case NE: dir = "TR"; break;
		case NW: dir = "TL"; break;
		case SE: dir = "BR"; break;
		case SW: dir = "BL"; break;
		default: throw new IllegalStateException();
		}
		String who = pupo ? "pupo" : "yelo";
		String wep = equipped!=null ? "Sword" : "";
		return ("images/" + who + wep + dir + ".png");
	}
	
	/**
	 * Attempts to drop an item in front of the player, this space must be completely free from items and entities, and item dropped must be in inventory
	 * 
	 * @param item Item to drop
	 * @return Returns true if drop was successful, false if otherwise
	 */
	public boolean dropItem(Item item){
		if(!inventory.containsItem(item)) return false; //item must be in inventory
		Point dropOnto = Direction.move(this.getCoord().getPoint(), this.getCoord().getFacing(), 1);
		if (this.getZone().checkForObstruction(dropOnto)) //space to drop must be free of entites/players
			return false;
		if (this.getZone().getItem(dropOnto) != null) //space to drop must be free of other items
			return false;
		item.onDrop(this.getZone(), dropOnto); //set item to world
		this.getInventory().removeItem(item); //remove item from inventory
		return true;
		
	}
}
