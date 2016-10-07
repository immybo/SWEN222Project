package view;
import model.Interactable;
import model.Interaction;
import model.Inventory;
import model.Zone;
import model.ZoneDrawInfo;
import network.client.Client;
import util.PointD;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Hamish Brown on 28/09/2016.
 */
public class RenderPanel extends JPanel {
    private volatile Zone zone;
    private volatile Inventory inventory;
    private AffineTransform isoTransform;
    private GameFrame frame;
    private GameListener listener;
    
    private JPopupMenu interactionMenu;

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
        if(listener != null) listener.setZone(zone);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    /**
     * Creates a render panel without any listeners.
     * Mouse and key input will therefore do nothing.
     */
    public RenderPanel(GameFrame frame){
    	this.frame = frame;
        this.setPreferredSize(new Dimension(1024,768));
        isoTransform = AffineTransform.getRotateInstance(Math.PI*0.25);
        isoTransform.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
        isoTransform.preConcatenate(AffineTransform.getTranslateInstance(512,20));
    }
    
    public void attachToClient(Client client){
        PositionTransformation transform = PositionTransformation.fromAffineTransform(isoTransform);
        listener = new GameListener(client, transform, this);
        this.addMouseListener(listener);
        this.addKeyListener(listener);
        this.setFocusable(true);
        this.requestFocus();
    }
    
    /**
     * Displays a menu with the given interactions at the given panel position.
     * Removes any interaction menu that is currently being displayed before
     * doing so.
     * 
     * @param point The _panel_ position to display it at.
     * @param interactable The interactable to display the interactions of.
     */
    public void displayInteractionMenu(Point point, Interactable interactable){
    	Interaction[] interactions = interactable.getInteractions();
    	removeInteractionMenu();
    	interactionMenu = new JPopupMenu();
    	
    	for(Interaction interaction : interactions){
    		JMenuItem interactMenuItem = new JMenuItem(interaction.getText());
        	// On clicking any of the interactions, perform the appropriate one with the client
    		interactMenuItem.addActionListener(listener.new InteractionMenuListener(this, interaction));
    		interactionMenu.add(interactMenuItem);
    	}
    	
    	interactionMenu.show(this, point.x, point.y);
    	
    	repaint();
    }
    
    /**
     * Removes any interaction menu that is currently being displayed.
     */
    public void removeInteractionMenu(){
    	interactionMenu = null;
    	repaint();
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
        if (zone == null) {
        	System.err.println("Zone is null; bail");
        	return;
        }

        PriorityQueue<Drawable> drawQueue = new PriorityQueue<>(11,new DrawableComparator());
        drawQueue.addAll(zone.getTiles());
        drawQueue.addAll(zone.getEntities());
        drawQueue.addAll(zone.getCharacters());



        while (!drawQueue.isEmpty()) {
        	Drawable d = drawQueue.poll();
            String filename = d.getDrawImagePath();
            if (filename == null) {
                continue;
            }
            try {
                Point2D drawPoint = applyTransform(d.getDrawPosition().getX()*64,d.getDrawPosition().getY()*64);
                BufferedImage img = ImageIO.read(new File(filename));
                g2.drawImage(img, (int)drawPoint.getX(), (int)(drawPoint.getY()-d.getYOffset()), null);
                //g2.drawString(""+drawPoint.getY()+d.getDepthOffset(), (int)drawPoint.getX(), (int)(drawPoint.getY()-d.getYOffset()));
            } catch (IOException e) {
                System.err.println("Renderer: Image "+filename+" not found");
            }
        }

        /*
        ZoneDrawInfo info = zone.getDrawInformation();
        String[][] tileInfo = info.getTileInfo();
        //g2.setTransform(isoTransform);
        for(int x = 0; x < tileInfo[0].length; x++){
			for(int y = 0; y < tileInfo.length; y++){
				BufferedImage img;
				String filename = "images/" + tileInfo[y][x] + "Iso.png";
				try {
					Point2D drawPoint = applyTransform(x*42,y*42);
					img = ImageIO.read(new File(filename));
					g2.drawImage(img, (int)drawPoint.getX(), (int)drawPoint.getY(), 64, 36, null);
				} catch (IOException e) {
					System.err.println("Renderer: Image "+filename+" not found");
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
		*/


        //DRAFT IMAGE

    }

    private Point2D applyTransform(double x, double y) {
    	Point2D trans = new Point2D.Double();
    	isoTransform.transform(new Point2D.Double(x,y), trans);
    	return trans;
    }

    private class DrawableComparator implements Comparator<Drawable> {
        public int compare(Drawable a, Drawable b) {
        	Point2D drawPointA = applyTransform(a.getDrawPosition().getX()*64,a.getDrawPosition().getY()*64);
        	Point2D drawPointB = applyTransform(b.getDrawPosition().getX()*64,b.getDrawPosition().getY()*64);
        	double aDepth = drawPointA.getY()+a.getDepthOffset();
        	double bDepth = drawPointB.getY()+b.getDepthOffset();
            return(int)(aDepth - bDepth);
        }
    }


}
