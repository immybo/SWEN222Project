package model;

import java.awt.Point;

import org.w3c.dom.Element;

import datastorage.StorableFactory;
import view.DrawDirection;

/**
 * A floor tile is a simple tile which doesn't collide with
 * anything else.
 * 
 * @author Robert Campbell
 */
public class FloorTile extends Tile {
	public FloorTile(Point position) {
		super(position);
	}
	
	public boolean collides(){
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof FloorTile)
			return super.equals(o);
		return false;
	}
	
	public static class Factory implements StorableFactory<FloorTile> {
		@Override
		public FloorTile fromXMLElement(Element elem) {
			String className = elem.getAttribute("class");
			Point pos = new Point(Integer.parseInt(elem.getAttribute("xpos")), Integer.parseInt(elem.getAttribute("ypos")));
			
			try {
				Class<?> c = Class.forName(className);
				Object o = c.getConstructor(Point.class).newInstance(pos);
				return (FloorTile)o;
			} catch (Exception e){
				throw new RuntimeException("Trying to parse incorrect XML for a tile factory; tile class invalid.");
			}
		}
	}

	@Override
	public String getDrawImagePath(DrawDirection d) {
		return "images/testFloorAIso.png";
	}
}
