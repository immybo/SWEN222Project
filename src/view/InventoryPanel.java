package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Inventory;
import model.Item;
import model.Player;

public class InventoryPanel extends JPanel {
	private final int columns = 3;
	
	public InventoryPanel() {
		super();
		this.setLayout(new GridLayout(0,columns));
		
	}
	
	public void showInventory(Inventory inventory) {
		this.removeAll();
		for (Item i : inventory.getItems()) {
			if (i == null) {
				continue;
			} else {
				System.out.println(i.getDrawImagePath(DrawDirection.NW));
				String path = i.getDrawImagePath(DrawDirection.NW);
				BufferedImage img = null;
				try {
					img = ImageIO.read(new File(path));
				} catch (IOException e) {

					e.printStackTrace();
					return;
				}
				this.add(new JLabel(new ImageIcon(img)));
			}
		}
		
	}
	
}
