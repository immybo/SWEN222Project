package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.StorableFactory;
import view.DrawDirection;

/**
 * A coin is an item which is treated as a unit of currency.
 * 
 * @author Robert Campbell
 */
public class Coin extends Item {
	private static final long serialVersionUID = -9106634193261392380L;
	public Coin(Point worldPosition) {
		super(worldPosition, false);
	}
	
	public Coin() {
		super(false);
	}
	
	public Coin(Element elem){
		super(elem);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Coin)
			return super.equals(o);
		return false;
	}
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Coin");
		return elem;
	}
	
	public static class Factory implements StorableFactory<Coin> {
		@Override
		public Coin fromXMLElement(Element elem) {
			return new Coin(elem);
		}
	}
	@Override
	public String getDrawImagePath(DrawDirection d){
		return "images/coin.png";
	}
	
}
