package model;

import datastorage.Storable;
import util.Coord;
import view.DrawDirection;

/**
 * Represents a generic impassable entity that is able to be interacted with. Typically decorative in nature.
 *
 * @author Martin Chau
 *
 */
public class Furniture extends Entity implements Storable{
	public Furniture(Zone zone, Coord worldPosition, Inventory inventory, String description) {
		super(zone, worldPosition, inventory);
		this.addInteraction(new Inspect(description));
	}

	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Furniture)
			return super.equals(o);
		return false;
	}
	@Override
	public String getDrawImagePath(DrawDirection d) {
		DrawDirection drawDir = DrawDirection.getCompositeDirection(d, this.getCoord().getFacing());
		String dir = "";
		if(drawDir == DrawDirection.NE) dir = super.getDrawImagePath(d) + "TR.png";
		else if(drawDir == DrawDirection.NW) dir = super.getDrawImagePath(d) + "TR.png";
		else if(drawDir == DrawDirection.SE) dir = super.getDrawImagePath(d) + "BR.png";
		else if(drawDir == DrawDirection.SW) dir = super.getDrawImagePath(d) + "BL.png";
		return null;
	}
	
}
