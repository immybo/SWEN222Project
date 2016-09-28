package model;

import java.awt.Point;
import java.util.ArrayList;
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
	private Character Pupo;
	private Character Yelo;
	
	public World( String name, Zone[] zones, Character pupo, Character yelo){
		this.zones = zones;
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
		for(int i = 7; i<7; i++){
			tiles[1][i] = new FloorTile(new Point(i,1));
		}
		//key, i have no idea what size does atm. 
		newZones[1].addItem(new Key(new PointD(1,2), 0.5, "testKey"));
		newZones[1].addEntity(new KeyGate(Gate.State.LOCKED, newZones[1], new Coord(new Direction(Direction.NORTH), new Point (1,4)), 1, "testKey"));
		//characters
				Character pupo = new PlayableCharacter(newZones[1], new Coord(new Direction(Direction.NORTH), new Point(1,0)), true);
				Character yelo = new PlayableCharacter(newZones[1], new Coord(new Direction(Direction.SOUTH),new Point(1,8)), false);
				
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
		if(checkForObstruction(zone, prospectivePoint)){ // check new point for obstacle
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
		if(checkForObstruction(zone, prospectivePoint)){ // check new point for obstacle
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
	 * Checks if a point in a particular zone is obstructed by characters, objects or tiles themselves
	 * @param zone The zone to check 
	 * @param point The point inside the zone to check
	 * @return True if obstacle exists, false otherwise
	 */
	public boolean checkForObstruction(Zone zone, Point point){
		if(zone.getTile(point).collides()){
			return true;
		}
		for(Entity e: zone.getEntities()){
			if(e.getWorldPosition().getPoint().equals(point)){ // check if entity same position
				if(!e.isPassable()){ // check if not passable
					return true;
				}
			}
		}
		for(Character c: zone.getCharacters()){
			if(c.getCoord().getPoint().equals(point)){
				return true;
			}
		}
		return false;
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
		// TODO Auto-generated method stub
		return Yelo;
	}
}
