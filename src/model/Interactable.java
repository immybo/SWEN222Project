package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import datastorage.Storable;

/**
 * Defines something which has certain interactions that
 * can be performed on it. Each interaction has some
 * text that describes it (which may be displayed to a
 * user). 
 * 
 * @author Robert Campbell
 *
 */
public abstract class Interactable implements Serializable, Storable {
	private static final long serialVersionUID = 4932231803603496537L;
	private ArrayList<Interaction> interactions = new ArrayList<Interaction>();
	
	/**
	 * Adds an interaction that may be performed on this
	 * interactable. This in no way guarantees that the interaction
	 * may be executed during gameplay, as this is handled externally.
	 * 
	 * @param i The interaction to add to this interactable.
	 */
	public void addInteraction(Interaction i){
		interactions.add(i);
	}
	
	/**
	 * Removes a given interaction from this interactable.
	 * The interaction may therefore no longer be performed
	 * unless reference to it is retained somewhere else in code.
	 * Does nothing if the interaction is not found.
	 * 
	 * @param i The interaction to remove.
	 * @return Whether or not an interaction was removed.
	 */
	public boolean removeInteraction(Interaction i){
		return interactions.remove(i);
	}
	
	/**
	 * Gets all interactions that this interactable can perform.
	 * 
	 * @return An array of all interactions that this interactable can perform.
	 */
	public Interaction[] getInteractions(){
		return interactions.toArray(new Interaction[0]);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Interactable){
			Interactable i = (Interactable) o;
			return this.interactions.containsAll(i.interactions);// && i.interactions.containsAll(this.interactions);
		}
		return false;
	}
}
