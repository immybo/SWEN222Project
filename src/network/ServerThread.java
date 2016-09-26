package network;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Server worker thread for handling a single client
 * Facilitates the handling of multiple clients simultaneously
 * by a Server
 * 
 * @author David Phillips
 *
 */
public class ServerThread extends Thread {
	
	/* back reference to our parent/controlling Server object */
	private Server parentServer;
	
	/* flag to indicate whether or not there is game state which requires
	 * sending to the client */
	private boolean postFlag = false;
	
	/* socket connected to client and its streams */
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	/* FIXME need to pass in object for game state? */
	public ServerThread(Server server, Socket socket) {
		this.socket = socket;
		this.parentServer = server;
	}
	
	/**
	 * Post a notification of a change made to the game state
	 * that needs sending to the client
	 */
	synchronized public void post() {
		postFlag = true;
	}
	
	/**
	 * If required by a previous uncleared post, send the game
	 * state to the client and clear the post request
	 */
	synchronized private void processDownstream() {
		if (!postFlag)
			return;
		
		/* FIXME send the updated game state to the client */
	
		postFlag = false;
	}
	
	/**
	 * Process any data being sent to us from the client
	 */
	private void processUpstream() {
		/* FIXME do something here */
	}
	
	@Override
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			while(true) {
				/* muh non-blocking-ness */
				/* FIXME check if processing only one upstream packet before processing downstream traffic hurts performace.
				 * Might be better to, say, process between 0 and 5 upstream packets if they are there
				 * before going on to process downstream. Dunno.
				 */
				if (in.available() > 0) {
					processUpstream();
				}
				processDownstream();
			}
		} catch (IOException e) {
			System.err.println("Error occurred; stopping the server:"+e.getMessage());
			parentServer.stop();
		}
	}
}
