package view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import util.Coord;
import model.Interactable;
import model.Interaction;
import model.Zone;
import network.client.Client;

/**
 * Defines a listener to events on a frame which
 * will relay relevant events to the client
 * given on construction.
 * 
 * @author Robert Campbell
 */
public class GameListener implements KeyListener, MouseListener {
	private Client client; // The client to send events to
	private Zone zone;
	private RenderPanel panel;
	
	/**
	 * Builds a game listener.
	 * 
	 * @param client The client to relay events through.
	 * @param panel The render panel which this listener corresponds to.
	 */
	public GameListener(Client client, RenderPanel panel){
		this.client = client;
		this.panel = panel;
	}
	
	public void setZone(Zone newZone){
		this.zone = newZone;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point clickWorldPoint;
		try {
			clickWorldPoint = panel.getWorldCoordinate(e.getPoint());
		} catch (NoninvertibleTransformException e2) {
			System.err.println("Attempting to click on an area when the render transformation isn't invertible; can't find world coordinate; aborting.");
			return;
		}
		
		// Move to the point
		if(e.getButton() == MouseEvent.BUTTON1){
			try {
				client.moveTo(clickWorldPoint);
			} catch (IOException e1) { // Do nothing?
				e1.printStackTrace();
			}
		}
		// Check for interactables to get the menu of
		else if(e.getButton() == MouseEvent.BUTTON2){
			Interactable interactable = zone.getInteractable(clickWorldPoint);
			if(interactable != null)
				panel.displayInteractionMenu(e.getPoint(), interactable);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		try{
			if(e.getKeyCode() == KeyEvent.VK_W){
				client.moveForward();
			}
			else if(e.getKeyCode() == KeyEvent.VK_S){
				client.moveBackward();
			}
			else if(e.getKeyCode() == KeyEvent.VK_A){
				client.rotateAnticlockwise();
			}
			else if(e.getKeyCode() == KeyEvent.VK_D){
				client.rotateClockwise();
			}
		}
		catch(IOException ex){} // Do nothing?
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Defines something which will listen to interaction menu events
	 * and respond to them by telling the client to relay them to 
	 * the server.
	 * 
	 * @author Robert Campbell
	 */
	public class InteractionMenuListener implements ActionListener {
		private RenderPanel panel;
		private Interaction interaction;
		
		public InteractionMenuListener(RenderPanel panel, Interaction interaction){
			this.panel = panel;
			this.interaction = interaction;
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			try {
				client.interact(interaction);
				panel.removeInteractionMenu(); // menu closes once you click an interaction
			} catch (IOException e1) {
				System.err.println("Unable to perform interaction " + interaction.getText() + " because of network error.");
			}
		}
	}
}
