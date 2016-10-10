package model;

import java.awt.Point;

/**
 * A sword is a melee-range weapon that 
 * @author Robert Campbell
 *
 */
public class Sword extends Weapon {
	private static final long serialVersionUID = 7703183004816103712L;

	public Sword(){
		super(false);
	}

	public Sword(Point worldPosition) {
		super(worldPosition, false);
	}

	@Override
	public int getDamage() {
		return 10;
	}
}
