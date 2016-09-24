package model;
/**
 * An interaction where when interacted will show a message and give an item. This interaction can only happen once and is a substitute for "Inspect". After the interaction has run, it will replace itself wtih
 * 
 * @author Martin Chau
 *
 */
public class InspectWithItem implements Interaction {
	private Item item;
	private String description;
	
	public InspectWithItem(Item item, String description){
		this.item = item;
		this.description = description;
	}
	@Override
	public String getText() {
		return "Inspect";
	}

	@Override
	public void execute() {
		//TODO takes a player and the object. 
		// show description, give item,  remove interaction, and add a standard interactions
	}

}
