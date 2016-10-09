package model;

import java.io.Serializable;

import datastorage.Storable;
import model.Gate.State;
import util.Coord;
import util.PointD;
import view.DrawDirection;

/**
 * Defines a gate which may be opened by a key.
 *
 * @author Robert Campbell
 */
public class KeyGate extends Gate implements Serializable, Storable {
	private String keyID;
	private boolean passable;

	public KeyGate(State initial, Zone zone, Coord worldPosition, String keyID) {
		super(initial, zone, worldPosition);
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
	
	@Override
    public String getDrawImagePath(DrawDirection d) {
		DrawDirection drawDir = DrawDirection.getCompositeDirection(d, this.getCoord().getFacing());
		String dir = "";
		if(drawDir == DrawDirection.NE || drawDir == DrawDirection.SW) dir = "TR.png";
		else if(drawDir == DrawDirection.NW || drawDir == DrawDirection.SE) dir = "TL.png";
        if (super.getState() == State.OPEN) {
            return "images/"+ keyID + "GateOpen" + dir;
        } else {
            return "images/"+ keyID + "GateClosed" + dir;
        }
    }
	
	@Override
	public boolean equals(Object o){
		if(o instanceof KeyGate){
			KeyGate kg = (KeyGate) o;
			if(this.keyID.equals(kg.keyID) && this.passable == kg.passable)
				return super.equals(o);
		}
		return false;
	}
}
