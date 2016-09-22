package tests;

import org.junit.Test;

import junit.framework.TestCase;
import util.PointD;

public class PointDTest extends TestCase {

	@Test
	public void testAddPointD() {
		PointD p1 = new PointD(1, -1);
		PointD p2 = new PointD(2, 2);
		
		PointD p3 = p1.add(p2);
		
		assertEquals(new PointD(3, 1), p3);
		assertEquals(new PointD(1, -1), p1);
		assertEquals(new PointD(2, 2), p2);
	}
	
	@Test
	public void testAddDoubles(){
		PointD p1 = new PointD(0, 0);
		PointD p2 = p1.add(5.01, 4.74);
		
		assertEquals(new PointD(0, 0), p1);
		assertEquals(new PointD(5.01, 4.74), p2);
	}

}
