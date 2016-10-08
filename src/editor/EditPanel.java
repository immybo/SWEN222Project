package editor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import model.*;
import view.RenderPanel;

public class EditPanel extends JPanel {
	private RenderPanel canvas;
	private Zone zone;
	
	private JTextField xPos;
	private JTextField yPos;
	
	private List<JButton> objectButtons;
	
	public EditPanel(RenderPanel canvas){
		this.canvas = canvas;
		
		// TODO allow setting zone size, going between multiple zones
		zone = getEmptyZone(5, 5);
		canvas.setZone(zone);
		
		xPos = new JTextField(5);
		yPos = new JTextField(5);
		
		this.add(xPos);
		this.add(yPos);
		
		objectButtons = new LinkedList<JButton>();
		
		JButton wallButton = new JButton("Wall");
		wallButton.addActionListener((ActionEvent e)->{
			setTile(new WallTile(new Point(getCurrentX(), getCurrentY())));
		});
		
		this.add(wallButton);
	}
	
	private void setTile(Tile newTile){
		zone.setTile(new Point(getCurrentX(), getCurrentY()), newTile);
		
		this.getParent().repaint();
	}
	
	private int getCurrentX(){
		return Integer.parseInt(xPos.getText());
	}
	
	private int getCurrentY(){
		return Integer.parseInt(yPos.getText());
	}
	
	private Zone getEmptyZone(int width, int height){
		Tile[][] zoneTiles = new Tile[height][width];
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				zoneTiles[y][x] = new FloorTile(new Point(x, y));
			}
		}
		// TODO allow zone naming
		return new Zone("defaultZone", zoneTiles);
	}
}
