package network;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.Character;
import model.PlayableCharacter;

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
	
	/* is the server thread still supposed to be running its loop? */
	private boolean running;
	
	/* character for the client this thread is managing */
	private Character character;
	
	/* socket connected to client and its streams */
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	/**
	 * Construct a ServerThread object
	 * @param parentServer -- back reference to server object
	 * @param socket -- socket on which to communicate with client
	 * @param character -- in-game character this thread's client controls
	 */
	public ServerThread(Server parentServer, Socket socket, Character character) {
		this.socket = socket;
		this.parentServer = parentServer;
		this.character = character;
	}
	
	/**
	 * Process any data being sent to us from the client
	 */
	private void processUpstream() throws IOException {
		Protocol.Event packetType = Protocol.Event.values()[in.readInt()];
		
		switch (packetType) {
		case FORWARD:
			parentServer.getWorld().moveCharacterForward(character);
			break;
		case BACKWARD:
			parentServer.getWorld().moveCharacterBackward(character);
			break;
		case ROTATE_CLOCKWISE:
			parentServer.getWorld().rotateCharacter(true, character);
			break;
		case ROTATE_ANTICLOCKWISE:
			parentServer.getWorld().rotateCharacter(false, character);
			break;
		default:
			System.err.println("Unhandled event : "+packetType);
			break;
		}
		
	}
	
	synchronized private boolean isRunning() {
		return running;
	}
	
	@Override
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			running = true;
			while(isRunning()) {
				processUpstream();
			}
			
		} catch (IOException e) {
			if (isRunning()) {
				e.printStackTrace();
				parentServer.stop();
			}
		}
	}

	/**
	 * Notify the thread to stop running
	 */
	synchronized public void shutdown() {
		this.running = false;
	}
}
