package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.StorableFactory;
import view.DrawDirection;

/**
 * A sword is a melee-range weapon that 
 * @author Robert Campbell
 *
 */
public class Sword extends Weapon {
	private static final long serialVersionUID = 7703183004816103712L;

	public Sword(){
		super(false);
	}

	public Sword(Point worldPosition) {
		super(worldPosition, false);
	}

	public Sword(Element elem) {
		super(elem);
	}
	
	@Override
	public void onCollision(Player player){
		super.onCollision(player);
		player.equipWeapon(this);
	}

	@Override
	public int getDamage() {
		return 10;
	}
	
	@Override
	public String getDrawImagePath(DrawDirection d){
		return "images/sword.png";
	}
	
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc,"Sword");
		return elem;
	}
	
	public static class Factory implements StorableFactory<Sword> {
		@Override
		public Sword fromXMLElement(Element elem) {
			return new Sword(elem);
		}
	}
}
