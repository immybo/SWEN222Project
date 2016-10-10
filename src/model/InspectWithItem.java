package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import datastorage.Storable;
import datastorage.StorableFactory;

/**
 * An interaction where when interacted will show a message and give an item. This interaction can only happen once and is a substitute for "Inspect". After the interaction has run, it will replace itself wtih
 *
 * @author Martin Chau
 *
 */
public class InspectWithItem extends Interaction implements Storable, Serializable {
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
	
	@Override
	public boolean equals(Object o){
		if(o instanceof InspectWithItem){
			InspectWithItem i = (InspectWithItem) o;
			if(this.entity.equals(i.entity) && this.item.equals(i.item)
					&& this.giveDescription.equals(i.giveDescription) && this.altDescription.equals(i.altDescription)){
				return super.equals(o);
			}
		}
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Inspect");
		elem.setAttribute("giveDescription", giveDescription);
		elem.setAttribute("altDescription", altDescription);
		elem.appendChild(entity.toXMLElement(doc));
		elem.appendChild(item.toXMLElement(doc));
		super.toXMLElement(doc);
		return elem;
	}
	
	public static class Factory implements StorableFactory<InspectWithItem> {
		
		private Zone[] zones;
		
		public Factory(Zone[] zones){
			this.zones = zones;
		}
		
		@Override
		public InspectWithItem fromXMLElement(Element elem) {
			String giveDescription = elem.getAttribute("giveDescription");
			String altDescription = elem.getAttribute("altDescription");
			NodeList nl = elem.getChildNodes();
			Entity entity = new Entity.Factory(zones).fromNode(nl.item(0));
			Item item = new Item.Factory().fromNode(nl.item(1));
			return new InspectWithItem(entity, item, giveDescription, altDescription);
		}
	}

}
