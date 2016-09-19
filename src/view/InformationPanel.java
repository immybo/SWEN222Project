package view;

import javax.swing.*;
import java.awt.*;

/**
 * The panel which displays information that can't be
 * displayed in the canvas, to the player.
 *
 * @author Robert Campbell
 */
public class InformationPanel extends JPanel {
    @Override
    public void paint(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
    }
}
