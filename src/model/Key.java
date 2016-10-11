package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import view.DrawDirection;

/**
 * A Key is an Item which can be used to open specific KeyGates.
 * 
 * @author Martin Chau
 */
public class Key extends Item implements Storable{
	private static final long serialVersionUID = -6877497241147657342L;
	private String keyID;
	/**
	 * Constructor for the key
	 * @param worldPosition Position of key
	 * @param keyID Unique code of the key
	 */
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
	
	public String toString() {
		return keyID+" Key";
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
