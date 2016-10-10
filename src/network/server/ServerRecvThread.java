package network.server;

import datastorage.XMLInterface;

import java.awt.Point;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import model.Character;
import model.Enemy;
import model.Player;
import model.Interaction;
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
	
	/* inward stream */
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
		if (readObj == null || !(readObj instanceof Event)) {
			System.err.println("object read not instance of Event, ignoring");
			return false;
		}
		Event packetType = (Event)readObj;
		synchronized (parentServer) {
			switch (packetType) {
			case GAME_LOAD:
				String loadFile = in.readUTF();
				if (loadFile == null) {
					System.err.println("Got null filename to load from; not trying to load");
				}
				parentServer.setWorld(XMLInterface.loadGame(loadFile));
				break;
			case GAME_SAVE:
				String saveFile = in.readUTF();
				if (saveFile == null) {
					System.err.println("Got null filename to save to; not trying to save");
				}
				XMLInterface.saveGame(parentServer.getWorld(), saveFile);
				break;
			case FORWARD:
				player.moveForward();
				break;
			case BACKWARD:
				player.moveBackwards();
				break;
			case MOVE_TO_POINT:
				readObj = in.readObject();
				if(readObj == null || !(readObj instanceof Point)){
					System.err.println("Received malformed point in move command");
					break;
				}
				player.moveToPoint((Point)readObj);
				break;
			case ROTATE_CLOCKWISE:
				player.rotate(true);
				break;
			case ROTATE_ANTICLOCKWISE:
				player.rotate(false);
				break;
			case INTERACT:
				readObj = in.readObject();
				if (readObj == null || !(readObj instanceof Interaction)) {
					System.err.println("Received malformed interaction in interact command");
					break;
				}
				System.err.println("Server event receiver: not calling unimplemented interaction method");
				//Interaction interaction = (Interaction)readObj;
				//player.interact(interaction);
				break;
			case ATTACK:
				long characterID = in.readLong();
				Character target = player.getZone().getCharacterFromID(characterID);
				if (target == null) {
					System.err.println("Cannot find character with id "+characterID+", bail");
					break;
				}
				if (!(target instanceof Enemy)) {
					System.err.println("Received non-enemy character to attack. Violent sod tried to attack a"+target.getClass());
					break;
				}
				player.attack((Enemy)target);
				break;
			default:
				System.err.println("Unhandled event in server event receiver: "+packetType);
				break;
			}
		}
		return true;
	}
	
	@Override
	public void run() {
		try {
			/* while we can, process events being sent to us */
			while(processUpstream())
				;
			
			/* loop broken, ask the server to stop */
			parentServer.stop();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			parentServer.stop();
		}
	}
}
