package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client worker thread for listening to the server's socket, with client sending stuff to the server
 * @author karam
 *
 */
public class ClientThread extends Thread{

	private Client parentClient;

	/* socket connected to server and input stream */
	private Socket socket;
	private DataInputStream in;
	
	private boolean running;

	public ClientThread(Client client, Socket socket) {
		this.socket = socket;
		this.parentClient = client;
	}


	/**
	 * Process data sent by the server
	 * @throws IOException 
	 */
	private void processUpStream() throws IOException {
		/* FIXME receive and decode packet here */
		Protocol.Event packetType = Protocol.Event.values()[in.readInt()];
		switch(packetType){
			case LEVEL_UPDATE:
				/*Parent Client Update window*/
				int x = this.in.readInt();
				int y = this.in.readInt();
				this.parentClient.updatePlayer(x,y);
				break;
			case DISCONNECT:
				this.parentClient.disconnect();
				break;
		}
	}
	
	public boolean isRunning(){
		return this.running;
	}

	/**
	 * Listens to server socket while running
	 */
	@Override
	public void run() {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.running = true;
			while(isRunning()){
				if(in.available() > 0){
					processUpStream();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Notify the thread to stop running
	 */
	public void shutdown(){
		this.running = false;
	}

}
