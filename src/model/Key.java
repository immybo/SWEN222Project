package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.StorableFactory;
import util.PointD;

public class Key extends Item {
	public Key(Inventory inventory, double size) {
		super(inventory, size);
	}

	public Key(Zone zone, PointD worldPosition, double size) {
		super(zone, worldPosition, size);
	}
}
