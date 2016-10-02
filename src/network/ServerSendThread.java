package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.Character;

public class ServerSendThread extends Thread {
	private Character character;
	private ObjectOutputStream out;
	private Server parentServer;
	
	public ServerSendThread(Server parentServer, Socket socket, Character character) throws IOException {
		this.character = character;
		this.parentServer = parentServer;
		
		/* create an ObjectOutputStream for pushing updates */
		this.out = new ObjectOutputStream(socket.getOutputStream());
	}
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				out.writeObject(character.getZone());
				sleep(Protocol.UPDATE_DELAY);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
		parentServer.stop();
	}
}
