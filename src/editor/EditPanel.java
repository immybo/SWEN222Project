package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

import datastorage.XMLInterface;
import model.*;
import view.RenderPanel;

public class EditPanel extends JPanel {
	private RenderPanel canvas;
	private Zone zone;
	
	private CoordinatePanel coordPanel;
	private ZoneConfigPanel zoneConfigPanel;
	private ComponentPanel componentPanel;
	
	public EditPanel(RenderPanel canvas){
		this.setLayout(new BorderLayout());
		this.add(coordPanel = new CoordinatePanel(), BorderLayout.NORTH);
		this.add(zoneConfigPanel = new ZoneConfigPanel(), BorderLayout.SOUTH);
		this.add(componentPanel = new ComponentPanel(), BorderLayout.CENTER);
		this.canvas = canvas;
		
		// TODO allow going between multiple zones
		zone = getEmptyZone(5, 5, "default");
		canvas.setZone(zone);
	}
	
	private void setTile(Tile newTile){
		if(!coordPanel.hasValidCoordinates()) return;
		
		zone.setTile(coordPanel.getCurrentPoint(), newTile);
		
		repaintAll();
	}
	
	private Zone getEmptyZone(int width, int height, String name){
		Tile[][] zoneTiles = new Tile[height][width];
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				zoneTiles[y][x] = new FloorTile(new Point(x, y));
			}
		}
		return new Zone(name, zoneTiles);
	}
	
	private void repaintAll(){
		this.getParent().repaint();
	}
	
	private void setZone(Zone zone){
		this.zone = zone;
		canvas.setZone(zone);
	}
	
	private class CoordinatePanel extends JPanel {
		private JTextField xPos;
		private JTextField yPos;
		
		private CoordinatePanel(){
			xPos = new JTextField(5);
			yPos = new JTextField(5);
			
			this.add(xPos);
			this.add(yPos);
		}
		
		private boolean hasValidCoordinates(){
			try{
				Integer.parseInt(xPos.getText());
				Integer.parseInt(yPos.getText());
				return true;
			}
			catch(NumberFormatException e){
				return false;
			}
		}
		
		private int getCurrentX(){
			return Integer.parseInt(xPos.getText());
		}
		
		private int getCurrentY(){
			return Integer.parseInt(yPos.getText());
		}
		
		private Point getCurrentPoint(){
			return new Point(getCurrentX(), getCurrentY());
		}
	}
	
	private class ZoneConfigPanel extends JPanel {
		private JTextField zoneName;
		private JTextField xSize;
		private JTextField ySize;
		private JButton saveZoneButton;
		private JButton loadZoneButton;
		private JButton newZoneButton;
		
		private ZoneConfigPanel(){
			this.setLayout(new GridLayout(4, 3));
			
			zoneName = new JTextField(20);
			
			xSize = new JTextField(5);
			ySize = new JTextField(5);
			
			saveZoneButton = new JButton("Save");
			loadZoneButton = new JButton("Load");
			newZoneButton = new JButton("New Zone");
			
			saveZoneButton.addActionListener((ActionEvent e)->{
				String result = JOptionPane.showInputDialog("What filename would you like to save it to?");
				if(result != null){
					XMLInterface.saveToFile(zone, new File(result));
				}
				repaintAll();
			});
			loadZoneButton.addActionListener((ActionEvent e)->{
				String result = JOptionPane.showInputDialog("What filename would you like to load from?");
				if(result != null){
					setZone(XMLInterface.loadFromFile(new Zone.ZoneFactory(), new File(result)));
				}
				repaintAll();
			});
			newZoneButton.addActionListener((ActionEvent e)->{
				if(hasValidSizes()){
					setZone(getEmptyZone(getXSize(), getYSize(), zoneName.getText()));
				}
				repaintAll();
			});
			
			this.add(new JLabel("Width"), 0);
			this.add(xSize, 1);
			this.add(saveZoneButton, 2);
			this.add(new JLabel("Height"), 3);
			this.add(ySize, 4);
			this.add(loadZoneButton, 5);
			this.add(new JLabel("Name"), 6);
			this.add(zoneName, 7);
			this.add(Box.createHorizontalGlue(), 8);
			this.add(Box.createHorizontalGlue(), 9);
			this.add(newZoneButton, 10);
		}
		
		private int getXSize(){
			return Integer.parseInt(xSize.getText());
		}
		
		private int getYSize(){
			return Integer.parseInt(ySize.getText());
		}
		
		private boolean hasValidSizes(){
			try{
				Integer.parseInt(xSize.getText());
				Integer.parseInt(ySize.getText());
				return true;
			}
			catch(NumberFormatException e){
				return false;
			}
		}
	}
	
	private class ComponentPanel extends JPanel {
		private ComponentPanel(){
			JButton wallButton = new JButton("Wall");
			wallButton.addActionListener((ActionEvent e)->{
				if(coordPanel.hasValidCoordinates())
					setTile(new WallTile(coordPanel.getCurrentPoint()));
			});
			JButton floorButton = new JButton("Floor");
			floorButton.addActionListener((ActionEvent e)->{
				if(coordPanel.hasValidCoordinates())
					setTile(new FloorTile(coordPanel.getCurrentPoint()));
			});
			
			this.add(wallButton);
			this.add(floorButton);
		}
	}
}
