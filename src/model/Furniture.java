package model;

import util.Coord;

/**
 * Represents a generic impassable entity that is able to be interacted with. Typically decorative in nature.
 * 
 * @author Martin Chau
 *
 */
public class Furniture extends Entity {
	
	public Furniture(Zone zone, Coord worldPosition, Inventory inventory, double size, String description) {
		super(zone, worldPosition, inventory, size);
		this.addInteraction(new Inspect(description));
	}

	@Override
	public boolean isPassable() {
		return false;
	}
	
}
