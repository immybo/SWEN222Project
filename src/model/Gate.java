package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import view.DrawDirection;

/**
 * Defines an entity which is passable in certain
 * states, and impassable in others. Specifically,
 * a gate in the locked state (where they usually
 * start) is impassable until unlocked by some
 * action. In the closed state, it is impassable
 * but may simply be opened to become passable.
 *
 * @author Robert Campbell
 */
public abstract class Gate extends Entity implements Storable {
	private static final long serialVersionUID = -532830362301640709L;

	public enum State {
		OPEN,
		CLOSED,
		LOCKED
	}

	private State state;

	public Gate(State initial, Zone zone, Coord worldPosition){
		super(zone, worldPosition, null);
		state = initial;
	}
	
	public Gate(Zone[] zones, Element elem){
		super(elem, zones);
		String stateString = elem.getAttribute("state");
		if(stateString.equals("OPEN"))
			state = State.OPEN;
		else if(stateString.equals("CLOSED"))
			state = State.CLOSED;
		else
			state = State.LOCKED;
	}

	public State state(){
		return this.state;
	}

	/**
	 * Returns whether or not this gate is currently passable.
	 * Gates are only passable if they are in the open state.
	 */
	@Override
	public boolean isPassable(){
		return state == State.OPEN;
	}

	/**
	 * Attempts to transition between two states. Does nothing
	 * if the gate isn't currently in the initial state.
	 *
	 * @param initial The state that the gate is expected to currently be in.
	 * @param result The state that the gate will be moved to if it is currently in the initial state.
	 * @return Whether or not any state transition occurred.
	 */
	private boolean transition(State initial, State result){
		if(this.state != initial)
			return false;
		this.state = result;
		return true;
	}

	/**
	 * If this gate was in the locked state, moves it
	 * to the unlocked state. If it was not in the locked
	 * state, does nothing.
	 *
	 * @return Whether or not the state of the door was changed.
	 */
	public boolean unlock() {
		return transition(State.LOCKED, State.CLOSED);
	}

	/**
	 * If this gate was in the closed state, moves
	 * it to the locked state. If it was not in the closed
	 * state, does nothing.
	 *
	 * @return Whether or not the state of the door was changed.
	 */
	public boolean lock() {
		return transition(State.CLOSED, State.LOCKED);
	}

	/**
	 * If this gate was in the closed state, moves
	 * it to the open state. If it was not in the closed
	 * state, does nothing.
	 *
	 * @return Whether or not the state of the door was changed.
	 */
	public boolean open() {
		return transition(State.CLOSED, State.OPEN);
	}

	/**
	 * If this gate was in the open state, moves it
	 * to the closed state. If it was not in the open
	 * state, does nothing.
	 *
	 * @return Whether or not the state of the door was changed.
	 */
	public boolean close() {
		return transition(State.OPEN, State.CLOSED);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Gate){
			Gate g = (Gate) o;
			return this.state == g.state && super.equals(o);
		}
		return false;
	}

	@Override
    public String getDrawImagePath(DrawDirection d) {
        if (state == State.OPEN) {
            return "images/GateOpen.png";
        } else if (state == State.CLOSED) {
            return "images/GateClosed.png";
        } else {
            return "images/GateLocked.png";
        }
    }

    @Override
    public double getDepthOffset() {
        if (state == State.OPEN) {
            return 0;
        } else {
            return 0.6;
        }
    }

    @Override
    public int getYOffset() {
        if (state == State.OPEN) {
            return 0;
        } else {
            return 31;
        }
    }
    
    public State getState(){
    	return state;
    }
    
    @Override
	public Element toXMLElement(Document doc, String type){
		Element elem = super.toXMLElement(doc, type);
		elem.setAttribute("State", state+"");
		return elem;
	}
	
	
	public static class Factory implements StorableFactory<Furniture> {
		
		private Zone[] zones;
		
		public Factory (Zone[] zones){
			this.zones = zones;
		}
		
		public Furniture fromXMLElement(Element elem) {
			return new Furniture(elem, zones);
		}

	}
}
