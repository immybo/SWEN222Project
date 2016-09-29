package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import model.Inventory;
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

	/* socket connected to server and input stream */
	private Socket socket;
	private ObjectInputStream in;
	private GameFrame frame;
	
	private boolean running;

	public ClientThread(Socket socket, GameFrame frame) {
		this.socket = socket;
		this.frame = frame;
	}


	/**
	 * Process data sent by the server
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void processDownstream() throws IOException, ClassNotFoundException {
		Object readObj = in.readObject();
		RenderPanel panel = frame.getRenderPanel();
		int updateCount = 0;
		do {
			if (readObj instanceof Zone) {
				Zone newZone = (Zone)readObj;
				panel.setZone(newZone);
			} else if (readObj instanceof Inventory) {
				Inventory newInv = (Inventory)readObj;
				panel.setInventory(newInv);
			}
			updateCount++;
		} while (in.available() > 0 && updateCount < 5);
		panel.repaint();
		return;
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
