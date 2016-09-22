package network;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int DEFAULT_PORT = 11684;
	
	private ServerSocket sock;
	private int port;
	private Socket[] clientSocks;
	
	/**
	 * Simple constructor uses default port no
	 */
	public Server() {
		this(DEFAULT_PORT);
	}
	
	/**
	 * Extended constructor specify custom port
	 * @param port --- custom port to listen on
	 */
	public Server(int port) {
		this.port = port;
	}
	
	/**
	 * Initialise the server
	 * 
	 * @return true on success, false otherwise 
	 */
	private boolean initialise() {
		try {
			sock = new ServerSocket();
			sock.setReuseAddress(true);
			sock.bind(new InetSocketAddress(port));
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
		out.writeUTF(Protocol.serverMagic);
		String response = in.readUTF();
		
		/* did the client's response match the expected value? */
		return (response != null && response.equals(Protocol.clientMagic));
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
		
		System.out.println("Server listening on "+port);
		
		int connected = 0;
		while (connected < clientSocks.length) {
			try {
				Socket client = sock.accept();
				System.out.println("Accepted connection from "+client.getInetAddress());
		
				/* attempt basic sanity-check */
				if (!doHandshake(client)) {
					System.err.println("Magic number exchange failed, disconnecting client");
					client.close();
				}
				
				clientSocks[connected] = client;
				connected++;
			} catch (IOException e) {
				System.err.println("Error with client socket: "+e.getMessage());
			}
		}
		
		System.out.println("Server listening on port " + port);
		cleanup();
		System.out.println("Server stopped");
	}
	
	/* temporary */
	public static void main(String[] args) {
		(new Server()).run();
	}
}
