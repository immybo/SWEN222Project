package model;

import datastorage.Storable;
import util.Coord;

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
}
