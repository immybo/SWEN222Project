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
				tiles[y][x].setDrawID("testFloorA");
			}
		}
		//1x7 corridor in middle
		for(int i = 1; i<8; i++){
			tiles[i][1] = new FloorTile(new Point(1,i));
			tiles[i][1].setDrawID("testFloorB");
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
	 * Will take a character and move that character 1 square forward
	 * 
	 * @param character The character to be moved
	 * @return boolean representing if movement was succesful
	 */
	public boolean moveCharacterForward(Character character){
		Coord origin = character.getCoord();
		Point oriP = origin.getPoint();
		Direction oriD = origin.getFacing();
		Point prospectivePoint = Direction.move(oriP, oriD, 1); // get resulting position if character were to move
		Zone zone = character.getZone();
		if(zone.checkForObstruction(prospectivePoint)){ // check new point for obstacle
			return false;
		}
		character.setCoord(new Coord(oriD, prospectivePoint));
		return true;
		
	}
	
	/**
	 * Will take a character and move that character 1 square backward
	 * 
	 * @param character The character to be moved
	 * @return boolean representing if movement was succesful
	 */
	public boolean moveCharacterBackward(Character character){
		Coord origin = character.getCoord();
		Point oriP = origin.getPoint();
		Direction oriD = origin.getFacing();
		Point prospectivePoint = Direction.move(oriP, oriD, -1); // get resulting position if character were to move
		Zone zone = character.getZone();
		if(zone.checkForObstruction(prospectivePoint)){ // check new point for obstacle
			return false;
		}
		character.setCoord(new Coord(oriD, prospectivePoint));
		return true;
	}
	
	/**
	 * Will take a character and rotate the character to the given direction of rotation
	 * 
	 * @param isClockwise Boolean representing the direction of rotation, True for clockwise
	 * @param character The Character to be rotated
	 */
	public void rotateCharacter(boolean isClockwise, Character character){
		int dirValue = character.getCoord().getFacing().getDirection();
		if(isClockwise == true) dirValue = dirValue + 1;
		else dirValue = dirValue - 1;
		if(dirValue == 5) dirValue = 1;
		if(dirValue == 0) dirValue = 4;
		Direction newDirection = new Direction(dirValue);
		Point point = character.getCoord().getPoint();
		Coord newCoord = new Coord(newDirection, point);
		character.setCoord(newCoord);
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

	public Character getPupo() {
		return Pupo;
	}
	
	public Character getYelo() {
		return Yelo;
	}
	
	public Zone[] getZones(){
		return this.zones;
	}
	
	
	/**
	 * This returns the ZoneDrawInfo for each zone, which contains enough information about the map, entities etc for a renderer to output to a player. 
	 * 
	 * @return
	 */
	public List<ZoneDrawInfo> getDrawInformation(){
		List<ZoneDrawInfo> drawInformation = new ArrayList<ZoneDrawInfo>();
		for(Zone z: zones){
			drawInformation.add(z.getDrawInformation());
		}
		return drawInformation;
		
	}
}
