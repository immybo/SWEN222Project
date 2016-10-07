package model;

import java.io.Serializable;

import datastorage.Storable;

/**
 * The simplest type of interaction that most if not all interactables will have, showing a string description when called.
 *
 * @author Martin Chau
 *
 */
public class Inspect extends Interaction implements Serializable, Storable {
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
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Inspect){
			Inspect i = (Inspect) o;
			return this.description.equals(i.description) && super.equals(o);
		}
		return false;
	}
}
