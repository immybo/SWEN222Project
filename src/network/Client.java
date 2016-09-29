package network;


import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import view.GameFrame;

public class Client {
	private String host;
	private int port;
	private Socket sock;
	private DataOutputStream out;
	private DataInputStream in;
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
		String response = in.readUTF();
		
		/* reply with out magic sequence */
		out.writeUTF(Protocol.CLIENT_MAGIC);
		
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
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendEvent(Protocol.Event e) throws IOException {
		List<Protocol.Event> events = Arrays.asList(e.values());
		int index = events.indexOf(e);
		out.writeInt(index);
	}
	
	public void moveForward() throws IOException {
		sendEvent(Protocol.Event.FORWARD);
	}
	
	public void moveBackward() throws IOException {
		sendEvent(Protocol.Event.BACKWARD);
	}
	
	public void rotateClockwise() throws IOException {
		sendEvent(Protocol.Event.ROTATE_CLOCKWISE);
	}
	
	public void rotateAnticlockwise() throws IOException {
		sendEvent(Protocol.Event.ROTATE_ANTICLOCKWISE);
	}
}
