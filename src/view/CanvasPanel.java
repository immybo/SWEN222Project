package view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The panel which displays the game world.
 *
 * @author Robert Campbell
 */
public class CanvasPanel extends JPanel {
	public CanvasPanel(){
	}
	
    @Override
    public void paint(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
    }
}
