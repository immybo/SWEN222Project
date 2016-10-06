package view;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

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
	private PositionTransformation transformation;
	
	/**
	 * Builds a game listener.
	 * 
	 * @param client The client to relay events through.
	 * @param transformation The transformation between world coordinates and coordinates on the panel.
	 */
	public GameListener(Client client, PositionTransformation transformation){
		this.client = client;
		this.transformation = transformation;
	}
	
	/**
	 * Changes the position transformation of this listener to a new one.
	 * Must be called any time the panel is resized or zoomed.
	 * 
	 * @param newTransformation The new transformation to use.
	 */
	public void changePositionTransformation(PositionTransformation newTransformation){
		transformation = newTransformation;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			Point newPoint = transformation.reverseTransform(e.getPoint());
			try {
				client.moveTo(newPoint);
			} catch (IOException e1) { // Do nothing?
				e1.printStackTrace();
			}
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
