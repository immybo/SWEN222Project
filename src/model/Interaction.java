package model;

import org.w3c.dom.*;

import datastorage.Storable;

/**
 * Defines something with a user-readable string
 * that may be executed with regards to a game.
 *
 * @author Robert Campbell
 */
public abstract class Interaction implements Storable {
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
	 */
	public abstract void execute(Player p);

	@Override
	public Element toXMLElement(Document doc){
		Element elem = doc.createElement("interaction");
		elem.setAttribute("text", getText());
		return elem;
	}
	
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
