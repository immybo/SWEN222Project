package model;

import java.awt.Point;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	private Coord coord;
	private Zone zone;

	/**
	 * Creates a new character with the given parameters.
	 *
	 * @param zone     The initial zone of the character.
	 * @param coord The initial position of the character.
	 */
	public Character(Zone zone, Coord coord) {
		this.zone = zone;
		this.coord = coord;
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
	}

	/**
	 * Moves by 1 square in the given direction.
	 * No checking is made as to whether this should be possible or not.
	 * Does set the direction of this character to be the appropriate one.
	 *
	 * @param dir The direction to move in.
	 */
	public void moveIn(Direction dir){
		moveIn(dir, 1);
	}

	/**
	 * Moves by a certain amount of squares in the given direction.
	 * No checking is made as to whether this should be possible or not.
	 * Does set the direction of this character to be the appropriate one.
	 *
	 * @param dir The direction to move in.
	 * @param amount The amount of squares to move.
	 */
	public void moveIn(Direction dir, int amount){
		Point originalPoint = getCoord().getPoint();
		Point newPoint = Direction.move(originalPoint, dir, amount);
		setCoord(new Coord(dir, newPoint));
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Character) {
			return ((Character) other).zone.equals(zone) && ((Character) other).coord.equals(coord);
		}
		return false;
	}

	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("character");
		elem.setAttribute("coord", coord.toString());
		elem.setAttribute("zoneID", zone.getID() + "");
		return elem;
	}

	public String getDrawImagePath() {
		return "images/pupo.png";
	}

	public PointD getDrawPosition() {
		return new PointD(coord.getPoint().getX(), coord.getPoint().getY());
	}

	public double getDepth() {
		return coord.getPoint().getY() + 0.1; //to make it above the floor
	}
}
