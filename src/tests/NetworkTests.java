package tests;

import org.junit.Test;

import junit.framework.TestCase;
import network.Server;
import network.Client;

public class NetworkTests extends TestCase {
	
	/**
	 * Check server reports itself as having bound correctly
	 */
	@Test
	public void testServerIsBound() {
		Server s = new Server();
		assertTrue(s.initialise());
		assertTrue(s.isBound());
		s.stop();
	}
	
	/**
	 * Don't initialise server and check it hasn't bound
	 */
	@Test
	public void testServerIsntBound() {
		Server s = new Server();
		assertFalse(s.isBound());
		s.stop();
	}
	
	/**
	 * Stop server and check it unbound
	 */
	@Test
	public void testServerStopUnbind() {
		Server s = new Server();
		assertTrue(s.initialise());
		s.stop();
		assertFalse(s.isBound());
	}
	
	/**
	 * Initialise the server twice, checking return values are in check
	 */
	@Test
	public void testServerDoubleInitialise() {
		/* initialise server */
		Server s = new Server();
		assertTrue(s.initialise());
		assertTrue(s.initialise());
		assertTrue(s.isBound());
		s.stop();
	}
}
