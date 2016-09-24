package model;

/**
 * Defines a gate which may be opened by a key.
 * 
 * @author Robert Campbell
 */
public class KeyGate extends Gate {
	/**
	 * Returns whether or not this KeyGate is opened
	 * by the given key.
	 */
	public boolean openedBy(Key key){
		return false;
	}
}
