package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.StorableFactory;
import util.PointD;

public class Key extends Item {
	private String keyID;

	public Key(PointD worldPosition, double size, String keyID) {
		super(worldPosition, size);
		this.keyID = keyID;
	}
	
	protected Key(Element elem){
		super(elem);
	}
	
	public String getKeyID(){
		return this.keyID;
	}
	
	public static class KeyFactory implements StorableFactory<Key> {
		@Override
		public Key fromXMLElement(Element elem) {
			return new Key(elem);
		}
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof Key && super.equals(other);
	}
}
