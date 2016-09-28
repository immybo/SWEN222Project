package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	public InformationPanel(){
		this.setLayout(new BorderLayout());
		
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
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 2));
		
		buttonPanel.add(saveButton, 0);
		buttonPanel.add(loadButton, 1);
		buttonPanel.add(exitButton, 2);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
    @Override
    public void paint(Graphics g){
        super.paint(g);
    }
    
    private void exitGame(){
    	System.exit(0);
    }
    
    private void loadGame(){
    	System.out.println("Load game button pressed");
    }
    
    private void saveGame(){
    	System.out.println("Save game button pressed");
    }
}
