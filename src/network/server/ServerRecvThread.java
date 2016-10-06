package network.server;

import java.net.Socket;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import model.Player;
import model.Interaction;
import model.World;
import network.Protocol.Event;

/**
 * Server worker thread for handling a single client
 * Facilitates the handling of multiple clients simultaneously
 * by a Server
 * 
 * @author David Phillips
 *
 */
public class ServerRecvThread extends Thread {
	
	/* back reference to our parent/controlling Server object */
	private Server parentServer;
	
	/* character for the client this thread is managing */
	private Player player;
	
	/* socket connected to client and its inward stream */
	private Socket socket;
	private ObjectInputStream in;
	
	/**
	 * Construct a ServerThread object
	 * @param parentServer -- back reference to server object
	 * @param socket -- socket on which to communicate with client
	 * @param character -- in-game character this thread's client controls
	 */
	public ServerRecvThread(Server parentServer, ObjectInputStream in, Player player) {
		this.in = in;
		this.parentServer = parentServer;
		this.player = player;
	}
	
	/**
	 * Process any data being sent to us from the client
	 * @return false on failure, true otherwise
	 */
	private boolean processUpstream() throws IOException, ClassNotFoundException {
		Object readObj;
		
		/* read next object from the stream */
		try {
			readObj = in.readObject();
		} catch (EOFException e) {
			/* bail on EOF (stream/socket closed) */
			return false;
		}
		
		/* ensure casting to Event is safe */
		if (!(readObj instanceof Event)) {
			System.err.println("object read not instance of Event, ignoring");
			return false;
		}
		Event packetType = (Event)readObj;
		World w = parentServer.getWorld();
		switch (packetType) {
		case FORWARD:
			player.moveForward();
			break;
		case BACKWARD:
			player.moveBackwards();
			break;
		case ROTATE_CLOCKWISE:
			player.rotate(true);
			break;
		case ROTATE_ANTICLOCKWISE:
			player.rotate(false);
			break;
		case INTERACT:
			readObj = in.readObject();
			if (!(readObj instanceof Interaction)) {
				System.err.println("Received malformed interaction from "+socket.getRemoteSocketAddress());
				break;
			}
			Interaction interaction = (Interaction)readObj;
			w.interact(interaction, player);
			break;
		default:
			System.err.println("Unhandled event : "+packetType);
			break;
		}
		return true;
	}
	
	@Override
	public void run() {
		try {
			//System.err.println("Doing the thing");
			//in = new ObjectInputStream(socket.getInputStream());
			//System.err.println("Created the thing \\o/");
			
			while(processUpstream())
				;
			
			parentServer.stop();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			parentServer.stop();
		}
	}
}
