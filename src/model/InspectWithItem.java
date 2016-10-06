package model;
/**
 * An interaction where when interacted will show a message and give an item. This interaction can only happen once and is a substitute for "Inspect". After the interaction has run, it will replace itself wtih
 *
 * @author Martin Chau
 *
 */
public class InspectWithItem extends Interaction {
	private Entity entity;
	private Item item;
	private String giveDescription;
	private String altDescription;

	public InspectWithItem(Entity entity, Item item, String giveDescription, String altDescription){
		this.entity = entity;
		this.item = item;
		this.giveDescription = giveDescription;
		this.altDescription = altDescription;
	}
	@Override
	public String getText() {
		return "Inspect";
	}

	@Override
	public void execute(Player p) {
		//TODO takes a player and the object.
		// show description, give item,  remove interaction, and add a standard interactions
		if(p.getInventory().hasRoom()){
			//show dialog box to user
			p.getInventory().addItem(this.item);
			this.entity.removeInteraction(this);
			this.entity.addInteraction(new Inspect(this.altDescription));
		} else {
			//show dialog box
		}
	}

}
