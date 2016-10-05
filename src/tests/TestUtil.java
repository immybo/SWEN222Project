package tests;

import java.awt.Point;

import model.*;

public class TestUtil {
	public static Tile[][] generateTiles(int width, int height){
		Tile[][] tiles = new Tile[height][width];
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				tiles[y][x] = new FloorTile(new Point(x,y));
			}
		}
		return tiles;
	}
}
