package network;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private String host;
	private int port;
	private Socket sock;
	
	public Client(String host) {
		this(host, Protocol.DEFAULT_PORT);
	}
	
	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
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
