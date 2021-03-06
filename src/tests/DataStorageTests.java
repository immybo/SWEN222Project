package tests;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import datastorage.XMLInterface;
import model.*;
import util.Coord;
import util.Direction;
import util.PointD;
import junit.framework.TestCase;

public class DataStorageTests extends TestCase {
	
	@Test
	public void testZoneStorage(){
		Tile[][] tiles = TestUtil.generateTiles(10,10);
		tiles[0][0] = new WallTile(new Point(0,0));
		tiles[0][1] = new WallTile(new Point(1,0));
		Zone zone = new Zone("zone", tiles);
		zone.addCharacter(new Player(zone, new Coord(new Direction(1), new Point(1,1)), false));
		XMLInterface.saveToFile(zone,  new File("testxml.xml"));
		
		Zone importedZone = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("testxml.xml"));
		importedZone.addCharacter(new Player(zone, new Coord(new Direction(1), new Point(1,1)), false));
		assertEquals(zone, importedZone);
		deleteTestXMLFile();
	}
	
	@Test
	public void testKeyStorage(){
		Key key = new Key(new Point(5, 5), "testKey");
		XMLInterface.saveToFile(key, new File("testxml.xml"));
		Key importedKey = XMLInterface.loadFromFile(new Key.KeyFactory(), new File("testxml.xml"));
		
		assertEquals(key, importedKey);
		assertEquals(key.inInventory(), importedKey.inInventory());
		assertEquals(key.getPosition(), importedKey.getPosition());
	}
	
	@Test
	public void testPlayableCharacterStorage(){
		Zone zone1 = new Zone("zone1", TestUtil.generateTiles(3,3));
		Zone zone2 = new Zone("zone2", TestUtil.generateTiles(5,5));
		Player pupo = new Player(zone1, new Coord(new Direction(0), new Point(1,1)), true);
		Player yelo = new Player(zone2, new Coord(new Direction(3), new Point(0,0)), false);

		XMLInterface.saveToFile(zone1, new File("src/tests/testfiles/testxml1.xml"));
		XMLInterface.saveToFile(zone2, new File("src/tests/testfiles/testxml2.xml"));
		XMLInterface.saveToFile(pupo, new File("src/tests/testfiles/testxml3.xml"));
		XMLInterface.saveToFile(yelo, new File("src/tests/testfiles/testxml4.xml"));
		
		Zone importedZone1 = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("src/tests/testfiles/testxml1.xml"));
		Zone importedZone2 = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("src/tests/testfiles/testxml2.xml"));
		Zone[] importedZones = new Zone[]{importedZone1, importedZone2};
		Player importedPupo = XMLInterface.loadFromFile(new Player.Factory(importedZones), new File("src/tests/testfiles/testxml3.xml"));
		Player importedYelo = XMLInterface.loadFromFile(new Player.Factory(importedZones), new File("src/tests/testfiles/testxml4.xml"));
		
		assertEquals(zone1, importedPupo.getZone());
		assertEquals(zone2, importedYelo.getZone());
		assertEquals(zone1, importedZone1);
		assertEquals(zone2, importedZone2);
		
		assertEquals(pupo, importedPupo);
		assertEquals(yelo, importedYelo);
	}
	
	@Test
	public void testCoinStorage(){
		Coin coin = new Coin();
		XMLInterface.saveToFile(coin, new File("src/tests/testfiles/testxml1.xml"));
		
		Coin importedCoin = XMLInterface.loadFromFile(new Coin.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(coin, importedCoin);
	}
	
	@Test
	public void testTileStorage(){
		Point pos = new Point(1,1);
		FloorTile floor = new FloorTile(pos);
		XMLInterface.saveToFile(floor, new File("src/tests/testfiles/testxml1.xml"));
		
		FloorTile importedFloor = XMLInterface.loadFromFile(new FloorTile.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		WallTile wall = new WallTile(pos);
		XMLInterface.saveToFile(wall, new File("src/tests/testfiles/testxml1.xml"));
		
		WallTile importedWall = XMLInterface.loadFromFile(new WallTile.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(floor, importedFloor);
		assertEquals(wall, importedWall);
	}
	
	@Test
	public void testEntityStorage(){
		Zone zone = new Zone("zone1", TestUtil.generateTiles(3,3));
		Coord coord = new Coord(new Direction(0), new Point(1,1));
		Inventory inventory = new Inventory(2);
		Furniture furniture = new Furniture(zone, coord, inventory, "TESTING");
		XMLInterface.saveToFile(furniture, new File("src/tests/testfiles/testxml1.xml"));
		Zone[] zones = new Zone[1];
		zones[0] = zone;
		Furniture importedFurniture = XMLInterface.loadFromFile(new Furniture.Factory(zones), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(furniture, importedFurniture);
	}
	
	@Test
	public void testInteractionStorage(){
		Inspect inspect = new Inspect("this is an interaction yes?");
		XMLInterface.saveToFile(inspect, new File("src/tests/testfiles/testxml1.xml"));
		
		Inspect importedInspect = XMLInterface.loadFromFile(new Inspect.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(inspect, importedInspect);
	}
	
	@Test
	public void testInventory(){
		Coin coin = new Coin();
		Inventory inventory = new Inventory(2);
		inventory.addItem(coin);
		XMLInterface.saveToFile(inventory, new File("src/tests/testfiles/testxml1.xml"));
		
		Inventory importedInventory = XMLInterface.loadFromFile(new Inventory.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(inventory, importedInventory);
	}
	
	@Test
	public void testWorldStorage(){
		World world = World.firstLevel();
		XMLInterface.saveToFile(world, new File("src/tests/testfiles/testxml1.xml"));
		
		World importedWorld = XMLInterface.loadFromFile(new World.Factory(), new File("src/tests/testfiles/testxml1.xml"));
		
		assertEquals(world, importedWorld);
	}
	
	public void deleteTestXMLFile(){
		try {
			Files.deleteIfExists(FileSystems.getDefault().getPath("testxml.xml"));
		} catch (IOException e) {
			// We don't really care if we can't find it. If we can't access it, the user screwed up. Oh well.
		}
	}
	
}
