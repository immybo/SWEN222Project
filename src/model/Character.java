package model;

import java.awt.Point;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import util.PointD;
import view.Drawable;
import util.Direction;

/**
 * 
 * 
 * @author Robert Campbell
 */
public abstract class Character implements Serializable, Storable, Drawable {
	private static final long serialVersionUID = -8377078297825212109L;
	private Coord coord;
	private Zone zone;

	/* ID to be used by next constructed character */
	private static long nextID = 0;
	
	/* runtime-unique id of this character */
	private long id;
	
	/**
	 * Creates a new character with the given parameters.
	 *
	 * @param zone     The initial zone of the character.
	 * @param coord The initial position of the character.
	 */
	public Character(Zone zone, Coord coord) {
		this.zone = zone;
		this.coord = coord;
		this.id = nextID++;
	}

	/**
	 * Creates a new character from an XML element.
	 *
	 * @param elem  The element to create the new character from.
	 * @param zones The list of all zones to find the one that this character belongs to from.
	 */
	protected Character(Element elem, Zone[] zones) {
		this.coord = Coord.fromString(elem.getAttribute("coord"));
		long zoneID = Long.parseLong(elem.getAttribute("zoneID"));
		for (Zone z : zones) {
			if (z.getID() == zoneID)
				this.zone = z;
		}
		this.id = nextID++;
	}
	
	/**
	 * Moves by 1 square in this character's current direction.
	 * No checking is made as to whether this should be possible or not.
	 */
	public boolean moveForward(){
		return moveIn(this.getCoord().getFacing());
	}
	
	/**
	 * Moves by 1 square in the opposite direction to this character's current direction,
	 * if it is possible. Returns whether or not the character moved.
	 */
	public boolean moveBackwards(){
		return moveIn(Direction.oppositeDirection(this.getCoord().getFacing()));
	}

	/**
	 * Moves by 1 square in the given direction, if it is possible.
	 * Returns whether or not the character moved.
	 *
	 * @param dir The direction to move in.
	 */
	public boolean moveIn(Direction dir){
		return moveIn(dir, 1);
	}

	/**
	 * Moves by a certain amount of squares in the given direction.
	 * Does nothing if this character can't move in that direction.
	 * Does set the direction of this character to be the appropriate one.
	 *
	 * @param dir The direction to move in.
	 * @param amount The amount of squares to move.
	 * @return Whether or not the character moved.
	 */
	public boolean moveIn(Direction dir, int amount){
		Point originalPoint = getCoord().getPoint();
		Point newPoint = Direction.move(originalPoint, dir, amount);
		
		Point nextPoint = originalPoint;
		while(!(nextPoint.equals(newPoint))){
			nextPoint = Direction.move(nextPoint, dir, 1);
			if(zone.checkForObstruction(nextPoint)){
				return false;
			}
		}
			
		setCoord(new Coord(getCoord().getFacing(), newPoint));
		return true;
	}
	
	/**
	 * Rotates this character to the given direction of rotation
	 * 
	 * @param isClockwise Boolean representing the direction of rotation, True for clockwise
	 */
	public void rotate(boolean isClockwise){
		int dirValue = getCoord().getFacing().getDirection();
		
		/* make the change; clockwise increases, anticlockwise decreases */
		dirValue += isClockwise ? 1 : -1;
		
		/* wrap around from 3 to 0 */
		if(dirValue > 3) {
			dirValue = 0;
		}
		/* wrap around from 0 to 3 */
		if(dirValue < 0) {
			dirValue = 3;
		}
		Direction newDirection = new Direction(dirValue);
		Point point = getCoord().getPoint();
		Coord newCoord = new Coord(newDirection, point);
		setCoord(newCoord);
	}

	/**
	 * get character's current position
	 * @return Coord representing position
	 */
	public Coord getCoord() {
		return coord;
	}

	/**
	 * Set the position of the character, forgetting old position
	 * @param coord -- new coordinate 
	 */
	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	/**
	 * get the zone the character is in
	 * @return Zone character is in, null if no zone
	 */
	public Zone getZone() {
		return zone;
	}

	/**
	 * Set the character's zone, forgetting old zone
	 * @param zone -- new zone
	 */
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	/**
	 * Get the character's game-wide unique ID
	 * @return
	 */
	public long getID() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Character){
			Character c = (Character) o;
			if(this.getCoord().equals(c.getCoord()) && this.getZone().equals(c.getZone())){
				return true;
			}
		}
		return false;
	}
	

	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("character");
		elem.setAttribute("coord", coord.toString());
		elem.setAttribute("zoneID", zone.getID() + "");
		return elem;
	}

	public PointD getDrawPosition() {
		return new PointD(coord.getPoint().getX(), coord.getPoint().getY());
	}

	public double getDepthOffset() {
		 // makes it just above the floor
		return 0.1;
	}
	
	@Override
	public int getYOffset() {
		// FIXME someone document this
		return 39;
	}
	
	/**
	 * Ticks any current processes this character
	 * is undertaking. Default implementation does nothing.
	 */
	public void tick(){
		// Do nothing
	}

	public Element toXMLElement(Document doc, String type) {
		Element elem = doc.createElement(type);
		elem.setAttribute("coord", coord.toString());
		elem.setAttribute("zoneID", zone.getID() + "");
		return elem;
	}
	
	public static class Factory implements StorableFactory<Character> {
		
		private Zone[] zones;
		
		public Factory(Zone[] zones){
			this.zones = zones;
		}

		@Override
		public Character fromXMLElement(Element elem) {
			return null;
		}
		
		public Character fromNode(Node n){
			switch(n.getNodeName()){
			case "Player":
				return new Player.Factory(zones).fromXMLElement((Element) n);
			case "Slime":
				return new Slime.Factory(zones).fromXMLElement((Element) n);
			}
			return null;
		}
	}
}
