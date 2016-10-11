package tests;

import org.junit.Test;

import junit.framework.TestCase;
import network.client.Client;
import network.server.Server;

/**
 * Basic testing to ensure simple functionality of the
 * networking components
 * 
 * @author David Phillips
 *
 */
public class NetworkTests extends TestCase {
	
	/**
	 * Helper class for running server in background in tests
	 * 
	 * @author David Phillips
	 *
	 */
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
	
	/**
	 * Helper class for running client in the background in tests
	 * 
	 * @author David Phillips
	 *
	 */
	private class BackgroundClient extends Thread {
		public Client c;
		@Override
		public void run() {
			c = new Client(null, "localhost");
			c.run();
		}
	}
	
	/**
	 * Check server reports itself as having bound correctly
	 */
	@Test
	public void testServerIsBound() {
		Server s = new Server();
		s.initialise();
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
		s.initialise();
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
		s.initialise();
		s.initialise();
		assertTrue(s.isBound());
		s.stop();
	}
	
	@Test
	public void testHandshake() {
		Server server = new Server();
		server.initialise();
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
