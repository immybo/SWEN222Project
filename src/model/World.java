package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import datastorage.Storable;
import datastorage.StorableFactory;
import model.Zone.ZoneFactory;
import util.Coord;
import util.Direction;
import util.PointD;

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
		// TODO Auto-generated method stub
		return null;
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
			// TODO Auto-generated method stub
			return null;
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

}
