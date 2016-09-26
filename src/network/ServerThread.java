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
	
	/* socket connected to clinet and its streams */
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	/* FIXME need to pass in object for game state? */
	public ServerThread(Server server, Socket socket) {
		this.socket = socket;
		this.parentServer = server;
	}
	
	@Override
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error occurred; stopping the server:"+e.getMessage());
			parentServer.stop();
		}
	}
}
