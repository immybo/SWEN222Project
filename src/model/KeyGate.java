package model;

import util.Coord;
import util.PointD;

/**
 * Defines a gate which may be opened by a key.
 * 
 * @author Robert Campbell
 */
public class KeyGate extends Gate {
	private String keyID;
	private boolean passable;
	
	public KeyGate(State initial, Zone zone, Coord worldPosition, double size, String keyID) {
		super(initial, zone, worldPosition, size);
		this.keyID = keyID;
	}

	/**
	 * Returns whether or not this KeyGate is opened
	 * by the given key.
	 */
	public boolean openedBy(Key key){
		return false;
	}

	@Override
	public void addInteraction(Interaction i) {
		// TODO Auto-generated method stub	
	}

	@Override
	public boolean removeInteraction(Interaction i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Interaction[] getInteractions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPassable() {
		return this.passable;
	}
}
