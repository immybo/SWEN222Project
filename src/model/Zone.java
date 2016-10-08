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
	private List<Character> characters = new ArrayList<Character>();

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
	
	public int getWidth(){
		return tiles[0].length;
	}
	
	public int getHeight(){
		return tiles.length;
	}
	
	/**
	 * Calculates the shortest path from the given start point to the
	 * given end point on this zone. This path must not collide
	 * with any collidable objects. Note that the path is only
	 * valid right after calculated, and so must be recalculated
	 * every time a step along the path is made in order to remain
	 * current.
	 * 
	 * A path may not necessarily exist between two points. If no
	 * such path exists, null is returned. The collision status of
	 * the start point is not considered, but the collision status 
	 * of the end point is.
	 * 
	 * If a path is returned, it will be returned as a series of directions
	 * in which to move one square, until the end point is reached.
	 * 
	 * @param start The point to start at.
	 * @param end The point to end at.
	 * @return The series of directions to get from the start to the end, or null if none exists.
	 */
	public Direction[] getPath(Point start, Point end){
		// A* used here; simple and effective (and, helpfully, learned in COMP261)
		Set<Point> visited = new HashSet<Point>();
		Queue<NodeData> fringe = new PriorityQueue<NodeData>();
		
		NodeData first = new NodeData(0, Coord.getDistance(start, end), null, start);
		fringe.add(first);
		
		while(!fringe.isEmpty()){
			NodeData current = fringe.poll();
			
			if(current.point.equals(end))
				return constructPath(current);
			
			visited.add(current.point);
			
			List<Point> neighbours = new ArrayList<Point>();
			neighbours.add(new Point(current.point.x + 1, current.point.y));
			neighbours.add(new Point(current.point.x - 1, current.point.y));
			neighbours.add(new Point(current.point.x, current.point.y + 1));
			neighbours.add(new Point(current.point.x, current.point.y - 1));
			for(Point p : neighbours){
				if(visited.contains(p))
					continue; // already visited, can't be a better path
				
				try{
					if(!checkForObstruction(p)){ // can we move onto the point?
						NodeData inFringe = null;
						for(NodeData n : fringe){
							if(n.point.equals(p)){ inFringe = n; break; }
						}
						
						if(inFringe == null){ // auto add it if we haven't seen it yet
							fringe.add(new NodeData(current.costSoFar + 1,
													Coord.getDistance(p, end),
													current, p));
						}
						else if(current.costSoFar + 1 >= inFringe.costSoFar){ // This path to it is longer
							continue;
						}
						else { // This path to it is shorter, override it
							fringe.remove(inFringe);
							inFringe.costSoFar = current.costSoFar + 1;
							inFringe.previous = current;
							fringe.add(inFringe);
						}
					}
				}
				catch(IllegalArgumentException e){ // out of bounds; ignore this neighbour
					continue;
				}
			}
		}
		
		return null; // We can't find a path at all!
	}
	
	private Direction[] constructPath(NodeData node){
		List<NodeData> list = new LinkedList<NodeData>();
		while(node != null){
			list.add(0, node);
			node = node.previous;
		}
		
		List<Direction> directions = new LinkedList<Direction>();
		for(int i = 1; i < list.size(); i++){
			directions.add(Direction.directionFrom(list.get(i-1).point, list.get(i).point));
		}
		
		return directions.toArray(new Direction[0]);
	}
	
	/**
	 * A collection of data used for A* search.
	 * 
	 * @author Robert Campbell
	 */
	private class NodeData implements Comparable<NodeData> {
		public int costSoFar;
		public double heuristicCost;
		public Point point;
		public NodeData previous;
		
		public NodeData(int costSoFar, double heuristicCost, NodeData previous, Point point){
			this.costSoFar = costSoFar; this.heuristicCost = heuristicCost; this.previous = previous; this.point = point;
		}
		
		@Override
		public int compareTo(NodeData other) {
			double comparison = (costSoFar+heuristicCost) - (other.costSoFar+other.heuristicCost);
			return comparison < 0 ? -1 : comparison > 0 ? 1 : 0;
		}
	}

	/**
	 * Checks if a point in this zone is obstructed by characters, objects or tiles themselves
	 * @param point The point inside the zone to check
	 * @return True if obstacle exists, false otherwise
	 */
	public boolean checkForObstruction(Point point){
		try{
			if(getTile(point).collides()){
				return true;
			}
		}
		catch(IllegalArgumentException e){
			return true; // Out of bounds
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
	 * Checks a specific point in this zone for an interactable.
	 * 
	 * @param point The point to check.
	 * @return An interactable that was found at the point, or null if none was found.
	 */
	public Interactable getInteractable(Point point){
		Entity matchEntity = null;

		//check all entities for correct position
		for(Entity e: entities){
			if(e.getWorldPosition().getPoint().equals(point)){
				matchEntity = e;
			}
		}

		return matchEntity == null ? null : matchEntity;
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
		// Remove dead enemies
		for(Character c : characters){
			if(c instanceof Enemy){
				if(((Enemy)c).isDead())
					characters.remove(c);
			}
		}
		
		return this.characters;
	}
	

	/**
	 * Adds a character to this zone.
	 *
	 * @param c The character to add.
	 */
	public void addCharacter(Character c){
		characters.add(c);
	}

	/**
	 * Removes a character from this zone,
	 * if it exists in this zone. If it does
	 * not, does nothing.
	 *
	 * @param c The character to remove.
	 */
	public void removeCharacter(Character c){
		characters.remove(c);
	}

	/**
	 * Returns the tile at the given point.
	 *
	 * @param point The point to get the tile at.
	 * @return The tile that is at the given point.
	 * @throws IllegalArgumentException If the point is out of the bounds of this zone.
	 */
	public Tile getTile(Point point){
		if(point.x >= tiles[0].length || point.x < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid X: " + point.getX());
		if(point.y >= tiles.length || point.y < 0)
			throw new IllegalArgumentException("Trying to get a tile at an invalid Y: " + point.getY());

		return tiles[point.y][point.x];
	}
	
	/**
	 * Sets a tile at a given point. Note that this
	 * generally shouldn't be done during gameplay,
	 * as it may break things if something is trying
	 * to move there!
	 * 
	 * @param point The point to set the tile at.
	 * @param tile The tile to set the point to.
	 */
	public void setTile(Point point, Tile tile){
		if(point.x >= tiles[0].length || point.x < 0)
			throw new IllegalArgumentException("Trying to set a tile at an invalid X: " + point.getX());
		if(point.y >= tiles.length || point.y < 0)
			throw new IllegalArgumentException("Trying to set a tile at an invalid Y: " + point.getY());
		
		tiles[point.y][point.x] = tile;
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
	 * Returns an enemy at the given point, if
	 * one exists. If more than one enemy exists at 
	 * the given point, no guarantee is made as
	 * to which will be returned.
	 * 
	 * @param point The point to query.
	 * @return An enemy that was at the point, or null if there was none.
	 */
	public Enemy getEnemy(Point point){
		for(Character c : getCharacters()){
			if(c instanceof Enemy &&
					c.getCoord().getPoint().x == point.x &&
					c.getCoord().getPoint().y == point.y){
				return (Enemy)c;
			}
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

	public List<Tile> getTiles() {
		List<Tile> tileList = new ArrayList<>();
		for (Tile[] ts : tiles) {
			for (Tile t : ts){
				tileList.add(t);
			}
		}
		return tileList;
	}

	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
}
