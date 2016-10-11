package network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import model.Character;
import network.Protocol;

public class ServerSendThread extends Thread {
	private Server parentServer;
	private ObjectOutputStream[] outputStreams;
	private Character[] characters;
	
	public ServerSendThread(Server parentServer, ObjectOutputStream[] outputStreams, Character[] characters) {
		this.parentServer = parentServer;
		this.outputStreams = outputStreams;
		this.characters = characters;
	}
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				/* send each client its zone information */
				for (int i = 0; i < outputStreams.length; i++) {
					ObjectOutputStream o = outputStreams[i];
					
					/* synchronise on the server instance to stop modification
					 * by other threads, eg. world tick and client uplink */
					synchronized (parentServer) {
						/* send the applicable zone to the right player */
						o.writeObject(characters[i].getZone());
						
						/* reset the object stream to avoid it being too "smart"
						 * and caching zone state */
						o.reset();
					}
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
