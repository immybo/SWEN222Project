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
	public void testLevelStorage(){
		Tile[][] tiles1 = generateTiles(5, 5);
		Tile[][] tiles2 = generateTiles(4, 4);
		
		Zone zone1 = new Zone("zone1", tiles1);
		Zone zone2 = new Zone("zone2", tiles2);
		
		Level level = new Level("level", new Zone[]{ zone1, zone2 });
		
		XMLInterface.saveToFile(level, new File("testxml.xml"));
		
		Level importedLevel = XMLInterface.loadFromFile(new Level.LevelFactory(), new File("testxml.xml"));
		
		assertEquals(level, importedLevel);
		deleteTestXMLFile();
	}
	
	@Test
	public void testZoneStorage(){
		Tile[][] tiles = generateTiles(10,10);
		
		Zone zone = new Zone("zone", tiles);
		XMLInterface.saveToFile(zone,  new File("testxml.xml"));
		
		Zone importedZone = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("testxml.xml"));
		
		assertEquals(zone, importedZone);
		deleteTestXMLFile();
	}
	
	@Test
	public void testKeyStorage(){
		Key key = new Key(new PointD(5, 5), 1, "testKey");
		XMLInterface.saveToFile(key, new File("testxml.xml"));
		Key importedKey = XMLInterface.loadFromFile(new Key.KeyFactory(), new File("testxml.xml"));
		
		assertEquals(key, importedKey);
		assertEquals(key.getSize(), importedKey.getSize());
		assertEquals(key.inInventory(), importedKey.inInventory());
		assertEquals(key.getPosition(), importedKey.getPosition());
	}
	
	@Test
	public void testPlayableCharacterStorage(){
		Zone zone1 = new Zone("zone1", generateTiles(3,3));
		Zone zone2 = new Zone("zone2", generateTiles(5,5));
		PlayableCharacter pupo = new PlayableCharacter(zone1, new Coord(new Direction(0), new Point(1,1)), true);
		PlayableCharacter yelo = new PlayableCharacter(zone2, new Coord(new Direction(3), new Point(0,0)), false);

		XMLInterface.saveToFile(zone1, new File("src/tests/testfiles/testxml1.xml"));
		XMLInterface.saveToFile(zone2, new File("src/tests/testfiles/testxml2.xml"));
		XMLInterface.saveToFile(pupo, new File("src/tests/testfiles/testxml3.xml"));
		XMLInterface.saveToFile(yelo, new File("src/tests/testfiles/testxml4.xml"));
		
		Zone importedZone1 = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("src/tests/testfiles/testxml1.xml"));
		Zone importedZone2 = XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File("src/tests/testfiles/testxml2.xml"));
		Zone[] importedZones = new Zone[]{importedZone1, importedZone2};
		PlayableCharacter importedPupo = XMLInterface.loadFromFile(new PlayableCharacter.Factory(importedZones), new File("src/tests/testfiles/testxml3.xml"));
		PlayableCharacter importedYelo = XMLInterface.loadFromFile(new PlayableCharacter.Factory(importedZones), new File("src/tests/testfiles/testxml4.xml"));
		
		assertEquals(zone1, importedPupo.getZone());
		assertEquals(zone2, importedYelo.getZone());
		assertEquals(zone1, importedZone1);
		assertEquals(zone2, importedZone2);
		
		assertEquals(pupo, importedPupo);
		assertEquals(yelo, importedYelo);
	}
	
	public void deleteTestXMLFile(){
		try {
			Files.deleteIfExists(FileSystems.getDefault().getPath("testxml.xml"));
		} catch (IOException e) {
			// We don't really care if we can't find it. If we can't access it, the user screwed up. Oh well.
		}
	}
		
	public Tile[][] generateTiles(int width, int height){
		Tile[][] tiles = new Tile[height][width];
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				tiles[y][x] = new FloorTile(new Point(x,y));
			}
		}
		return tiles;
	}
}
