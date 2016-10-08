package model;

import java.awt.Point;
import java.util.ArrayList;

import datastorage.Storable;
import util.Coord;

/**
 * An interaction that can be done on a portal
 * It will teleport the player to the destination portal.
 *
 * @author Martin Chau
 *
 */
public class UsePortal extends Interaction implements Storable{
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
	public void execute(Player player) {

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
			//TODO show dialog no room on other side... should very rarely happen
			return;
		}
		//remove the player from the origin zone and add to destination zone;
		origin.getZone().removeCharacter(player);
		destination.getZone().addCharacter(player);
		//set the player's zone and position
		player.setZone(destination.getZone());
		player.setCoord(new Coord(player.getCoord().getFacing(), freePosition));
		//show dialog you are now in zone lalal
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof UsePortal){
			UsePortal up = (UsePortal) o;
			if(this.portal.equals(up.portal))
				return super.equals(o);
		}
		return false;
	}
}