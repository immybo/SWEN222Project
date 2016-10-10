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
import java.awt.geom.NoninvertibleTransformException;
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
	private static final int TILE_HEIGHT = 64;
	private static final int TILE_WIDTH = 64;
	
    private volatile Zone zone;
    private volatile Inventory inventory;
    private AffineTransform isoTransform;
    private GameListener listener;
    private DrawDirection drawDirection = DrawDirection.NW;
    
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
    public RenderPanel(){
        isoTransform = AffineTransform.getRotateInstance(Math.PI*0.25);
        isoTransform.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
        isoTransform.preConcatenate(AffineTransform.getTranslateInstance(512,20));
    }
    
    public void rotateClockwise(){
    	drawDirection = DrawDirection.getClockwiseDirection(drawDirection);
    	repaint();
    }
    public void rotateAntiClockwise(){
    	drawDirection = DrawDirection.getAnticlockwiseDirection(drawDirection);
    	repaint();
    }
    
    public void attachToClient(Client client){
        listener = new GameListener(client, this);
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
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());
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
        drawQueue.addAll(zone.getItems());
        drawQueue.addAll(zone.getCharacters());



        while (!drawQueue.isEmpty()) {
        	Drawable d = drawQueue.poll();
            String filename = d.getDrawImagePath(drawDirection);
            if (filename == null) {
                continue;
            }
            try {
                Point2D drawPoint = applyTransform(d.getDrawPosition().getX()*TILE_WIDTH,d.getDrawPosition().getY()*TILE_HEIGHT);
                BufferedImage img = ImageIO.read(new File(filename));
                g2.drawImage(img, (int)drawPoint.getX(), (int)(drawPoint.getY()-d.getYOffset()), null);
                //g2.drawString(""+d.getDepthOffset(), (int)drawPoint.getX(), (int)(drawPoint.getY()));
            } catch (IOException e) {
                System.err.println("Renderer: Image "+filename+" not found");
            }
        }




        //DRAFT IMAGE

    }

    private Point2D applyTransform(double x, double y) {
    	Point2D trans = new Point2D.Double();
    	isoTransform.transform(new Point2D.Double(x,y), trans);
    	return trans;
    }
    
    /**
     * Transforms a coordinate from one on the screen to one
     * in the world, according to the transformation applied
     * the other way.
     * 
     * @param screenCoordinate The coordinate on the screen to transform.
     * @return The equivalent coordinate in the world.
     * @throws NoninvertibleTransformException If the transformation supplied by this render panel can't be inverted.
     */
    public Point getWorldCoordinate(Point screenCoordinate) throws NoninvertibleTransformException{
    	Point2D inverseTrans = new Point2D.Double();
    	isoTransform.createInverse().transform(new Point2D.Double(screenCoordinate.x, screenCoordinate.y), inverseTrans);
    	return new Point((int)(inverseTrans.getX()/TILE_WIDTH), (int)(inverseTrans.getY()/TILE_HEIGHT));
    }
    
    public Point getScreenCoordinate(double worldX, double worldY){
    	Point2D transformed = applyTransform(worldX * TILE_WIDTH, worldY * TILE_HEIGHT);
    	return new Point((int)transformed.getX(), (int)transformed.getY());
    }

    private class DrawableComparator implements Comparator<Drawable> {
        public int compare(Drawable a, Drawable b) {
        	Point2D drawPointA = applyTransform(a.getDrawPosition().getX()*TILE_WIDTH,a.getDrawPosition().getY()*TILE_HEIGHT);
        	Point2D drawPointB = applyTransform(b.getDrawPosition().getX()*TILE_WIDTH,b.getDrawPosition().getY()*TILE_HEIGHT);
        	double aDepth = drawPointA.getY()+a.getDepthOffset();
        	double bDepth = drawPointB.getY()+b.getDepthOffset();
            //to avoid rounding stuffing up the difference
            if ((aDepth - bDepth)==0) {
                return 0;
            } else if ((aDepth - bDepth)>0) {
                return 1;
            } else {
                return -1;
            }
        }
    }


}
