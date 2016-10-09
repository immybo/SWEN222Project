package model;

import java.awt.*;
import java.io.Serializable;

import org.w3c.dom.*;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.PointD;
import view.DrawDirection;
import view.Drawable;

/**
 * A tile is a background object.
 * Tiles generally have some texture and may
 * or may not collide with characters/items.
 * 
 * @author Robert Campbell
 */
public abstract class Tile implements Storable,Serializable, Drawable {
	private Point position;
	
	public Tile(Point position){
		this.position = position;
	}
	
	public Point getPosition(){
		return position;
	}
	
	/**
	 * Tiles can either collide with collidable objects,
	 * or not collide with them.
	 * 
	 * @return Whether or not this tile collides with objects.
	 */
	public abstract boolean collides();
	
	/**
	 * 
	 */
	
	
	public Element toXMLElement(Document doc, String type){
		Element elem = doc.createElement("tile"+position.x+":"+position.y);
		elem.setAttribute("xpos", position.x+"");
		elem.setAttribute("ypos", position.y+"");
		elem.setAttribute("class", this.getClass().getCanonicalName());
		return elem;
	}
	
	public static class TileFactory implements StorableFactory<Tile> {
		@Override
		public Tile fromXMLElement(Element elem) {
			String className = elem.getAttribute("class");
			Point pos = new Point(Integer.parseInt(elem.getAttribute("xpos")), Integer.parseInt(elem.getAttribute("ypos")));
			
			try {
				Class<?> c = Class.forName(className);
				Object o = c.getConstructor(Point.class).newInstance(pos);
				return (Tile)o;
			} catch (Exception e){
				throw new RuntimeException("Trying to parse incorrect XML for a tile factory; tile class invalid.");
			}
		}
	}
	
	@Override
	public boolean equals(Object other){
		return other.getClass().equals(this.getClass())
		    && ((Tile)other).getPosition().equals(getPosition());
	}
	
	@Override
	public int hashCode(){
		return position.hashCode();
	}
	
	private String drawImagePath;
	
	@Override
	public String getDrawImagePath(DrawDirection d) {
		return this.drawImagePath;
	}

	@Override
	public double getDepthOffset() {
		return 0;
	}

	public PointD getDrawPosition() {
		return new PointD(position.getX(),position.getY());
	}

	/*
	public void setDrawImagePath(String drawImagePath) {
		this.drawImagePath = drawImagePath;
		
	}
	*/
	@Override
	public int getYOffset() {
		return 35;
	}
}
