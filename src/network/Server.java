package network;

import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import model.World;
import model.Character;
import model.PlayableCharacter;

public class Server {
	
	private ServerThread[] serverThreads;
	private ServerSocket sock;
	private int port;
	private Socket[] clientSocks;
	private int clientCount;
	private World world;
	private Character[] characters;
	
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
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		
		/* send the server's magic sequence and wait for a reply */
		out.writeUTF(Protocol.SERVER_MAGIC);
		String response = in.readUTF();
		
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
	/*
	 * FIXME start the game once all clients are running.
	 * This will likely require creating a slave thread for
	 * each client. Perhaps even two for each client (up+down)
	 * so we can do everything asynchronously. Using non-blocking
	 * socket IO might mitigate the need for so many threads
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
		
		/* spawn a thread for each client */ 
		serverThreads = new ServerThread[totalPlayers];
		for (int i = 0; i < totalPlayers; i++) {
			serverThreads[i] = new ServerThread(i, this, clientSocks[i], characters[i]);
			serverThreads[i].start();
		}
	}
	
	protected World getWorld() {
		return this.world;
	}
	
	public void playerLocation(int x, int y){
		this.serverThreads[0].setPlayer(x,y);
	}
	
	
	/**
	 * Stop the server if it is running
	 */
	public void stop() {
		System.out.print("Server stopping... ");
		for (int i = 0; i < clientCount; i++) {
			serverThreads[i].shutdown();
		}
		System.out.print(" Notified... ");
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
	 * Get the number of client connections currently on the server
	 * @return
	 */
	public int getConnectedCount() {
		return clientCount; 
	}
	
	/* temporary */
	public static void main(String[] args) {
		Server s = new Server();
		s.run();
		s.stop();
	}
}