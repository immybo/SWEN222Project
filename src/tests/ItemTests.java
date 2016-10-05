package tests;

import java.awt.Point;

import org.junit.Test;

import util.Coord;
import util.Direction;
import junit.framework.TestCase;
import model.*;

public class ItemTests extends TestCase {
	@Test
	public void testPickupOnCollision(){
		Zone zone = new Zone("testzone", TestUtil.generateTiles(5, 5));
		Player player = new Player(zone, new Coord(new Direction(Direction.NORTH), new Point(3, 4)), true);
		
		Item item1 = new TestItem(new Point(4,4));
		Item item2 = new TestItem(new Point(5,5));
		zone.addItem(item1); zone.addItem(item2);
		
		player.moveIn(new Direction(Direction.EAST));
		
		assertTrue(item1.inInventory());
		assertTrue(player.getInventory().containsItem(item1));
		
		assertFalse(item2.inInventory());
		assertFalse(player.getInventory().containsItem(item2));
		
		player.moveIn(new Direction(Direction.EAST));
		player.moveIn(new Direction(Direction.SOUTH));
		
		assertTrue(item2.inInventory());
		assertTrue(player.getInventory().containsItem(item2));
	}
}
