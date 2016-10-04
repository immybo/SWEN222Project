package model;

import java.awt.Point;

/**
 * A coin is an item which is treated as a unit of currency.
 * 
 * @author Robert Campbell
 */
public class Coin extends Item {
	public Coin(Point worldPosition) {
		super(worldPosition, true);
	}
	
	public Coin() {
		super(true);
	}
}
