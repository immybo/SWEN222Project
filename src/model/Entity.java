package model;

import java.io.Serializable;

import org.w3c.dom.*;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import util.PointD;
import view.DrawDirection;
import view.Drawable;

/**
 * Defines static objects in the game world that can be interacted with.
 *
 * @author Martin Chau
 */
public abstract class Entity extends Interactable implements Serializable, Drawable, Storable {
	private static final long serialVersionUID = 3586592516798073269L;

	public Entity(Zone zone, Coord worldPosition, Inventory inventory) {
		this.zone = zone;
		this.worldPosition = worldPosition;
		this.inventory = inventory;
	}

	protected Entity(Element elem, Zone[] zones){
		this.worldPosition = Coord.fromString(elem.getAttribute("coord"));
		long zoneID = Long.parseLong(elem.getAttribute("zoneID"));
		for (Zone z : zones) {
			if (z.getID() == zoneID)
				this.zone = z;
		}
		NodeList nl = elem.getChildNodes();
		this.inventory = new Inventory.Factory().fromXMLElement((Element) nl.item(0));
		for(int i = 1 ; i < nl.getLength() ; i++){
			Node n = nl.item(i);
			switch(n.getNodeName()){
			case "Inspect":
				Inspect inspect = new Inspect.Factory().fromXMLElement((Element) n);
				this.addInteraction(inspect);
				break;
			case "BuyItem":
				BuyItem buy = new BuyItem.Factory().fromXMLElement((Element) n);
				buy.setEntity(this);
				this.addInteraction(buy);
				break;
			case "InspectWithItem":
				InspectWithItem inspectItem = new InspectWithItem.Factory().fromXMLElement((Element) n);
				inspectItem.setEntity(this);
				this.addInteraction(inspectItem);
				break;
			case "Push":
				Push push = new Push();
				push.setEntity(this);
				this.addInteraction(push);
				break;
			}
		}
	}

	private Zone zone;
	private Coord worldPosition;
	private Inventory inventory;
	
	public Coord getCoord (){
		return worldPosition;
	}
	/**
	 * Teleports this entity to another position
	 * @param coord The position to teleport this entity to.
	 */
	public void teleportTo(Coord coord){
		this.worldPosition = coord;
	}

	public Coord getWorldPosition() {
		return worldPosition;
	}

	public Zone getZone(){
		return zone;
	}
	
	public abstract boolean isPassable();

	private String drawImagePath;

	@Override
	public String getDrawImagePath(DrawDirection d) {
		return this.drawImagePath;
	}

	public void setDrawImagePath(String drawImagePath) {
		this.drawImagePath = drawImagePath;

	}

	@Override
	public double getDepthOffset() {
		return worldPosition.getPoint().getY();
	}

	@Override
	public PointD getDrawPosition() {
		return new PointD(worldPosition.getPoint().getX(),worldPosition.getPoint().getY());
	}
	
	@Override
	public int getYOffset() {
		return 39;
	}

	public Element toXMLElement(Document doc, String type){
		Element elem = doc.createElement(type);
		elem.setAttribute("zoneID", zone.getID()+"");
		elem.setAttribute("coord", worldPosition.toString());
		if(inventory!=null)
			elem.appendChild(inventory.toXMLElement(doc));
		for(Interaction inter : this.getInteractions()){
			elem.appendChild(inter.toXMLElement(doc));
		}
		return elem;
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if(o instanceof Entity){
			Entity e = (Entity) o;
			if(this.zone.equals(e.zone) && this.worldPosition.equals(e.worldPosition) && this.inventory.equals(e.inventory)){
				return super.equals(o);
			}
		}
		return false;
	}
	
	public static class Factory implements StorableFactory<Entity> {
		
		private Zone[] zones;
		
		public Factory(Zone[] zones){
			this.zones = zones;
		}

		@Override
		public Entity fromXMLElement(Element elem) {
			return null;
		}
		public Entity fromNode(Node n){
			switch(n.getNodeName()){
			case "Furniture":
				return new Furniture.Factory(zones).fromXMLElement((Element) n);
			case "KeyGate":
				return new KeyGate.Factory(zones).fromXMLElement((Element) n);
			case "Portal":
				return new Portal.Factory(zones).fromXMLElement((Element) n);
			}
			return null;
		}
	}
}
