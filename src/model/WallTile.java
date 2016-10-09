package model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.StorableFactory;
import view.DrawDirection;

/**
 * A wall tile is a tile that acts as a boundary which will collide with any other object
 * 
 * @author Martin Chau
 */
public class WallTile extends Tile {

	public WallTile(Point position) {
		super(position);
	}
	
	public boolean collides(){
		return true;
	}
	
	@Override
	public String getDrawImagePath(DrawDirection d) {
		return "images/wallTile.png";
	}
	
	public static class Factory implements StorableFactory<WallTile> {
		@Override
		public WallTile fromXMLElement(Element elem) {
			String className = elem.getAttribute("class");
			Point pos = new Point(Integer.parseInt(elem.getAttribute("xpos")), Integer.parseInt(elem.getAttribute("ypos")));
			
			try {
				Class<?> c = Class.forName(className);
				Object o = c.getConstructor(Point.class).newInstance(pos);
				return (WallTile)o;
			} catch (Exception e){
				throw new RuntimeException("Trying to parse incorrect XML for a tile factory; tile class invalid.");
			}
		}
	}

	@Override
	public Element toXMLElement(Document doc) {
		// TODO Auto-generated method stub
		return super.toXMLElement(doc, "FloorTile");
	}
}
