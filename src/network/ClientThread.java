package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import model.Zone;
import network.Protocol.Event;
import view.GameFrame;
import view.RenderPanel;

/**
 * Client worker thread for listening to the server's socket, with client sending stuff to the server
 * @author karam
 *
 */
public class ClientThread extends Thread{

	private Client parentClient;

	/* socket connected to server and input stream */
	private Socket socket;
	private ObjectInputStream in;
	private GameFrame frame;
	
	private boolean running;

	public ClientThread(Client client, Socket socket, GameFrame frame) {
		this.socket = socket;
		this.parentClient = client;
		this.frame = frame;
	}


	/**
	 * Process data sent by the server
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void processDownstream() throws IOException, ClassNotFoundException {
		System.err.println("Waiting for update");
		Object readObj = in.readObject();
		
		System.err.println("Zone update!");
		readObj = in.readObject();
		Zone newZone = (Zone)readObj;
		RenderPanel panel = frame.getRenderPanel();
		panel.setZone(newZone);
		panel.repaint();
		return;
				/*
		switch (packetType) {
			case LEVEL_UPDATE:
				
			case DISCONNECT:
				this.parentClient.disconnect();
				break;
			default:
				System.err.println("Unhandled packet type received from server: "+packetType);
				break;
		}*/
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
			this.in = new ObjectInputStream(socket.getInputStream());
			this.running = true;
			while(isRunning()){
				processDownstream();
			}
		} catch (Exception e) {
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
