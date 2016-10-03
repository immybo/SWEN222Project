package model;

import java.io.Serializable;

import util.Coord;
import util.PointD;

/**
 * Defines a gate which may be opened by a key.
 * 
 * @author Robert Campbell
 */
public class KeyGate extends Gate implements Serializable {
	private String keyID;
	private boolean passable;
	
	public KeyGate(State initial, Zone zone, Coord worldPosition, double size, String keyID) {
		super(initial, zone, worldPosition, size);
		this.keyID = keyID;
		
		//this.addInteraction(i);
	}

	/**
	 * Returns whether or not this KeyGate is opened
	 * by the given key.
	 */
	public boolean openedBy(Key key){
		return false;
	}

	@Override
	public boolean isPassable() {
		return this.passable;
	}

	public void setPassable(boolean passable) {
		this.passable = passable;
	}
}
