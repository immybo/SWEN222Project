package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;

/**
 * The simplest type of interaction that most if not all interactables will have, showing a string description when called.
 *
 * @author Martin Chau
 *
 */
public class Inspect extends Interaction implements Serializable, Storable {
	private static final long serialVersionUID = 2533033331746954099L;
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
		System.out.println("inspecting");
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Inspect){
			Inspect i = (Inspect) o;
			return this.description.equals(i.description) && super.equals(o);
		}
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Inspect");
		elem.setAttribute("description", description);
		super.toXMLElement(doc);
		return elem;
	}
	
	public static class Factory implements StorableFactory<Inspect> {
		@Override
		public Inspect fromXMLElement(Element elem) {
			String description = elem.getAttribute("description");
			return new Inspect(description);
		}
	}
}
