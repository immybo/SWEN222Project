package view;
import model.Zone;
import model.ZoneDrawInfo;
import util.PointD;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
//        g2.setTransform(isoTransform);
//        for (int i = 0;i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                g2.drawRect(i*20,j*20,20,20);
//            }
//        }
        
        
        
        //DRAFT IMAGE
        ZoneDrawInfo info = zone.getDrawInformation();
        String[][] tileInfo = info.getTileInfo();
        //g2.setTransform(isoTransform);
        for(int x = 0; x < tileInfo[0].length; x++){
			for(int y = 0; y < tileInfo.length; y++){
				BufferedImage img;
				try {
					Point2D drawPoint = applyTransform(x*42,y*42);
					img = ImageIO.read(new File("images/" + tileInfo[y][x] + "Iso.png"));
					g2.drawImage(img, (int)drawPoint.getX(), (int)drawPoint.getY(), 64, 36, null);
				} catch (IOException e) {
					System.err.println("Image not found for thing");
				}
				
			}
		}
        BufferedImage img;
        try {
			img = ImageIO.read(new File("images/pupo.png"));
			
			g2.drawImage(img, zone.getPupo().getCoord().getPoint().x*60, zone.getPupo().getCoord().getPoint().y*60, 60, 60, null);
		} catch (IOException e) {
			//do nothing cos i dont know man
		}
        
        
        //DRAFT IMAGE

    }
    
    private Point2D applyTransform(double x, double y) {
    	Point2D trans = new Point2D.Double();
    	isoTransform.transform(new Point2D.Double(x,y), trans);
    	return trans;
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
