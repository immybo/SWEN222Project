package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int DEFAULT_PORT = 11684;
	
	private ServerSocket sock;
	private int port;
	
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
	
	
	public void run() {
		/* FIXME magic constant 2 */
		int totalPlayers = 2;
		
		Socket clientSocks[] = new Socket[totalPlayers];
		
		/* try to initialise, bailing altogether if it fails */
		if (!initialise()) {
			System.err.println("Initialising failed. Stop.");
			return;
		}
		
		System.err.println("Server listening on "+port);
		
		int connected = 0;
		while (connected < clientSocks.length) {
			try {
				Socket client = sock.accept();
				
				System.err.println("Accepted connection from "+client.getInetAddress());
				
				clientSocks[connected] = client;
				connected++;
				
				byte[] data = "Hello\n".getBytes();
				client.getOutputStream().write(data);
			} catch (IOException e) {
				System.err.println("Error accepting client connection: "+e.getMessage());
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
