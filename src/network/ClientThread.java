package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread{

	private Client parentClient;
	
	/* socket connected to server and its streams */
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	public ClientThread(Client client, Socket socket) {
		this.socket = socket;
		this.parentClient = client;
	}
	
	/**
	 * Send server movement details
	 */
	private void processDownStream() {
		/* FIXME send server movement info */
	}
	
	/**
	 * Process data sent by the server
	 */
	private void processUpStream() {
		/* FIXME receive and decode packet here */
		/* FIXME details of how map gamestate will be received */
	}
	
	
	@Override
	public void run() {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			
			if(in.available() > 0){
				processUpStream();
			}
			processDownStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
