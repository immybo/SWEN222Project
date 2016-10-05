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
import util.Direction;
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
	private List<Character> characters = new ArrayList<Character>(); // characters not including main characters
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
			System.out.println(getTile(point).getDrawID());
			System.out.println("T");
			return true;
		}
		for(Entity e: getEntities()){
			if(e.getWorldPosition().getPoint().equals(point)){ // check if entity same position
				if(!e.isPassable()){ // check if not passable
					System.out.println("E");
					return true;
				}
			}
		}
		for(Character c: getCharacters()){
			if(c.getCoord().getPoint().equals(point)){
				System.out.println("C");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check a space in front of a player for interactions
	 * @param player
	 * @return Interactions of the space in front
	 */
	public Interaction[] getInteractions(Player p){
		//check if player is in current zone
		if (!p.getZone().equals(this)) return null;	
		Point origin = p.getCoord().getPoint();
		Point check = Direction.move(origin, p.getCoord().getFacing(), 1);
		Entity matchEntity = null;
		
		//check all entities for correct position
		for(Entity e: entities){
			if(e.getWorldPosition().getPoint().equals(check)){
				matchEntity = e;
			}
		}
		
		if(matchEntity == null){
			return null;
		} else {
			return matchEntity.getInteractions();
		}
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
	
	/**
	 * Returns the character at the given point,
	 * if one exists. If more than one character
	 * is at the given point, no guarantee is made
	 * as to which will be returned.
	 * 
	 * @param point The point to query.
	 * @return A character that was at the point, or null if there was none.
	 */
	public Character getCharacter(Point point){
		for(Character c : characters){
			int x = c.getCoord().getPoint().x;
			int y = c.getCoord().getPoint().y;
			if(x == point.x && y == point.y)
				return c;
		}
		return null;
	}

	/**
	 * Returns the entity at the given point,
	 * if one exists. If more than one entity
	 * is at the given point, no guarantee is made
	 * as to which will be returned.
	 * 
	 * @param point The point to query.
	 * @return An entity that was at the point, or null if there was none.
	 */
	public Entity getEntity(Point point){
		for(Entity e : entities){
			int x = e.getWorldPosition().getPoint().x;
			int y = e.getWorldPosition().getPoint().y;
			if(x == point.x && y == point.y)
				return e;
		}
		return null;
	}
	
	/**
	 * Returns the item that was on the floor
	 * at the given point, if one exists. If more
	 * than one item is at the given point, no
	 * guarantee is made as to which will be returned.
	 * 
	 * @param point The point to query.
	 * @return An item that was at the point, or null if there was none.
	 */
	public Item getItem(Point point){
		for(Item i : items){
			if(i.inInventory()) continue;
			
			int x = i.getPosition().x;
			int y = i.getPosition().y;
			if(x == point.x && y == point.y)
				return i;
		}
		return null;
	}
	
	/**
	 * Returns all items that are on the floor at
	 * the given point, if any exist. If none exist,
	 * returns an empty array.
	 * 
	 * @param point The point to query.
	 * @return All items that were at the point.
	 */
	public Item[] getItems(Point point){
		List<Item> it = new LinkedList<Item>();
		for(Item i : items){
			if(i.inInventory()) continue;
			
			int x = i.getPosition().x;
			int y = i.getPosition().y;
			if(x == point.x && y == point.y)
				it.add(i);
		}
		return it.toArray(new Item[0]);
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
	 * Returns an array of all items in this zone.
	 * 
	 * @return All items in this zone.
	 */
	public Item[] getItems(){
		return items.toArray(new Item[0]);
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
