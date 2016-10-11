package network.server;

import model.World;

public class TickThread extends Thread {
	private static final int TICK_DELAY = 500;
	private Server server;
	
	public TickThread(Server server) {
		this.server = server;
	}
	
	public void run() {
		World world = server.getWorld();
		boolean running = true;
		while (running) {
			try {
				synchronized (server) {
					world.tick();
					if (world.checkForGameWin()) {
						server.win();
					}
				}
				sleep(TICK_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
}
