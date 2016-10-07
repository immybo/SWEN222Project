package model;

import java.awt.Point;

import datastorage.Storable;

/**
 * A coin is an item which is treated as a unit of currency.
 * 
 * @author Robert Campbell
 */
public class Coin extends Item{
	public Coin(Point worldPosition) {
		super(worldPosition, true);
	}
	
	public Coin() {
		super(true);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Coin)
			return super.equals(o);
		return false;
	}
	
}
