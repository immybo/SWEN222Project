package model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import util.Coord;
import view.GameFrame;

/**
 * An interaction that can be done on a portal
 * It will teleport the player to the destination portal.
 *
 * @author Martin Chau
 *
 */
public class UsePortal extends Interaction implements Serializable {
	private static final long serialVersionUID = -7601751065430972886L;
	private Portal portal;

	/**
	 * Constructor for portal that takes the portal the interaction is attached to, the origin portal.
	 * @param portal The portal this interaction is for.
	 */
	public UsePortal(Portal portal){
		this.portal = portal;
	}

	@Override
	public String getText() {
		return "Use Portal";
	}

	@Override
	public String execute(Player player) {

		Portal origin = this.portal;
		Portal destination = origin.getPairPortal();
		//first check if the destination has room
		Point p = destination.getWorldPosition().getPoint();
		ArrayList<Point> points = new ArrayList<Point>();
		Point freePosition = null;
		points.add(new Point(p.x-1, p.y));
		points.add(new Point(p.x+1, p.y));
		points.add(new Point(p.x, p.y-1));
		points.add(new Point(p.x, p.y+1));
		for(Point i: points){
			if(!destination.getZone().checkForObstruction(i)){
				freePosition = i;
				break;
			}
		}
		if(freePosition == null){
			return "Theres no room on the other side";
		}
		//remove the player from the origin zone and add to destination zone;
		origin.getZone().removeCharacter(player);
		destination.getZone().addCharacter(player);
		//set the player's zone and position
		player.setZone(destination.getZone());
		player.setCoord(new Coord(player.getCoord().getFacing(), freePosition));
		return "*Doctor Who Theme Song* ... you are now in zone " + destination.getZone().getName();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof UsePortal){
			UsePortal up = (UsePortal) o;
			if(this.portal.getCoord().equals(up.portal.getCoord()))
				return super.equals(o);
		}
		return false;
	}

	@Override
	public Element toXMLElement(Document doc) {
		return super.toXMLElement(doc, "UsePortal");
	}
	
	
}