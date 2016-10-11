package network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import model.World;
import network.Protocol;
import network.Protocol.Event;
import network.NetworkError;
import model.Player;

public class Server {
	
	private ServerSocket sock;
	private int port;
	private Socket[] clientSocks;
	private ObjectOutputStream[] outs;
	private ObjectInputStream[] ins;
	private int clientCount;
	private World world;
	private Player[] players;
	private Thread[] workerThreads;
	private Thread tickThread;
	private Thread sendThread;
	
	/**
	 * Simple constructor using default port number
	 */
	public Server() {
		this(Protocol.DEFAULT_PORT);
	}
	
	/**
	 * Extended constructor taking custom port number
	 * @param port --- port number to listen on
	 */
	public Server(int port) {
		this.port = port;
		clientCount = 0;
		
		/* FIXME HACK set up world and players */
		world = World.firstLevel();
		this.players = new Player[2];
		players[0] = world.getPupo();
		players[1] = world.getYelo();
		
	}
	
	/**
	 * Initialise the server
	 * 
	 * @throws NetworkError
	 */
	public void initialise() {
		/* has the server already been initialised? */
		if (sock != null && sock.isBound()) {
			return;
		}
		try {
			sock = new ServerSocket();
			sock.setReuseAddress(true);
			sock.bind(new InetSocketAddress(port));
			System.out.println("Server listening on port "+port);
			return;
		} catch (IOException e) {
			throw new NetworkError("Error binding on port " + port + ": " + e.getMessage());
		}
	}
	
	
	/**
	 * Clean up resources allocated to the server
	 * 
	 * @return true on success, false otherwise
	 * @throws NetworkError 
	 */
	private boolean cleanup() {
		try {
			if (sock != null && !sock.isClosed()) {
				sock.close();
			}
			return true;
		} catch (IOException e) {
			throw new NetworkError("cleanup: Error closing server socket: " + e.getMessage());
		}
	}
	
	/**
	 * Check if the server's listener socket is bound
	 * @return true if bound, false if unbound, closed, or no socket present
	 */
	synchronized public boolean isBound() {
		return sock != null && !sock.isClosed() && sock.isBound();
	}
	
	
	/**
	 * Perform a simple sanity-check handshake with a client attached
	 * to a client socket based on the protocol set out in the network.Protocol
	 * class
	 * @param clientSocket -- client socket to handshake with
	 * @return true if handshake succeeds, false otherwise
	 * @throws IOException
	 */
	public boolean doHandshake(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		/* send the server's magic sequence and wait for a reply */
		out.writeObject(Protocol.SERVER_MAGIC);
		out.reset();
		
		/* receive the object from the other side, bailing if it cannot be reconstructed */
		Object rawResponse = null;
		try {
			rawResponse = in.readObject();
		} catch (ClassNotFoundException e) {
			/* definitely not a String if ClassNotFound */
			return false;
		}
		
		/* check that the object is a String instance before casting */
		if (!(rawResponse instanceof String))
			return false;
		
		String response = (String)rawResponse;
		
		/* did the client's response match the expected value? */
		return (response != null && response.equals(Protocol.CLIENT_MAGIC));
	}
	
	
	/**
	 * Run the server
	 * When run, the server will bind a socket to the port
	 * it was constructed with, and begin accepting client
	 * connections, handshaking until it has fulfilled
	 * the number of clients required to play a game
	 */
	public void run() {
		/* FIXME magic constant 2 */
		int totalPlayers = 2;
		
		clientSocks = new Socket[totalPlayers];
		outs = new ObjectOutputStream[totalPlayers];
		ins = new ObjectInputStream[totalPlayers];
		
		
		/* initialise the main server listener socket */
		initialise();
		
		/* main connection accept loop */
		while (!sock.isClosed() && clientCount < clientSocks.length) {
			try {
				Socket client = sock.accept();
				System.out.println("Accepted connection from "+client.getInetAddress());
				outs[clientCount] = new ObjectOutputStream(client.getOutputStream());
				ins[clientCount] = new ObjectInputStream(client.getInputStream());
				
				/* attempt basic sanity-check */
				if (doHandshake(ins[clientCount], outs[clientCount])) {
					System.out.println("Magic phrase exchange succeeded");
					clientSocks[clientCount] = client;
					outs[clientCount].writeObject(players[clientCount].getZone());
					outs[clientCount].writeObject(Event.YOUR_CHARACTER_ID);
					outs[clientCount].writeLong(players[clientCount].getID());
					clientCount++;
				} else {
					System.err.println("Magic phrase exchange failed, disconnecting client");
					client.close();
				}
			} catch (IOException e) {
				System.err.println("Error accpeting client: "+e.getMessage());
			}
		}
		if (sock.isClosed()) {
			stop();
			return;
		}
		System.out.println("All clients connected");
		
		/* spawn two threads for each client */ 
		workerThreads = new Thread[totalPlayers];
		for (int i = 0; i < totalPlayers; i++) {
			/* create+start a receiving thread for this client */
			Thread recvThread = new ServerRecvThread(this, ins[i], outs[i], players[i]);
			recvThread.start();
			
			/* store reference to the thread away */
			this.workerThreads[i] = recvThread;
		}
		
		this.sendThread = new ServerSendThread(this, outs, players); 
		this.tickThread = new TickThread(this);
		this.sendThread.start();
		this.tickThread.start();
	}
	
	/**
	 * Stop the server if it is running
	 */
	public void stop() {
		System.out.print("Server stopping... ");
		if (clientSocks != null) {
			for (Socket client : clientSocks) {
				if (client == null)
					continue;
				try {
					client.close();
				} catch (IOException e) {
					System.err.println("Warning: failed to disconnect a client");
				}
			}
		}
		cleanup();
		System.out.println("stopped");
	}
	
	
	protected void win() {
		for (int i = 0; i < outs.length; i++) {
			try {
				outs[i].writeObject(Event.POPUP_MESSAGE);
				outs[i].writeUTF("You're winner!");
			} catch (IOException e) {
				/* do no thing */
			}
		}
		/* broadcast went out; stop. */
		stop();
	}
	
	/**
	 * Wait for all clients to disconnect
	 */
	public void waitForDisconnect() {
		/* wait for server threads to exit/error-out/whatever */
		for (int i = 0; i < clientCount; i++) {
			try {
				workerThreads[i].join();
			} catch (InterruptedException e) {
				/* we don't really care */
				e.printStackTrace();
			}
		}
		
		/* join the downlink thread */
		try {
			sendThread.join();
		} catch(InterruptedException e) {
			/* don't care */
		}
		
		/* join the world tick thread */
		try {
			/* FIXME tell the thread to stop */
			tickThread.join();
		} catch (InterruptedException e) {
			/* don't care */
		}
	}
	
	/**
	 * Get the number of client connections currently on the server
	 * @return
	 */
	public int getConnectedCount() {
		return clientCount; 
	}
	
	/**
	 * Run the server standalone
	 * @param args
	 */
	public static void main(String[] args) {
		Server s = new Server();
		/* run server */
		s.run();
		s.waitForDisconnect();
		
		/* stop + cleanup if not already done */
		s.stop();
	}

	/**
	 * Get the world the server is serving to clients
	 * @return
	 */
	protected World getWorld() {
		return this.world;
	}
	
	/**
	 * Set the world the server is serving to clients 
	 * @param newWorld -- world to use 
	 */
	protected void setWorld(World newWorld) {
		this.world = newWorld;
	}
}
