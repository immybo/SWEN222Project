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
				int currentTick = 0;
				synchronized (parentServer) {
					/* FIXME don't tick inside server thread; don't want 2 ticks at a time */
					if(currentTick % 4 == 0)
						world.tick();
					if (world.checkForGameWin()) {
						parentServer.win();
					}
					
					out.writeObject(character.getZone());
					out.reset();
					out.writeObject(character);
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
