package network;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.Character;
import network.Protocol.Event;

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
	
	/* socket connected to client and its inward stream */
	private Socket socket;
	private ObjectInputStream in;
	
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
	private void processUpstream() throws IOException, ClassNotFoundException {
		Object readObj = in.readObject();
		Event packetType = (Event)readObj;
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
			in = new ObjectInputStream(socket.getInputStream());
			
			running = true;
			while(isRunning()) {
				processUpstream();
			}
			
		} catch (IOException | ClassNotFoundException e) {
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
