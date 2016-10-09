package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;

/**
 * A coin is an item which is treated as a unit of currency.
 * 
 * @author Robert Campbell
 */
public class Coin extends Item{
	public Coin(Point worldPosition) {
		super(worldPosition, true);
	}
	
	public Coin() {
		super(true);
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
	
}
