package model;

import java.io.Serializable;

import org.w3c.dom.*;

import datastorage.Storable;

/**
 * Defines something with a user-readable string
 * that may be executed with regards to a game.
 *
 * @author Robert Campbell
 */
public abstract class Interaction implements Storable, Serializable {
	private static final long serialVersionUID = -8279745242900716669L;

	/**
	 * Returns the human-readable text of this interaction.
	 * Note that this does not necessarily return the same
	 * string as this object's toString.
	 *
	 * @return Some human-readable text describing this interaction.
	 */
	public abstract String getText();

	/**
	 * Performs the result of the player specifying this
	 * interaction; for example, pressing a button may
	 * open similarly coloured doors.
	 *
	 * @param p Player that starts the interaction
	 * @param message to return to player
	 */
	public abstract String execute(Player p);

	/**
	 * An interaction can display a message that it wants
	 * to be displayed to any player that performs it.
	 * The default of an empty string indicates that no
	 * message will be displayed.
	 * 
	 * @return The message to be displayed.
	 */
	public String getMessageText(){ return ""; }

	public Element toXMLElement(Document doc, String type){
		Element elem = doc.createElement(type);
		elem.setAttribute("text", getText());
		return elem;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Interaction;
	}
}
