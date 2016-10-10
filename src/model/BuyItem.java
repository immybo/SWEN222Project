package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.GameFrame;
import datastorage.Storable;
import datastorage.StorableFactory;

/**
 * An interaction where when interacted will subtract a certain amount of coins and exchange it for an item. 
 * This interaction can only happen once ,after the interaction has run it will remove itself.
 *
 * @author Martin Chau
 *
 */
public class BuyItem extends Interaction implements Storable, Serializable {
	private static final long serialVersionUID = 8169296467720083387L;
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
	
	public BuyItem(Item item, String itemName, int cost) {
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
	
	public void setEntity(Entity e){
		this.entity = e;
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
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "BuyItem");
		elem.setAttribute("itemName", itemName);
		elem.setAttribute("cost", cost+"");
		elem.appendChild(item.toXMLElement(doc));
		return elem;
	}
	
	public static class Factory implements StorableFactory<BuyItem> {

		@Override
		public BuyItem fromXMLElement(Element elem) {
			String itemName = elem.getAttribute("itemName");
			int cost = Integer.parseInt(elem.getAttribute("cost"));
			NodeList nl = elem.getChildNodes();
			Item item = new Item.Factory().fromNode(nl.item(1));
			return new BuyItem(item, itemName, cost);
		}
	}

}