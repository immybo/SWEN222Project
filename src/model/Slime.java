package model;

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
}
