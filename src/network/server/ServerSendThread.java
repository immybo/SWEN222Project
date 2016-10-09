package network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import model.Character;
import network.Protocol;

public class ServerSendThread extends Thread {
	private Character character;
	private ObjectOutputStream out;
	private Server parentServer;
	
	public ServerSendThread(Server parentServer, ObjectOutputStream out, Character character) throws IOException {
		this.character = character;
		this.parentServer = parentServer;
		this.out = out;
	}
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				synchronized (parentServer) {
					out.writeObject(character.getZone());
					out.reset();
				}
				sleep(Protocol.UPDATE_DELAY);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
		parentServer.stop();
	}
}
