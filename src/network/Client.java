package network;


import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import view.GameFrame;
import network.Protocol.Event;

public class Client {
	private String host;
	private int port;
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ClientThread clientThread;

	private GameFrame frame;
	
	/**
	 * Constructor that also creates a window for the game
	 * @param host
	 */
	public Client(GameFrame frame, String host){
		this(host, Protocol.DEFAULT_PORT);
		this.frame = frame;
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
			sock = new Socket(host, port);
			out = new ObjectOutputStream(sock.getOutputStream());
			in = new ObjectInputStream(sock.getInputStream());
			if (!doHandshake(sock)) {
				System.err.println("Handshaking with server failed");
				sock.close();
				return;
			}
			System.err.println("Starting client thread");
			this.clientThread = new ClientThread(this, this.sock, this.frame);
			this.clientThread.start();
		} catch (IOException e) {
			System.err.printf("Error connecting to %s:%d : %s\n",
						host, port,
						e.getMessage());
		}
	}
	
	public void disconnect(){
		System.out.println("Disconnecting from server");
		this.clientThread.shutdown();
		try {
			this.sock.close();
		} catch (IOException e) {
			/* we don't care */
			e.printStackTrace();
		}
	}
	
	private void sendEvent(Event e) throws IOException {
		out.writeObject(e);
	}
	
	public void moveForward() throws IOException {
		sendEvent(Event.FORWARD);
	}
	
	public void moveBackward() throws IOException {
		sendEvent(Event.BACKWARD);
	}
	
	public void rotateClockwise() throws IOException {
		sendEvent(Event.ROTATE_CLOCKWISE);
	}
	
	public void rotateAnticlockwise() throws IOException {
		sendEvent(Event.ROTATE_ANTICLOCKWISE);
	}
}
