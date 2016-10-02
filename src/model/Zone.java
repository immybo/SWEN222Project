package model;

import java.awt.Point;
import java.io.Serializable;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import datastorage.Storable;
import datastorage.StorableFactory;
import model.Tile.TileFactory;
import util.Coord;
import util.PointD;

/**
 * A zone is a discrete section of the world.
 * Generally, things in zones will not be able to
 * 'see' or affect things in other zones.
 * 
 * @author Robert Campbell
 */
public class Zone implements Storable, Serializable {
	private static long nextID = 0;
	private long id;
	
	private String name;
	private Tile[][] tiles;
	private List<Item> items;
	private List<Entity> entities;
	private List<Character> characters; // characters not including main characters
	private Character Pupo;
	private Character Yelo;
	
	/**
	 * Zones should usually only be constructed from
	 * appropriate XML objects.
	 * 
	 * @param name The name of the new zone.
	 * @param tiles The tiles in the new zone.
	 */
	public Zone(String name, Tile[][] tiles){
		this.name = name;
		this.tiles = tiles;
		
		items = new ArrayList<Item>();
		entities = new ArrayList<Entity>();
		
		this.id = nextID++;
	}
	
	/**
	 * Creates a zone using a specific ID.
	 * This is done internally when a zone is being 
	 * recreated from an XML document.
	 */
	private Zone(String name, Tile[][] tiles, long id){
		this.name = name;
		this.tiles = tiles;
		
		items = new ArrayList<Item>();
		entities = new ArrayList<Entity>();
		
		this.id = id;
	}
	
	/**
	 * Checks if a point in this zone is obstructed by characters, objects or tiles themselves
	 * @param zone The zone to check 
	 * @param point The point inside the zone to check
	 * @return True if obstacle exists, false otherwise
	 */
	public boolean checkForObstruction(Point point){
		if(getTile(point).collides()){
			return true;
		}
		for(Entity e: getEntities()){
			if(e.getWorldPosition().getPoint().equals(point)){ // check if entity same position
				if(!e.isPassable()){ // check if not passable
					return true;
				}
			}
		}
		for(Character c: getCharacters()){
			if(c.getCoord().getPoint().equals(point)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return The unique ID of this zone.
	 */
	public long getID(){
		return id;
	}
	
	/**
	 * @return A list of all entities contained within this zone.
	 */
	public List<Entity> getEntities(){
		return this.entities;
	}
	
	/**
	 * @return A list of all characters contained within this zone.
	 */
	public List<Character> getCharacters(){
		return this.characters;
	}
	
	/**
	 * Returns the tile at the given point.
	 * 
	 * @param point The point to get the tile at.
	 * @return The tile that is at the given point.
	 * @throws IllegalArgumentException If the point is out of the bounds of this zone.
	 */
	public Tile getTile(Point point){
		if(point.x > tiles[0].length || point.x < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid X: " + point.getX());
		if(point.y > tiles.length || point.y < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid Y: " + point.getY());
		
		return tiles[point.y][point.x];
	}

	
	@Override
	/**
	 * Returns the name of this zone.
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * Adds an item to be contained in (and therefore drawn on)
	 * this zone.
	 * 
	 * @param i The item to be added.
	 */
	public void addItem(Item i){
		items.add(i);
	}
	
	/**
	 * Removes an item to no longer be contained in (and therefore
	 * not drawn on) this zone.
	 * 
	 * @param i The item to be removed.
	 */
	public void removeItem(Item i){
		items.remove(i);
	}
	
	/**
	 * Adds an entity to be contained in (and therefore drawn on)
	 * this zone.
	 * 
	 * @param e The entity to be added.
	 */
	public void addEntity(Entity e){
		entities.add(e);
	}
	
	/**
	 * Removes an entity to no longer be contained in (and therefore
	 * not drawn on) this zone.
	 * 
	 * @param e The entity to be removed.
	 */
	public void removeEntity(Entity e){
		entities.remove(e);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("zone");
		elem.setAttribute("ID", id+"");
		elem.setAttribute("name", name);
		elem.setAttribute("width", tiles[0].length+"");
		elem.setAttribute("height", tiles.length+"");
		for(int x = 0; x < tiles[0].length; x++){
			for(int y = 0; y < tiles.length; y++){
				elem.appendChild(tiles[x][y].toXMLElement(doc));
			}
		}
		return elem;
	}
	
	public static class ZoneFactory implements StorableFactory<Zone> {
		@Override
		public Zone fromXMLElement(Element elem) {
			long id = Long.parseLong(elem.getAttribute("ID"));
			String name = elem.getAttribute("name");
			int width = Integer.parseInt(elem.getAttribute("width"));
			int height = Integer.parseInt(elem.getAttribute("height"));
			Tile[][] tiles = new Tile[height][width];
			TileFactory factory = new TileFactory();
			
			NodeList children = elem.getChildNodes();
			for(int i = 0; i < children.getLength(); i++){
				String positionString = children.item(i).getNodeName().substring(4);
				int x = Integer.parseInt(positionString.split(":")[0]);
				int y = Integer.parseInt(positionString.split(":")[1]);
				tiles[y][x] = factory.fromXMLElement((Element)children.item(i));
			}
			
			// Make sure IDs don't overlap
			if(id >= nextID)
				nextID = id + 1;
			return new Zone(name, tiles, id);
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Zone){
			Zone zone = (Zone)other;
			
			// If the basic attributes are the same, they can't be the same
			if(!zone.name.equals(name))
				return false;
			if(zone.tiles[0].length != tiles[0].length || zone.tiles.length != tiles.length)
				return false;
			
			// Otherwise just make sure every single tile is equal in both
			for(int x = 0; x < tiles[0].length; x++){
				for(int y = 0; y < tiles.length; y++){
					if(!zone.tiles[y][x].equals(tiles[y][x])){
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}

	public ZoneDrawInfo getDrawInformation() {
		//compile drawIDs of all tiles 
		String[][] tileInfo = new String[tiles.length][tiles[0].length];
		for(int x = 0; x < tiles[0].length; x++){
			for(int y = 0; y < tiles.length; y++){
				tileInfo[y][x] = tiles[y][x].getDrawID();
			}
		}
		HashMap<Coord, String> entityInfo = new HashMap<Coord, String>();
		for(Entity e: entities){
			entityInfo.put(e.getWorldPosition(), e.getDrawID());
		}
		HashMap<Point, String> itemInfo = new HashMap<Point, String>();
		for(Item i: items){
			itemInfo.put(i.getPosition(), i.getDrawID());
		}
		return new ZoneDrawInfo(tileInfo, entityInfo, itemInfo);
	}

	public Character getPupo() {
		return Pupo;
	}

	public void setPupo(Character pupo) {
		Pupo = pupo;
	}

	public Character getYelo() {
		return Yelo;
	}

	public void setYelo(Character yelo) {
		Yelo = yelo;
	}
}
