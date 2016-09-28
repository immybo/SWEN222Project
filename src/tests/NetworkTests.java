package tests;

import org.junit.Test;

import junit.framework.TestCase;
import network.Server;
import network.Client;

public class NetworkTests extends TestCase {
	private class BackgroundServer extends Thread {
		private Server s;
		
		public BackgroundServer(Server s) {
			this.s = s;
		}
		
		@Override
		public void run() {
			s.run();
			s.stop();
		}
	}
	private class BackgroundClient extends Thread {
		public Client c;
		@Override
		public void run() {
			c = new Client("localhost");
			c.run();
		}
	}
	
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
	
	@Test
	public void testHandshake() {
		Server server = new Server();
		assertTrue(server.initialise());
		assertTrue(server.isBound());
		BackgroundServer serverThread = new BackgroundServer(server);
		
		/* start server as background thread */
		serverThread.start();
		
		/* start two clients */
		/* FIXME magic constant 2, should be derived from elsewhere */
		int clientCount = 2;
		for (int i = 0; i < clientCount; i++)
			(new BackgroundClient()).start();
			
		try {
			/* give some time for clients to connect (or not) */
			serverThread.join(5000);
			if (server.getConnectedCount() != clientCount)
				fail("Server didn't accept all connections");
		} catch (Exception e) {
			throw new Error(e);
		} finally {
			/* stop the server */
			server.stop();
		}
	}
}
