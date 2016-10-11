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
	private static final long serialVersionUID = 3600680322909171302L;
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
	
	public InspectWithItem(Item item, String giveDescription, String altDescription){
		this.item = item;
		this.giveDescription = giveDescription;
		this.altDescription = altDescription;
	}
	
	@Override
	public String getText() {
		return "Inspect";
	}

	@Override
	public String execute(Player p) {
		if(p.getInventory().hasRoom()){
			p.getInventory().addItem(this.item);
			this.entity.removeInteraction(this);
			this.entity.addInteraction(new Inspect(this.altDescription));
			return giveDescription; 
		} else {
			return "There might be something here, but you are too heavy";
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
		Element elem = super.toXMLElement(doc, "InspectWithItem");
		elem.setAttribute("giveDescription", giveDescription);
		elem.setAttribute("altDescription", altDescription);
		elem.appendChild(item.toXMLElement(doc));
		return elem;
	}
	
	public static class Factory implements StorableFactory<InspectWithItem> {

		@Override
		public InspectWithItem fromXMLElement(Element elem) {
			String giveDescription = elem.getAttribute("giveDescription");
			String altDescription = elem.getAttribute("altDescription");
			NodeList nl = elem.getChildNodes();
			Item item = new Item.Factory().fromNode(nl.item(1));
			return new InspectWithItem(item, giveDescription, altDescription);
		}
	}

	public void setEntity(Entity e) {
		this.entity = e;
	}

}
