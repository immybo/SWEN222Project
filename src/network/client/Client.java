package network.client;


import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.awt.Point;
import java.io.IOException;
import java.net.Socket;
import java.lang.Thread.UncaughtExceptionHandler;

import model.Interaction;
import view.GameFrame;
import network.NetworkError;
import network.Protocol;
import network.Protocol.Event;

public class Client {
	private String host;
	private int port;
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ClientThread clientThread;
	private UncaughtExceptionHandler errorHandler;
	
	/**
	 * Constructor that also creates a window for the game
	 * @param host
	 */
	public Client(String host){
		this(host, Protocol.DEFAULT_PORT);
	}
	
	/**
	 * Extended constructor using custom port number
	 * @param host --- host name
	 * @param port --- port number to listen too
	 */
	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Perform a simple sanity-check handshake with a server attached
	 * to a socket based on the protocol set out in the network.Protocol
	 * class
	 * @param socket -- server socket to handshake with
	 * @return true if handshake succeeds, false otherwise
	 * @throws IOException
	 */
	public boolean doHandshake(Socket socket) throws IOException {
		/* receive server's greeting */
		Object rawResponse;
		
		/* receive from the other end, failing if no such local class */
		try {
			rawResponse = in.readObject();
		} catch (ClassNotFoundException e) {
			return false;
		}
		
		if (!(rawResponse instanceof String))
			return false;
		
		String response = (String)rawResponse;
		
		/* reply with our magic sequence */
		out.writeObject(Protocol.CLIENT_MAGIC);
		
		/* did the client's response match the expected value? */
		return (response != null && response.equals(Protocol.SERVER_MAGIC));
	}
	
	/**
	 * Run the client
	 * When run, will connect to server and attempt a handshake
	 */
	public void run() {
		try {
			/* create socket connection to host:port */
			sock = new Socket(host, port);
			
			/* create input+output streams for objects */
			out = new ObjectOutputStream(sock.getOutputStream());
			in = new ObjectInputStream(sock.getInputStream());
			
			/* attempt handshake with server, bailing if it fails */
			if (!doHandshake(sock)) {
				sock.close();
				throw new NetworkError("Handshaking with server failed");
			}
			System.out.println("Starting client thread");
			this.clientThread = new ClientThread(in);
			this.clientThread.setUncaughtExceptionHandler(errorHandler);
			this.clientThread.start();
		} catch (IOException e) {
			throw new NetworkError(e);
		}
	}
	
	public void disconnect(){
		System.out.println("Disconnecting from server");
		if (clientThread != null)
			this.clientThread.shutdown();
		try {
			if (sock != null)
				this.sock.close();
			
			try {
				if (clientThread != null)
					clientThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			/* not too concerned at this point, so turn it into an Error */
			throw new NetworkError(e);
		}
	}
	
	public void setUncaughtExceptionHandler(UncaughtExceptionHandler e) {
		this.errorHandler = e;
	}
	
	/**
	 * Asks the server to move the player to the given point.
	 * 
	 * @param newPoint The point to move to.
	 */
	public void moveTo(Point newPoint) throws IOException {
		out.writeObject(Event.MOVE_TO_POINT);
		out.writeObject(newPoint);
	}
	
	/**
	 * Ask the server to move the player forwards (in
	 * their facing direction)
	 * @throws IOException
	 */
	public void moveForward() throws IOException {
		out.writeObject(Event.FORWARD);
	}
	
	/**
	 * Ask the server to move the player backward (away from
	 * their facing direction)
	 * @throws IOException
	 */
	public void moveBackward() throws IOException {
		out.writeObject(Event.BACKWARD);
	}
	
	/**
	 * Ask the server to rotate the player 90° clockwise,
	 * when looking top-down
	 * @throws IOException
	 */
	public void rotateClockwise() throws IOException {
		out.writeObject(Event.ROTATE_CLOCKWISE);
	}
	
	/**
	 * Ask the server to rotate the player 90° anti-clockwise
	 * when looking top-down
	 * @throws IOException
	 */
	public void rotateAnticlockwise() throws IOException {
		out.writeObject(Event.ROTATE_ANTICLOCKWISE);
	}
	
	/**
	 * Asks the server to attack at a point with the player.
	 * 
	 * @param point The point to attack (occupant(s))
	 * @throws IOException
	 */
	public void attack(long enemyID) throws IOException {
		out.writeObject(Event.ATTACK);
		out.writeLong(enemyID);
	}
	
	/**
	 * Ask the server to execute the specified interaction
	 * @param interaction
	 * @throws IOException
	 */
	public void interact(Interaction interaction) throws IOException {
		out.writeObject(Event.INTERACT);
		out.writeObject(interaction);
	}
	
	/**
	 * Ask the server to save the world state to file
	 * @param name -- the filename to save to server-side
	 * @throws IOException
	 */
	public void saveWorld(String name) throws IOException {
		out.writeObject(Event.GAME_SAVE);
		out.writeUTF(name);
	}
	
	/**
	 * Ask the server to load the world state from file
	 * @param name -- the filename to save to server-side
	 * @throws IOException
	 */
	public void loadWorld(String name) throws IOException {
		out.writeObject(Event.GAME_LOAD);
		out.writeUTF(name);
	}
}
