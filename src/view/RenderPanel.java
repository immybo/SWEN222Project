package view;
import model.Zone;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Hamish Brown on 28/09/2016.
 */
public class RenderPanel extends JPanel {
    private Zone zone;
    private AffineTransform isoTransform;

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public RenderPanel() {
        this.setPreferredSize(new Dimension(1024,768));
        isoTransform = AffineTransform.getRotateInstance(Math.PI*0.25);
        isoTransform.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
        isoTransform.preConcatenate(AffineTransform.getTranslateInstance(512,20));

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g; //so we can do the fancy transform stuff
        g2.setTransform(isoTransform);
        for (int i = 0;i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                g2.drawRect(i*20,j*20,20,20);
            }
        }

    }

    public static void testRender() {
        JFrame testWindow = new JFrame();
        JPanel renderPanel = new RenderPanel();
        testWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testWindow.add(renderPanel);
        testWindow.pack();
        testWindow.setVisible(true);
        renderPanel.setVisible(true);
    }


}
