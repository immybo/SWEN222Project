package network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import model.Character;
import model.World;
import network.Protocol;
import network.Protocol.Event;

public class ServerSendThread extends Thread {
	private Character character;
	private ObjectOutputStream out;
	private Server parentServer;
	private World world;
	
	public ServerSendThread(Server parentServer, ObjectOutputStream out, Character character, World world) throws IOException {
		this.character = character;
		this.parentServer = parentServer;
		this.out = out;
		this.world = world;
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
