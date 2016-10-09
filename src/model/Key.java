package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.PointD;
import view.DrawDirection;

public class Key extends Item implements Storable{
	private String keyID;

	public Key(Point worldPosition, String keyID) {
		super(worldPosition, false);
		this.keyID = keyID;
	}
	
	protected Key(Element elem){
		super(elem);
		this.keyID = elem.getAttribute("keyID");
	}
	
	public String getKeyID(){
		return this.keyID;
	}
	@Override
	public String getDrawImagePath(DrawDirection d){
		return "images/" +keyID + "Key.png";
	}
	
	
	@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "Key");
		elem.setAttribute("keyID", keyID);
		return elem;
	}
	
	public static class KeyFactory implements StorableFactory<Key> {
		@Override
		public Key fromXMLElement(Element elem) {
			return new Key(elem);
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Key){
			Key k = (Key) o;
			return this.keyID.equals(k.keyID) && super.equals(o);
		}
		return false;
	}
}
