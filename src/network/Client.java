package network;


import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private String host;
	private int port;
	private Socket sock;
	
	/**
	 * Simple constructor connecting to host using default port number
	 * @param host --- host name
	 */
	public Client(String host) {
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
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
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
			if (!doHandshake(sock)) {
				System.err.println("Handshaking with server failed");
			}
		} catch (IOException e) {
			System.err.printf("Error connecting to %s:%d : %s\n",
						host, port,
						e.getMessage());
		}
	}
	
	/* temporary */
	public static void main(String[] args) {
		(new Client("localhost")).run();
	}

}
