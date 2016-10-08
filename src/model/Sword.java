package model;

import java.awt.Point;

/**
 * A sword is a melee-range weapon that 
 * @author Robert Campbell
 *
 */
public class Sword extends Weapon {
	public Sword(){
		super(false);
	}

	public Sword(Point worldPosition) {
		super(worldPosition, false);
	}

	@Override
	public int getDamage() {
		return 1;
	}
}
