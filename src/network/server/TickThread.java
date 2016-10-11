package network.server;

import model.World;

/**
 * Thread to tick the game world periodically
 * 
 * @author David Phillips
 *
 */
public class TickThread extends Thread {
	/* delay */
	private static final int TICK_DELAY = 500;
	
	/* keep running the ticker thread loop? */
	private boolean isRunning;
	
	/* reference to the server owning the world we're ticking */
	private Server server;
	
	/**
	 * Construct a new TickThread to synchronise on sever
	 * @param server -- server object to synchronise main loop on
	 */
	public TickThread(Server server) {
		this.server = server;
	}
	
	/**
	 * Set whether the thread will continue running or not
	 * @param isRunning
	 */
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	/**
	 * Check if the thread is running or will keep running after current iteration
	 * @return
	 */
	public synchronized boolean isRunning() {
		return isRunning;
	}
	
	@Override
	public void run() {
		World world = server.getWorld();
		setRunning(true);
		while (isRunning()) {
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
				setRunning(false);
			}
		}
	}
}
