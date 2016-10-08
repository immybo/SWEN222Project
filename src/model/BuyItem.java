package model;

import view.GameFrame;
import datastorage.Storable;

/**
 * An interaction where when interacted will subtract a certain amount of coins and exchange it for an item. 
 * This interaction can only happen once ,after the interaction has run it will remove itself.
 *
 * @author Martin Chau
 *
 */
public class BuyItem extends Interaction implements Storable{
	private Entity entity;
	private Item item;
	private String itemName;
	private int cost;

	public BuyItem(Entity entity, Item item, String itemName, int cost){
		this.entity = entity;
		this.item = item;
		this.itemName = itemName;
		this.cost = cost;
	}
	
	@Override
	public String getText() {
		return "Buy "+this.itemName;
	}

	@Override
	public void execute(Player p) {
		// show description, give item,  remove interaction, and add a standard interactions
		
		String title = "Buy item?";
		String text = "Are you sure you want to buy a " + itemName + " for " + cost + " coins?";
		boolean no = GameFrame.instance().displayBooleanQuestion(title, text) == 1;
		
		if(no){
			
		}
		if(p.getInventory().hasRoom()){
			p.getInventory().addItem(this.item);
			this.entity.removeInteraction(this);
		} else {
			GameFrame.instance().displayMessage("You can't purchase this item as your inventory is full.");
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof BuyItem){
			BuyItem i = (BuyItem) o;
			if(this.entity.equals(i.entity) && this.item.equals(i.item)
					&& this.itemName.equals(i.itemName) && this.cost == i.cost){
				return super.equals(o);
			}
		}
		return false;
	}

}