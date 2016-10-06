package tests;

import org.junit.Test;

import junit.framework.TestCase;
import util.Direction;

public class DirectionTests extends TestCase {

	private static final Direction n = new Direction(Direction.NORTH);
	private static final Direction s = new Direction(Direction.SOUTH);
	private static final Direction e = new Direction(Direction.EAST);
	private static final Direction w = new Direction(Direction.WEST);
	
	@Test
	public void testEquality() {
		/* check for equality */
		assertEquals(n,n);
		assertEquals(e,e);
		assertEquals(s,s);
		assertEquals(w,w);
		assertEquals(n, new Direction(Direction.NORTH));
		assertEquals(e, new Direction(Direction.EAST));
		assertEquals(s, new Direction(Direction.SOUTH));
		assertEquals(w, new Direction(Direction.WEST));
		
		/* inequality checks */
		assertFalse(n.equals(null));
		assertFalse(n.equals("Top kek m'lady"));
		assertFalse(n.equals(e));
	}
	
	@Test
	public void testOpposite() {
		assertEquals(Direction.oppositeDirection(n), s);
		assertEquals(Direction.oppositeDirection(e), w);
		assertEquals(Direction.oppositeDirection(s), n);
		assertEquals(Direction.oppositeDirection(w), e);
	}
}
