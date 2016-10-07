package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import network.NetworkError;
import network.client.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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

	private JButton forwardButton;
	private JButton backwardButton;
	private JButton rotateAntiButton;
	private JButton rotateButton;
	

	private Client client;
	private GameFrame gameFrame;

	public InformationPanel(GameFrame gameFrame){
		this.gameFrame = gameFrame;
		this.setLayout(new BorderLayout());

		connectButton = new JButton("Connect");
		connectButton.addActionListener((ActionEvent e)->{
			String host = JOptionPane.showInputDialog("Server hostame or ip:");
			System.err.println("Host is: "+host);
			try {
				if(host != null && client == null) {
					client = new Client(gameFrame, host);
					gameFrame.getRenderPanel().attachToClient(client);
					client.run();
				}
			} catch (NetworkError ne) {
				showNetworkErrorBox(ne);
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

		forwardButton = new JButton("Move Forward");
		forwardButton.addActionListener((ActionEvent e)->{
			moveForward();
		});
		
		backwardButton = new JButton("Move Backward");
		backwardButton.addActionListener((ActionEvent e)->{
			moveBackward();
		});
		rotateAntiButton = new JButton("Rotate Anticlockwise");
		rotateAntiButton.addActionListener((ActionEvent e)->{
			rotateAnticlockwise();
		});
		
		rotateButton = new JButton("Rotate Clockwise");
		rotateButton.addActionListener((ActionEvent e)->{
			rotateClockwise();
		});
		

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 3));

		buttonPanel.add(saveButton, 0);
		buttonPanel.add(loadButton, 1);
		buttonPanel.add(exitButton, 2);
		buttonPanel.add(forwardButton, 3);
		buttonPanel.add(backwardButton, 4);
		buttonPanel.add(rotateAntiButton, 5);
		buttonPanel.add(rotateButton, 6);
		buttonPanel.add(connectButton, 7);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
    public void paint(Graphics g){
        super.paint(g);
    }

    private void exitGame(){
    	if (client != null)
    		client.disconnect();
    	System.exit(0);
    }

    private void loadGame(){
    	System.out.println("Load game button pressed");
    }

    private void saveGame(){
    	System.out.println("Save game button pressed");
    }

    private void moveForward(){
    	try {
			client.moveForward();
		} catch (IOException e) {
			showNetworkErrorBox(e);
		}
    }
    
    private void moveBackward(){
    	try {
			client.moveBackward();
		} catch (IOException e) {
			showNetworkErrorBox(e);
		}
    }
    
    private void rotateAnticlockwise(){
    	try {
			client.rotateAnticlockwise();
		} catch (IOException e) {
			showNetworkErrorBox(e);
		}
    }
    
    private void rotateClockwise(){
    	try {
			client.rotateClockwise();
		} catch (IOException e) {
			showNetworkErrorBox(e);
		}
    }
    
    private void showNetworkErrorBox(Throwable e) {
			JOptionPane.showMessageDialog(this.getParent(), "Network Error: "+e.getMessage(),
				"Error", JOptionPane.OK_OPTION);
    }
}
