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
import junit.framework.TestCase;

public class DataStorageTests extends TestCase {
	@Test
	public void testLevelStorage1(){
		Tile[][] tiles1 = generateTiles(5, 5);
		Tile[][] tiles2 = generateTiles(4, 4);
		
		Zone zone1 = new Zone("zone1", tiles1);
		Zone zone2 = new Zone("zone2", tiles2);
		
		Level level = new Level("level", new Zone[]{ zone1, zone2 });
		
		XMLInterface.saveToFile(level, new File("testxml.xml"));
		
		Level importedLevel = XMLInterface.loadFromFile(new Level.LevelFactory(), new File("testxml.xml"));
		
		assertEquals(level, importedLevel);
		
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
