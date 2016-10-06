package tests;

import java.awt.Point;

import org.junit.Test;

import junit.framework.TestCase;
import model.FloorTile;
import model.Furniture;
import model.Gate;
import model.Key;
import model.KeyGate;
import model.Player;
import model.Tile;
import model.WallTile;
import model.World;
import model.Zone;
import model.Character;
import util.Coord;
import util.Direction;
import util.PointD;

public class GameTests extends TestCase {

	
	@Test
	public void testSimpleMovement(){
		World world = generateWorld1();
		Character yelo = world.getYelo();
		//test movement feedback
		assertTrue(yelo.moveForward()); //should be at 5,4
		yelo.rotate(true); //facing east
		assertFalse(yelo.moveForward()); //run into wall
		yelo.rotate(false); //facing north
		assertTrue(yelo.moveForward()); //should be at 5,3
		yelo.rotate(false); //facing west
		assertTrue(yelo.moveForward()); //should be at 4,3
		assertTrue(yelo.moveForward()); //should be at 3,3
		assertFalse(yelo.moveForward()); //run into object
		yelo.rotate(true); //facing north
		assertTrue(yelo.moveForward()); //should be at 3,2
		assertTrue(yelo.moveForward()); //should be at 3,1
		//test end position is correct
		assertEquals(yelo.getCoord().getPoint().x, 3);
		assertEquals(yelo.getCoord().getPoint().y, 1);
	}
	
	/**
	 * Generates a test world which is a world with 1 zone and that zone is a 5x5 walkable area
	 *  - impassable furniture at (2,3)
	 *  - pupo at (1,1)
	 *  - yelo at (5,5)
	 *  - floor tiles is (1,1) to (5,5)
	 *  - wall tiles is 1 thick border around floor tiles
	 * @return
	 */
	public World generateWorld1(){
		Zone[] newZones = new Zone[1];
		//make just a test zone 7x7 big
		Tile[][] tiles = new Tile[7][7];
		for(int x = 0; x<7; x++){
			for(int y = 0; y<7; y++){
				tiles[y][x] = new WallTile(new Point(x,y));
				tiles[y][x].setDrawImagePath("wallTile");
			}
		}
		//5x5 walkable room
		for(int x = 1; x<6; x++){
			for(int y = 1; y<6; y++){
				tiles[y][x] = new FloorTile(new Point(x,y));
				tiles[y][x].setDrawImagePath("floorTile");
			}
		}
		newZones[0] = new Zone("testZone", tiles);
		//characters
		Player pupo = new Player(newZones[0], new Coord(new Direction(Direction.SOUTH), new Point(1,1)), true);
		Player yelo = new Player(newZones[0], new Coord(new Direction(Direction.NORTH),new Point(5,5)), false);
		newZones[0].setPupo(pupo);
		newZones[0].setYelo(yelo);
		newZones[0].addEntity(new Furniture(newZones[0], new Coord(new Direction(Direction.NORTH),new Point(2,3)), null, 1, "testObject"));
		
		return new World("test",newZones, pupo, yelo);
	}

}