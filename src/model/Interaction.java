package model;

/**
 * Defines something with a user-readable string
 * that may be executed with regards to a game.
 * 
 * @author Robert Campbell
 */
public interface Interaction {
	/**
	 * Returns the human-readable text of this interaction.
	 * Note that this does not necessarily return the same
	 * string as this object's toString.
	 * 
	 * @return Some human-readable text describing this interaction.
	 */
	public String getText();
	
	/**
	 * Performs the result of the player specifying this
	 * interaction; for example, pressing a button may
	 * open similarly coloured doors.
	 */
	public void execute();
}
