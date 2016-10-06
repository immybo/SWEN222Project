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
		this.addInteraction(new Inspect("Looks like a gate... Is that a key hole?"));
		this.addInteraction(new UseKey(this));
	}

	/**
	 * Returns whether or not this KeyGate is opened
	 * by the given key.
	 */
	public boolean openedBy(Key key){
		return key.getKeyID().equals(this.keyID);
	}

	@Override
	public boolean isPassable() {
		return this.passable;
	}

	public void setPassable(boolean passable) {
		this.passable = passable;
	}
}
