package model;

import java.awt.Point;

import util.Coord;
import util.Direction;
import util.PointD;

public class World {
	private Zone[] zones;
	private Character Pupo;
	private Character Yelo;
	
	public World(Zone[] zones, Character pupo, Character yelo){
		this.zones = zones;
	}
	
	public World testWorld(){
		zones = new Zone[1];
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
		zones[1].addItem(new Key(new PointD(1,3), 0.5, "testKey"));
		zones[1].addEntity(new KeyGate(Gate.State.LOCKED, zones[1], new Coord(new Direction(0), new PointD (1,5)), 1, "testKey"));
		return null;
	}
	
	
	
}
