package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;
import datastorage.StorableFactory;
import util.Coord;
import util.Direction;

/**
 * The World contains many Zones and the two Players.
 * This is the top-level class for representing the game;
 * i.e. reproducing this will reproduce (most) of the game.
 *  
 * @author Robert Campbell
 * @author Martin Chau
 */
public class World implements Storable {
	private String name;
	private Zone[] zones;
	private Player Pupo;
	private Player Yelo;

	public World( String name, Zone[] zones, Player pupo, Player yelo){
		this.name = name;
		this.zones = zones;
		this.Pupo = pupo;
		this.Yelo = yelo;
		syncPortals();
	}
	/**
	 * This is used to pair all the portals together that have the same id.
	 * This is useful when creating a world.
	 * Each portal must have another portal with the same id, and only 1 other. 
	 */
	public void syncPortals(){
		// get all portals
		List<Portal> portals = new ArrayList<Portal>();
		for(Zone z: zones){
			if(z == null) continue;
			List<Entity> entities = z.getEntities();
			for(Entity e: entities){
				if(e instanceof Portal){
					Portal portal = (Portal)e;
					portals.add(portal);
				}
			}
		}
		
		while(portals.size() > 0){
			Portal first = portals.get(0);
			Portal second = null;
			for(Portal portal: portals){
				if(portal.getPortalID().equals(first.getPortalID())){
					second = portal;
				}
			}
			first.setPairPortal(second);
			second.setPairPortal(first);
			portals.remove(first);
			portals.remove(second);
		}
	}
	
	public void tick(){
		for(Zone zone : zones){
			zone.tick();
		}
	}
	
	public static World testWorld(){
		Zone[] newZones = new Zone[1];
		//make just a test zone 3x9 big
		Tile[][] tiles = new Tile[9][3];
		for(int x = 0; x<3; x++){
			for(int y = 0; y<9; y++){
				tiles[y][x] = new WallTile(new Point(x,y));
			}
		}
		//1x7 corridor in middle
		for(int i = 1; i<8; i++){
			tiles[i][1] = new FloorTile(new Point(1,i));
		}
		newZones[0] = new Zone("testZone", tiles);
		//key, i have no idea what size does atm.
		newZones[0].addItem(new Key(new Point(1,2), "testKey"));
		newZones[0].addEntity(new KeyGate(Gate.State.LOCKED, newZones[0], new Coord(new Direction(Direction.NORTH), new Point (1,4)), "testKey"));
		newZones[0].addCharacter(new Slime(newZones[0], new Coord(new Direction(Direction.NORTH), new Point(1, 3))));
		//characters
		Player pupo = new Player(newZones[0], new Coord(new Direction(Direction.NORTH), new Point(1,1)), true);
		Player yelo = new Player(newZones[0], new Coord(new Direction(Direction.SOUTH),new Point(1,7)), false);
		
		// add back reference from zone to character
		newZones[0].addCharacter(pupo);
		newZones[0].addCharacter(yelo);
		
		return new World("test",newZones, pupo, yelo);
	}

	/**
	 * Execute an interaction as a specific player
	 * @param interaction
	 * @param player
	 */
	public void interact(Interaction interaction, Player player) {
		interaction.execute(player);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("World");
		elem.setAttribute("name", name);
		int i = 0;
		for(Zone z : zones){
			i++;
			elem.appendChild(z.toXMLElement(doc));
		}
		elem.appendChild(Pupo.toXMLElement(doc));
		elem.appendChild(Yelo.toXMLElement(doc));
		return elem;
	}

	public Player getPupo() {
		return Pupo;
	}

	public Player getYelo() {
		return Yelo;
	}

	public Zone[] getZones(){
		return this.zones;
	}

	public static class Factory implements StorableFactory<World> {

		@Override
		public World fromXMLElement(Element elem) {
			String name = elem.getAttribute("name");
			Zone[] zones = new Zone[elem.getChildNodes().getLength()-2];
			int i = 0;
			for(; i < elem.getChildNodes().getLength() - 2 ; i++ )
				zones[i] = new Zone.ZoneFactory().fromXMLElement((Element) elem.getChildNodes().item(i));
			Player Pupo = new Player.Factory(zones).fromXMLElement((Element) elem.getChildNodes().item(i));
			Player Yelo = new Player.Factory(zones).fromXMLElement((Element) elem.getChildNodes().item(i+1));
			World world = new World(name, zones, Pupo, Yelo);
			Pupo.getZone().addCharacter(Pupo);
			Yelo.getZone().addCharacter(Yelo);
			return world;
		}

	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof World){
			World w = (World) o;
			if(this.name.equals(w.name) && Arrays.equals(this.zones, w.zones)
					&& this.Pupo.equals(w.Pupo) && this.Yelo.equals(w.Yelo))
				return true;
		}
		return false;
	}
	/**
	 * Checks if pupo is next to yelo, therefore winning game
	 * @return Boolean that represents if the game has been won
	 */
	public boolean checkForGameWin(){
		if(Pupo.getZone().equals(Yelo.getZone())){
			Point p = Pupo.getCoord().getPoint();
			Point y = Yelo.getCoord().getPoint();
			Point[] points = new Point[4];
			points[0] = new Point(p.x-1, p.y);
			points[1] = new Point(p.x+1, p.y);
			points[2] = new Point(p.x, p.y-1);
			points[3] = new Point(p.x, p.y+1);
			for(Point i: points){
				if(y.equals(i)) return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Constructs the first level, information for which can be found in the excel sheet.
	 * 
	 * @return First level for demonstration
	 */
	public static World firstLevel(){
		Zone[] zones = new Zone[5];
		
		// ZONE 0 / Memory and Decisions.
		Tile[][] tiles = new Tile[12][13];
		int y;
		int x;
		int toAdd;
		//y=0
		y=0;x=0;
		toAdd=13;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=1
		y=1;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addFloor(tiles,toAdd,x,y);x=x+toAdd;	
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;	
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=2
		y=2;x=0;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=6;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=3
		y=3;x=0;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=4
		y=4;x=0;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=5
		y=5;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=6
		y=6;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=6;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=7
		y=7;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=8
		y=8;x=0;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=9
		y=9;x=0;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=10
		y=10;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=11
		y=11;x=0;
		toAdd=13;addWall(tiles,toAdd,x,y);x=x+toAdd;
		zones[0] = new Zone("Memory and Decisions", tiles);
		//items and entities
		zones[0].addItem(new Coin(new Point(1,5)));
		zones[0].addItem(new Coin(new Point(2,5)));
		zones[0].addItem(new Coin(new Point(1,6)));
		zones[0].addItem(new Coin(new Point(2,6)));
		zones[0].addItem(new Coin(new Point(1,7)));
		zones[0].addItem(new Coin(new Point(2,7)));
		zones[0].addItem(new Key(new Point(3,3), "blue"));
		zones[0].addItem(new Key(new Point(8,7), "green"));
		zones[0].addItem(new Key(new Point(10,4), "orange"));
		Furniture shop1 = new Furniture(zones[0],new Coord(new Direction(Direction.SOUTH),new Point(4,1)),null,"Looks like a shop");
		Key blueKey = new Key(null, "blue");
		blueKey.onPickUp();
		shop1.addInteraction(new BuyItem(shop1, blueKey, "blue key", 7));
		shop1.setDrawImagePath("images/shopKeeper");
		zones[0].addEntity(shop1);
		zones[0].addEntity(new KeyGate(Gate.State.LOCKED, zones[0], new Coord(new Direction(Direction.EAST),new Point(3,6)), "blue"));
		zones[0].addEntity(new KeyGate(Gate.State.LOCKED, zones[0], new Coord(new Direction(Direction.EAST),new Point(5,6)), "blue"));
		zones[0].addEntity(new KeyGate(Gate.State.LOCKED, zones[0], new Coord(new Direction(Direction.NORTH),new Point(4,9)), "red"));
		zones[0].addEntity(new KeyGate(Gate.State.LOCKED, zones[0], new Coord(new Direction(Direction.EAST),new Point(8,2)), "orange"));
		zones[0].addEntity(new KeyGate(Gate.State.LOCKED, zones[0], new Coord(new Direction(Direction.EAST),new Point(9,2)), "green"));
		zones[0].addEntity(new Portal(zones[0], new Coord(new Direction(Direction.NORTH),new Point(6,10)), "1"));
		zones[0].addEntity(new Portal(zones[0], new Coord(new Direction(Direction.NORTH),new Point(1,10)), "2"));
		zones[0].addEntity(new Portal(zones[0], new Coord(new Direction(Direction.NORTH),new Point(10,6)), "4"));
		zones[0].addEntity(new Portal(zones[0], new Coord(new Direction(Direction.NORTH),new Point(11,2)), "5"));
		zones[0].addEntity(new Portal(zones[0], new Coord(new Direction(Direction.NORTH),new Point(9,10)), "6"));
		Furniture totem1 = new Furniture(zones[0],new Coord(new Direction(Direction.NORTH),new Point(6,1)),null,"Some sort of totem, nothing inside");
		totem1.setDrawImagePath("images/totem");
		Furniture totem2 = new Furniture(zones[0],new Coord(new Direction(Direction.NORTH),new Point(8,3)),null,"Some sort of totem, nothing inside");
		totem2.setDrawImagePath("images/totem");
		Furniture totemWithItem = new Furniture(zones[0],new Coord(new Direction(Direction.NORTH),new Point(8,1)),null,"Shouldnt be here");
		totemWithItem.removeInteraction(totemWithItem.getInteractions()[0]);
		Key redKey = new Key(null, "red");
		redKey.onPickUp();
		totemWithItem.addInteraction(new InspectWithItem(totemWithItem, redKey, "Hey, theres a red key in here", "Some sort of totem, nothing inside"));
		totemWithItem.setDrawImagePath("images/totem");
		zones[0].addEntity(totem1);
		zones[0].addEntity(totem2);
		zones[0].addEntity(totemWithItem);
				
		//ZONE 1 / Start Over
		tiles = new Tile[7][10];
		//y=0
		y=0;x=0;
		toAdd=10;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=1
		y=1;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=2
		y=2;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=3
		y=3;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=8;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=4
		y=4;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=5
		y=5;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=6
		y=6;x=0;
		toAdd=10;addWall(tiles,toAdd,x,y);x=x+toAdd;		
		zones[1] = new Zone("Start Over",tiles);
		zones[1].addItem(new Coin(new Point(1,1)));
		zones[1].addItem(new Coin(new Point(5,5)));
		zones[1].addItem(new Key(new Point(1,5), "blue"));
		Furniture shop2 = new Furniture(zones[1],new Coord(new Direction(Direction.SOUTH),new Point(5,1)),null,"Looks like a shop");
		Key yellowKey = new Key(null, "yellow");
		yellowKey.onPickUp();
		shop2.addInteraction(new BuyItem(shop2, yellowKey, "yellow key", 8));
		shop2.setDrawImagePath("images/shopKeeper");
		zones[1].addEntity(shop2);
		zones[1].addEntity(new KeyGate(Gate.State.LOCKED, zones[1], new Coord(new Direction(Direction.EAST),new Point(6,3)), "yellow"));
		zones[1].addEntity(new Portal(zones[1], new Coord(new Direction(Direction.NORTH),new Point(3,3)), "2"));
		zones[1].addEntity(new Portal(zones[1], new Coord(new Direction(Direction.NORTH),new Point(8,3)), "4"));
		
		//ZONE 2 / Bob Campbell
		tiles = new Tile[7][12];
		//y=0
		y=0;x=0;
		toAdd=12;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=1
		y=1;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=10;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=2
		y=2;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=3
		y=3;x=0;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=4
		y=4;x=0;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=5
		y=5;x=0;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=6
		y=6;x=0;
		toAdd=12;addWall(tiles,toAdd,x,y);x=x+toAdd;
		zones[2] = new Zone("Bob Campbell", tiles);
		Furniture sign1 = new Furniture(zones[2],new Coord(new Direction(Direction.NORTH),new Point(3,1)),null,"Most common number 1-1000 inclusive?");
		sign1.setDrawImagePath("images/sign");
		zones[2].addEntity(sign1);
		Furniture sign2 = new Furniture(zones[2],new Coord(new Direction(Direction.EAST),new Point(7,2)),null,"1 or 2");
		sign2.setDrawImagePath("images/sign");
		zones[2].addEntity(sign2);
		Furniture sign3 = new Furniture(zones[2],new Coord(new Direction(Direction.EAST),new Point(7,4)),null,"2 or 3");
		sign3.setDrawImagePath("images/sign");
		zones[2].addEntity(sign3);
		zones[2].addItem(new Key(new Point(4,1), "purple"));
		zones[2].addEntity(new KeyGate(Gate.State.LOCKED, zones[2], new Coord(new Direction(Direction.EAST),new Point(8,1)), "purple"));
		zones[2].addEntity(new KeyGate(Gate.State.LOCKED, zones[2], new Coord(new Direction(Direction.EAST),new Point(8,3)), "purple"));
		zones[2].addEntity(new KeyGate(Gate.State.LOCKED, zones[2], new Coord(new Direction(Direction.EAST),new Point(8,5)), "purple"));
		zones[2].addEntity(new Portal(zones[2], new Coord(new Direction(Direction.NORTH),new Point(1,1)), "1"));
		zones[2].addEntity(new Portal(zones[2], new Coord(new Direction(Direction.NORTH),new Point(10,1)), "3"));
		Furniture fakePortal1 = new Furniture(zones[2],new Coord(new Direction(Direction.NORTH),new Point(10,3)),null,"Dammit this portal's a fake");
		fakePortal1.setDrawImagePath("images/portal");
		zones[2].addEntity(fakePortal1);
		Furniture fakePortal2 = new Furniture(zones[2],new Coord(new Direction(Direction.NORTH),new Point(10,5)),null,"Dammit this portal's a fake");
		fakePortal2.setDrawImagePath("images/portal");
		zones[2].addEntity(fakePortal2);
		
		//ZONE 3 / Familiarity
		tiles = new Tile[16][14];
		//y=0
		y=0;x=0;
		toAdd=14;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=1
		y=1;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=2
		y=2;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=4;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=3
		y=3;x=0;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=4
		y=4;x=0;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=5
		y=5;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=6
		y=6;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=12;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=7
		y=7;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=8
		y=8;x=0;
		toAdd=4;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=9
		y=9;x=0;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=10
		y=10;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=11
		y=11;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=6;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=12
		y=12;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=13
		y=13;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=14
		y=14;x=0;
		toAdd=5;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=2;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=15
		y=15;x=0;
		toAdd=14;addWall(tiles,toAdd,x,y);x=x+toAdd;
		zones[3] = new Zone("Familiarity", tiles);
		Furniture totem3 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(7,1)),null,"Some sort of totem, nothing inside");
		totem3.setDrawImagePath("images/totem");
		Furniture totem4 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(8,2)),null,"Some sort of totem, nothing inside");
		totem4.setDrawImagePath("images/totem");
		Furniture totem5 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(5,2)),null,"Some sort of totem, nothing inside");
		totem5.setDrawImagePath("images/totem");
		Furniture totem6 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(1,5)),null,"Some sort of totem, nothing inside");
		totem6.setDrawImagePath("images/totem");
		Furniture totem7 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(1,7)),null,"Some sort of totem, nothing inside");
		totem7.setDrawImagePath("images/totem");
		Furniture totem8 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(2,7)),null,"Some sort of totem, nothing inside");
		totem8.setDrawImagePath("images/totem");
		Furniture totem9 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(10,5)),null,"Some sort of totem, nothing inside");
		totem9.setDrawImagePath("images/totem");
		Furniture totem10 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(12,5)),null,"Some sort of totem, nothing inside");
		totem10.setDrawImagePath("images/totem");
		Furniture totem11 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(12,7)),null,"Some sort of totem, nothing inside");
		totem11.setDrawImagePath("images/totem");
		Furniture totem12 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(5,10)),null,"Some sort of totem, nothing inside");
		totem12.setDrawImagePath("images/totem");
		Furniture totem13 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(5,12)),null,"Some sort of totem, nothing inside");
		totem13.setDrawImagePath("images/totem");
		Furniture totem14 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(5,14)),null,"Some sort of totem, nothing inside");
		totem14.setDrawImagePath("images/totem");
		Furniture fakePortal3 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(6,1)),null,"Dammit this portal's a fake");
		fakePortal3.setDrawImagePath("images/portal");
		Furniture fakePortal4 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(2,5)),null,"Dammit this portal's a fake");
		fakePortal4.setDrawImagePath("images/portal");
		Furniture fakePortal5 = new Furniture(zones[3],new Coord(new Direction(Direction.NORTH),new Point(7,10)),null,"Dammit this portal's a fake");
		fakePortal5.setDrawImagePath("images/portal");
		zones[3].addEntity(totem3);
		zones[3].addEntity(totem4);
		zones[3].addEntity(totem5);
		zones[3].addEntity(totem6);
		zones[3].addEntity(totem7);
		zones[3].addEntity(totem8);
		zones[3].addEntity(totem9);
		zones[3].addEntity(totem10);
		zones[3].addEntity(totem11);
		zones[3].addEntity(totem12);
		zones[3].addEntity(totem13);
		zones[3].addEntity(totem14);
		zones[3].addEntity(fakePortal3);
		zones[3].addEntity(fakePortal4);
		zones[3].addEntity(fakePortal5);
		zones[3].addItem(new Key(new Point(8,8), "purple"));
		zones[3].addEntity(new KeyGate(Gate.State.LOCKED, zones[3], new Coord(new Direction(Direction.NORTH),new Point(6,3)), "purple"));
		zones[3].addEntity(new KeyGate(Gate.State.LOCKED, zones[3], new Coord(new Direction(Direction.EAST),new Point(3,6)), "purple"));
		zones[3].addEntity(new KeyGate(Gate.State.LOCKED, zones[3], new Coord(new Direction(Direction.EAST),new Point(9,6)), "purple"));
		zones[3].addEntity(new KeyGate(Gate.State.LOCKED, zones[3], new Coord(new Direction(Direction.NORTH),new Point(6,9)), "purple"));
		zones[3].addEntity(new Portal(zones[3], new Coord(new Direction(Direction.NORTH),new Point(6,6)), "3"));
		zones[3].addEntity(new Portal(zones[3], new Coord(new Direction(Direction.NORTH),new Point(10,7)), "5"));
		
		//ZONE 4 / FightClub
		tiles = new Tile[8][7];
		//y=0
		y=0;x=0;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=1
		y=1;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=2
		y=2;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=3
		y=3;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=4
		y=4;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=5
		y=5;x=0;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=5;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=6
		y=6;x=0;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=1;addFloor(tiles,toAdd,x,y);x=x+toAdd;
		toAdd=3;addWall(tiles,toAdd,x,y);x=x+toAdd;
		//y=7
		y=7;x=0;
		toAdd=7;addWall(tiles,toAdd,x,y);x=x+toAdd;
		zones[4] = new Zone("Fight Club", tiles);
		zones[4].addEntity(new Portal(zones[4], new Coord(new Direction(Direction.NORTH),new Point(3,6)), "6"));
		//Slime 
		Slime slime1 = new Slime(zones[4], new Coord(new Direction(Direction.SOUTH),new Point(3,3)));
		zones[4].addCharacter(slime1);
		//Sword
		zones[4].addItem(new Sword(new Point(5,5)));
		
		//characters
		Player pupo = new Player(zones[0], new Coord(new Direction(Direction.EAST), new Point(1,1)), true);
		Player yelo = new Player(zones[0], new Coord(new Direction(Direction.SOUTH),new Point(8,5)), false);
		zones[0].addCharacter(pupo);
		zones[0].addCharacter(yelo);
		return new World("Demonstration Level", zones, pupo, yelo);
	}
	
	/**
	 * Helper method to add walls to an array
	 * @param tiles Array of tiles to add to
	 * @param amount Amount to add across x
	 * @param startX X value to start at
	 * @param startY Y value to start at
	 */
	public static void addWall(Tile[][] tiles, int amount, int startX, int startY){
		for(int i = 0; i<amount; i++){
			tiles[startY][startX+i] = new WallTile(new Point(startX + i,startY));
		}
	}
	
	/**
	 * Helper method to add floors to an array
	 * @param tiles Array of tiles to add to
	 * @param amount Amount to add across x
	 * @param startX X value to start at
	 * @param startY Y value to start at
	 */
	public static void addFloor(Tile[][] tiles, int amount, int startX, int startY){
		for(int i = 0; i<amount; i++){
			tiles[startY][startX+i] = new FloorTile(new Point(startX + i,startY));
		}
	}
	public Zone getZoneByID(long id) {
		for (Zone z : getZones()) {
			if (z.getID() == id)
				return z;
		}
		return null;
	}

}
