package model;

import java.io.Serializable;

/**
 * The simplest type of interaction that most if not all interactables will have, showing a string description when called.
 *
 * @author Martin Chau
 *
 */
public class Inspect extends Interaction implements Serializable {
	private String description;
	public Inspect(String description){
		//TODO add the dialog box popup for parameters
		this.description = description;
	}

	@Override
	public String getText() {
		return "Inspect";
	}

	@Override
	public void execute(Player p) {
		// TODO show dialog box with the description
	}
}
