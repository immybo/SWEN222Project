package model;

import java.awt.Point;
import java.util.ArrayList;
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
	}
	
	public static World testWorld(){
		Zone[] newZones = new Zone[1];
		//make just a test zone 3x9 big
		Tile[][] tiles = new Tile[9][3];
		for(int x = 0; x<3; x++){
			for(int y = 0; y<9; y++){
				tiles[y][x] = new WallTile(new Point(x,y));
				tiles[y][x].setDrawImagePath("images/testFloorAIso.png");
			}
		}
		//1x7 corridor in middle
		for(int i = 1; i<8; i++){
			tiles[i][1] = new FloorTile(new Point(1,i));
			tiles[i][1].setDrawImagePath("images/testFloorBIso.png");
		}
		newZones[0] = new Zone("testZone", tiles);
		//key, i have no idea what size does atm. 
		newZones[0].addItem(new Key(new Point(1,2), "testKey"));
		newZones[0].addEntity(new KeyGate(Gate.State.LOCKED, newZones[0], new Coord(new Direction(Direction.NORTH), new Point (1,4)), 1, "testKey"));
		//characters
				Player pupo = new Player(newZones[0], new Coord(new Direction(Direction.NORTH), new Point(1,1)), true);
				Player yelo = new Player(newZones[0], new Coord(new Direction(Direction.SOUTH),new Point(1,7)), false);
				newZones[0].setPupo(pupo);
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
	
	

}
