package tests;

import java.awt.Point;

import org.junit.Test;

import junit.framework.TestCase;
import model.FloorTile;
import model.Furniture;
import model.Gate;
import model.Interaction;
import model.Item;
import model.Key;
import model.KeyGate;
import model.Player;
import model.Portal;
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

	@Test
	public void testKeyGate(){
		World world = generateWorld2();
		Player pupo = world.getPupo();
		pupo.rotate(false); //facing east
		assertTrue(pupo.moveForward());
		assertTrue(pupo.moveForward()); // should have run over key
		boolean correctKey = false; // check if it has correct key
		for(Item i: pupo.getInventory().getItems()){
			if(i instanceof Key){
				Key k = (Key)i;
				if(k.getKeyID().equals("blue")){
					correctKey = true;
				}
			}
		}
		assertTrue(correctKey); //should have key
		pupo.rotate(true);
		assertTrue(pupo.moveForward());
		assertFalse(pupo.moveForward()); // run into gate
		Interaction[] interactions = pupo.getZone().getInteractions(pupo);
		if(interactions == null) fail();
		Interaction toDo = null;
		for(Interaction i: interactions){
			if(i.getText().equals("Use Key")){
				toDo = i;
			}
		}
		toDo.execute(pupo);
		assertTrue(pupo.moveForward()); // run into the gate this time no smash head;
	}

	public void testPortal(){
		World world = generateWorld2();
		Player pupo = world.getPupo();
		Zone originZone = pupo.getZone();
		assertTrue(pupo.moveForward());
		assertFalse(pupo.moveForward()); // should have run into portal, smash face
		Interaction[] interactions = pupo.getZone().getInteractions(pupo);
		if(interactions == null) fail();
		Interaction toDo = null;
		for(Interaction i: interactions){
			if(i.getText().equals("Use Portal")){
				toDo = i;
			}
		}
		toDo.execute(pupo);
		assertTrue(!pupo.getZone().equals(originZone)); //pupo is in diff zone than before
		assertEquals(pupo.getCoord().getPoint().x,2);
		assertEquals(pupo.getCoord().getPoint().y,1);
		
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
		newZones[0].addEntity(new Furniture(newZones[0], new Coord(new Direction(Direction.NORTH),new Point(2,3)), null, "testObject"));
		return new World("test",newZones, pupo, yelo);
	}

	/**
	 * Generates a test world which is a world with 2 zone and that zone is a 3x3 walkable area
	 *  - locked gate at (1,1) z0
	 *  - key at (1,3) z0
	 *  - portal at (3,1) z0
	 *  - pupo at (3,3) z0
	 *  - floor tiles is (1,1) to (3,3) z0
	 *  - portal at (1,1) z1
	 *  - yelo at (4,1) z1
	 *  - floor tiles is (1,1) to (4,1) z1
	 *  - wall tiles is 1 thick border around floor tiles
	 * @return
	 */
	public World generateWorld2(){
		Zone[] newZones = new Zone[2];

		//make just a test zone1 5x5 big
		Tile[][] tiles1 = new Tile[5][5];
		for(int x = 0; x<5; x++){
			for(int y = 0; y<5; y++){
				tiles1[y][x] = new WallTile(new Point(x,y));
				tiles1[y][x].setDrawImagePath("wallTile");
			}
		}
		for(int x = 1; x<4; x++){
			for(int y = 1; y<4; y++){
				tiles1[y][x] = new FloorTile(new Point(x,y));
				tiles1[y][x].setDrawImagePath("floorTile");
			}
		}
		newZones[0] = new Zone("testZone1", tiles1);
		Player pupo = new Player(newZones[0], new Coord(new Direction(Direction.NORTH), new Point(3,3)), true);
		newZones[0].addEntity(new KeyGate(Gate.State.LOCKED, newZones[0], new Coord(new Direction(Direction.NORTH), new Point(1,1)), "blue"));
		newZones[0].addItem(new Key(new Point(1,3), "blue"));
		newZones[0].addEntity(new Portal(newZones[0], new Coord(new Direction(Direction.NORTH), new Point(3,1)), "portal1"));


		//make a test zone2 6x3 big
		Tile[][] tiles2 = new Tile[3][6];
		for(int x = 0; x<6; x++){
			for(int y = 0; y<3; y++){
				tiles2[y][x] = new WallTile(new Point(x,y));
				tiles2[y][x].setDrawImagePath("wallTile");
			}
		}
		for(int x = 1; x<5; x++){
			for(int y = 1; y<2; y++){
				tiles2[y][x] = new FloorTile(new Point(x,y));
				tiles2[y][x].setDrawImagePath("floorTile");
			}
		}
		newZones[1] = new Zone("testZone2", tiles2);
		Player yelo = new Player(newZones[1], new Coord(new Direction(Direction.NORTH), new Point(4,1)), true);
		newZones[1].addEntity(new Portal(newZones[1], new Coord(new Direction(Direction.NORTH), new Point(1,1)), "portal1"));
		return new World("test",newZones, pupo, yelo);
	}


}