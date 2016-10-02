package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import model.World;
import model.Character;

public class Server {
	
	private ServerSocket sock;
	private int port;
	private Socket[] clientSocks;
	private int clientCount;
	private World world;
	private Character[] characters;
	private Thread[] workerThreads;
	
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
		world = World.testWorld();
		this.characters = new Character[2];
		characters[0] = world.getPupo();
		characters[1] = world.getYelo();
		
	}
	
	/**
	 * Initialise the server
	 * 
	 * @return true on success of already initialised, false on error
	 */
	public boolean initialise() {
		/* has the server already been initialised? */
		if (sock != null && sock.isBound()) {
			return true;
		}
		try {
			sock = new ServerSocket();
			sock.setReuseAddress(true);
			sock.bind(new InetSocketAddress(port));
			System.out.println("Server listening on port "+port);
			return true;
		} catch (IOException e) {
			/* FIXME gui popup instead */
			System.err.println("Error binding on port " + port + ": " + e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * Clean up resources allocated to the server
	 * 
	 * @return true on success, false otherwise
	 */
	private boolean cleanup() {
		try {
			if (sock != null && !sock.isClosed()) {
				sock.close();
			}
			return true;
		} catch (IOException e) {
			/* FIXME gui popup instead */
			System.err.println("Error closing server socket: " + e.getMessage());
			return false;
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
	public boolean doHandshake(Socket clientSocket) throws IOException {
		/* DataInputStream and co. make sending packets of varied data types easy */
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
		
		/* send the server's magic sequence and wait for a reply */
		out.writeObject(Protocol.SERVER_MAGIC);
		
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
		
		/* try to initialise, bailing altogether if it fails */
		if (!initialise()) {
			System.err.println("Initialising failed. Stop.");
			return;
		}
		
		while (!sock.isClosed() && clientCount < clientSocks.length) {
			try {
				Socket client = sock.accept();
				System.out.println("Accepted connection from "+client.getInetAddress());
		
				/* attempt basic sanity-check */
				if (doHandshake(client)) {
					System.out.println("Magic phrase exchange succeeded");
					clientSocks[clientCount] = client;
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
		workerThreads = new Thread[2*totalPlayers];
		for (int i = 0; i < totalPlayers; i++) {
			Thread sendThread;
			Thread recvThread;
			try {
				sendThread = new ServerSendThread(this, clientSocks[i], characters[i]); 
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Error creating game state updater thread, bailing");
				stop();
				return;
			}
			
			recvThread = new ServerRecvThread(this, clientSocks[i], characters[i]);
			
			sendThread.start();
			recvThread.start();
			workerThreads[(2 * i)] = sendThread;
			workerThreads[(2 * i) + 1] = recvThread;
		}
	}
	
	
	/**
	 * Get the world of the hosted game
	 * @return
	 */
	protected World getWorld() {
		return this.world;
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
}