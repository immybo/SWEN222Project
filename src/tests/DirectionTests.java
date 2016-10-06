package tests;

import org.junit.Test;

import junit.framework.TestCase;
import util.Direction;

public class DirectionTests extends TestCase {

	@Test
	public void testOpposite() {
		Direction n = new Direction(Direction.NORTH);
		Direction s = new Direction(Direction.SOUTH);
		Direction e = new Direction(Direction.EAST);
		Direction w = new Direction(Direction.WEST);
		
		assertEquals(Direction.oppositeDirection(n), s);
		assertEquals(Direction.oppositeDirection(e), w);
		assertEquals(Direction.oppositeDirection(s), n);
		assertEquals(Direction.oppositeDirection(w), e);
	}
}
