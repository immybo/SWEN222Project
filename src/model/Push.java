package model;

import java.awt.Point;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;

/**
 * An interaction where when interacted will push the entity one square opposite
 * to the player if that square is free, an item on the square to be pushed on is 
 * not considered free even if the item is collidable.
 * 
 *
 * @author Martin Chau
 *
 */
public class Push extends Interaction implements Storable, Serializable {
	private static final long serialVersionUID = 3600680322909171302L; // TODO
																		// this
																		// has
																		// the
																		// same
																		// id as
																		// inspect
																		// with
																		// item
	private Entity entity;

	public Push(Entity entity) {
		this.entity = entity;
	}

	@Override
	public String getText() {
		return "Push";
	}

	@Override
	public void execute(Player player) {

		if (!(player.getZone().equals(this.entity.getZone())))
			return;
		Point p = player.getCoord().getPoint();
		Point[] points = new Point[4];
		points[0] = new Point(p.x - 1, p.y);
		points[1] = new Point(p.x + 1, p.y);
		points[2] = new Point(p.x, p.y - 1);
		points[3] = new Point(p.x, p.y + 1);
		Point pushOnto = null;
		// entity to the left of player
		if (entity.getCoord().getPoint().equals(points[0])) {
			pushOnto = new Point(points[0].x - 1, points[0].y);
		}
		// entity to the right of player
		if (entity.getCoord().getPoint().equals(points[1])) {
			pushOnto = new Point(points[1].x + 1, points[1].y);
		}
		// entity to the top of player
		if (entity.getCoord().getPoint().equals(points[2])) {
			pushOnto = new Point(points[2].x, points[2].y - 1);
		}
		// entity to the bot of player
		if (entity.getCoord().getPoint().equals(points[3])) {
			pushOnto = new Point(points[3].x, points[3].y + 1);
		}
		if (entity.getZone().checkForObstruction(pushOnto))
			return;
		if (entity.getZone().getItem(pushOnto) != null)
			return;
		entity.teleportTo(new Coord(entity.getCoord().getFacing(),pushOnto));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Push) {
			Push i = (Push) o;
			if (this.entity.equals(i.entity)) {
				return super.equals(o);
			}
		}
		return false;
	}

	@Override
	public Element toXMLElement(Document doc) {
		// TODO fix this josh, data storage man
		Element elem = super.toXMLElement(doc, "Inspect");
		elem.setAttribute("giveDescription", giveDescription);
		elem.setAttribute("altDescription", altDescription);
		elem.appendChild(entity.toXMLElement(doc));
		elem.appendChild(item.toXMLElement(doc));
		super.toXMLElement(doc);
		return elem;
	}

	public static class Factory implements StorableFactory<Push> {

		private Zone[] zones;

		public Factory(Zone[] zones) {
			this.zones = zones;
		}

		@Override
		public Push fromXMLElement(Element elem) {
			String giveDescription = elem.getAttribute("giveDescription");
			String altDescription = elem.getAttribute("altDescription");
			NodeList nl = elem.getChildNodes();
			Entity entity = new Entity.Factory(zones).fromNode(nl.item(0));
			Item item = new Item.Factory().fromNode(nl.item(1));
			return new Push(entity, item, giveDescription, altDescription);
		}
	}

}
