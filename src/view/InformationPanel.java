package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Inventory;
import model.Item;
import model.Player;
import network.NetworkError;
import network.client.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * The panel which displays information that can't be
 * displayed in the canvas, to the player.
 *
 * @author Robert Campbell
 */
public class InformationPanel extends JPanel {
	private JButton exitButton;
	private JButton saveButton;
	private JButton loadButton;
	private JButton connectButton;

	private JButton rotateAntiButton;
	private JButton rotateButton;
	
	private Client client;
	private GameFrame gameFrame;
	
	private JPanel iconPanel;
	private JTextArea inventoryTextArea;

	public InformationPanel(GameFrame gameFrame){
		this.gameFrame = gameFrame;
		this.setLayout(new BorderLayout());

		connectButton = new JButton("Connect");
		connectButton.addActionListener((ActionEvent e)->{
			String host = JOptionPane.showInputDialog("Server hostame or ip:");
			System.err.println("Host is: "+host);
			try {
				if(host != null) {
					gameFrame.connect(host);
				}
			} catch (NetworkError ne) {
				gameFrame.showNetworkErrorBox(ne);
			}
		});

		exitButton = new JButton("Exit");
		exitButton.addActionListener((ActionEvent e)->{
			int result = JOptionPane.showConfirmDialog(this.getParent(), "Are you sure you want to exit?",
					"", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == 0)
				exitGame();
		});

		saveButton = new JButton("Save Game");
		saveButton.addActionListener((ActionEvent e)->{
			saveGame();
		});

		loadButton = new JButton("Load Game");
		loadButton.addActionListener((ActionEvent e)->{
			int result = JOptionPane.showConfirmDialog(this.getParent(), "Are you sure you want to exit this game and load another?",
					"", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == 0)
				loadGame();
		});
		
		rotateButton = new JButton("<- Rotate View");
		rotateButton.addActionListener((ActionEvent e)->{
			gameFrame.getRenderPanel().rotateAntiClockwise();
		});
		
		rotateAntiButton = new JButton("Rotate View ->");
		rotateAntiButton.addActionListener((ActionEvent e)->{
			gameFrame.getRenderPanel().rotateClockwise();
		});
		
		try{
			iconPanel = new JPanel(){
				private final BufferedImage image = ImageIO.read(new File("images/logo.png"));
				
				@Override
				public void paint(Graphics g){
					super.paint(g);
					g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
				}
			};
			iconPanel.setPreferredSize(new Dimension((int)this.getPreferredSize().getWidth(), 200));
			this.add(iconPanel, BorderLayout.NORTH);
		}
		catch(IOException e){
			// We don't care, just don't add it
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 3));

		buttonPanel.add(saveButton, 0);
		buttonPanel.add(loadButton, 1);
		buttonPanel.add(exitButton, 2);
		buttonPanel.add(connectButton, 3);
		buttonPanel.add(rotateButton, 4);
		buttonPanel.add(rotateAntiButton, 5);

		this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.inventoryTextArea = new JTextArea();
		inventoryTextArea.setEditable(false);
		this.add(inventoryTextArea, BorderLayout.CENTER);
	}
	
	public void setClient(Client newClient){
		client = newClient;
	}
	
	

	@Override
	public void repaint() {
		if (gameFrame != null && gameFrame.getRenderPanel() != null 
				&& gameFrame.getRenderPanel().getPlayer() != null
				&& gameFrame.getRenderPanel().getPlayer().getInventory() != null) {
				showInventory(gameFrame.getRenderPanel().getPlayer().getInventory());
		}
		super.repaint();
	}
	public void showInventory(Inventory inventory) {
		StringBuilder sb = new StringBuilder();
		for (Item i : inventory.getItems()) {
			if (i != null) {
				sb.append(i.getStackSize());
				sb.append(" ");
				sb.append(i.toString());
				sb.append("\n");
			}
		}
		//System.out.println(sb.toString());
		inventoryTextArea.setText(sb.toString());
	}

	private void exitGame(){
		if (client != null)
			client.disconnect();
		System.exit(0);
	}

	private void loadGame(){
		String name = JOptionPane.showInputDialog("What filename would you like to load from?");
		if(name == null || name.length() == 0){
			gameFrame.displayMessage("Invalid filename.");
		}
		try {
			client.loadWorld(name);
		} catch (IOException e) {
			gameFrame.displayMessage("Could not load from file.");
		}
	}

	private void saveGame(){
		String name = JOptionPane.showInputDialog("What filename would you like to save to?");
		if(name == null || name.length() == 0){
			gameFrame.displayMessage("Invalid filename.");
		}
		try {
			client.saveWorld(name);
		} catch (IOException e) {
			gameFrame.displayMessage("Could not save to file.");
		}
	}

	private void moveForward(){
		try {
			client.moveForward();
		} catch (IOException e) {
			gameFrame.showNetworkErrorBox(e);
		}
	}
	
	private void moveBackward(){
		try {
			client.moveBackward();
		} catch (IOException e) {
			gameFrame.showNetworkErrorBox(e);
		}
	}
	
	private void rotateAnticlockwise(){
		try {
			client.rotateAnticlockwise();
		} catch (IOException e) {
			gameFrame.showNetworkErrorBox(e);
		}
	}
	
	private void rotateClockwise(){
		try {
			client.rotateClockwise();
		} catch (IOException e) {
			gameFrame.showNetworkErrorBox(e);
		}
	}
}
