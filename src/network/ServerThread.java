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
	
	/* socket connected to clinet and its streams */
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	/* FIXME need to pass in object for game state? */
	public ServerThread(Socket s) {
		this.socket = s;
	}
	
	@Override
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Some error; bailing");
		}
	}
}
