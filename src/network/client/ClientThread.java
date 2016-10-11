package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;

import model.Inventory;
import model.Player;
import model.Zone;
import view.GameFrame;
import view.RenderPanel;
import network.NetworkError;
import network.Protocol.Event;

/**
 * Client worker thread for listening to the server's socket, with client sending stuff to the server
 * @author karam
 *
 */
public class ClientThread extends Thread{

	/* socket connected to server and input stream */
	private ObjectInputStream in;
	private GameFrame frame;
	
	private boolean running;

	public ClientThread(ObjectInputStream in, GameFrame frame) {
		this.in = in;
		this.frame = frame;
	}


	/**
	 * Process data sent by the server
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void processDownstream() throws IOException, ClassNotFoundException {
		/* FIXME catch EOFException on this read ? */
		Object readObj = in.readObject();
		RenderPanel panel = frame.getRenderPanel();
		if (readObj instanceof Zone) {
			Zone newZone = (Zone)readObj;
			panel.setZone(newZone);
		} else if (readObj instanceof Inventory) {
			Inventory newInv = (Inventory)readObj;
			panel.setInventory(newInv);
		} else if (readObj instanceof Event) {
			Event packetType = (Event)readObj;
			switch (packetType) {
			case YOUR_CHARACTER_ID:
				long ourID = in.readLong();
				frame.getRenderPanel().setPlayer(ourID);
				break;
			case POPUP_MESSAGE:
				String message = in.readUTF();
				frame.showMessageBox(message);
				break;
			default:
				System.err.println("Unhandled packet type "+packetType);
				break;
			}
		}
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
			this.running = true;
			while(isRunning()){
				processDownstream();
			}
		} catch (Exception e) {
			throw new NetworkError(e);
		}
	}
	
	/**
	 * Notify the thread to stop running
	 */
	public void shutdown(){
		this.running = false;
	}
}
