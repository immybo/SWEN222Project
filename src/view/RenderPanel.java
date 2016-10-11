package view;
import model.*;
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
        isoTransform = transformForDirection(drawDirection);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    
    
    private AffineTransform transformForDirection(DrawDirection d) {
    	
    	if (zone == null) {
    		return null;
    	}
    	double rotation = Math.PI*0.25;
    	double transX = 512;
    	double transY = 20;
    	AffineTransform t = null;
    	
    	switch(d) {
    	case NW:
    		rotation = Math.PI*0.25;
    		t = AffineTransform.getRotateInstance(rotation);
            t.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
            isoTransform = t;
    		transX = (getScreenCoordinate(zone.getWidth(),0).getX() - getScreenCoordinate(0,zone.getHeight()).getX())/2;
    		transY = 20;
    		break;
    	case NE:
    		rotation = 7*(Math.PI*0.25);
    		t = AffineTransform.getRotateInstance(rotation);
            t.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
            isoTransform = t;
            transX = 0;
    		transY = ((getScreenCoordinate(0,zone.getHeight()).getY() - getScreenCoordinate(zone.getWidth(),0).getY())/2)+20;
    		break;
    	case SE:
    		rotation = 5*Math.PI*0.25;
    		t = AffineTransform.getRotateInstance(rotation);
            t.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
            isoTransform = t;
            transX = (getScreenCoordinate(0,zone.getHeight()).getX() - getScreenCoordinate(zone.getWidth(),0).getX())/2;
            transY = (getScreenCoordinate(0,0).getY() - getScreenCoordinate(zone.getWidth(),zone.getHeight()).getY());
    		break;
    	case SW:
    		rotation = 3*(Math.PI*0.25);
    		t = AffineTransform.getRotateInstance(rotation);
            t.preConcatenate(AffineTransform.getScaleInstance(1,0.574)); //Magic numbers are bad but oh well
            isoTransform = t;
            transX = (getScreenCoordinate(0,0).getX() - getScreenCoordinate(zone.getWidth(),zone.getHeight()).getX());
            transY = ((getScreenCoordinate(zone.getWidth(),0).getY() - getScreenCoordinate(0,zone.getHeight()).getY())/2);
    		break;
    	}
    	
    	
        t.preConcatenate(AffineTransform.getTranslateInstance(transX,transY));
        return t;
    }
    
    /**
     * Creates a render panel without any listeners.
     * Mouse and key input will therefore do nothing.
     */
    public RenderPanel(){
        isoTransform = transformForDirection(drawDirection);
    }
    
    public void rotateClockwise(){
    	drawDirection = DrawDirection.getClockwiseDirection(drawDirection);
    	isoTransform = transformForDirection(drawDirection);
    	repaint();
    }
    public void rotateAntiClockwise(){
    	drawDirection = DrawDirection.getAnticlockwiseDirection(drawDirection);
    	isoTransform = transformForDirection(drawDirection);
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
                Point2D drawPoint = getScreenPosition(d);
                BufferedImage img = ImageIO.read(new File(filename));
                g2.drawImage(img, (int)drawPoint.getX(), (int)(drawPoint.getY()-d.getYOffset()), null);
                //g2.drawString(""+d.getDepthOffset(), (int)drawPoint.getX(), (int)(drawPoint.getY()));
                
                if(d instanceof Enemy){
                	Enemy enemy = (Enemy)d;
                	double proportion = (double)enemy.getRemainingHealth() / enemy.getMaxHealth();

                	g2.setColor(proportion > 0.66 ? Color.GREEN : proportion > 0.33 ? Color.YELLOW : Color.RED);
                	g2.fillRect((int)drawPoint.getX() + 20, (int)drawPoint.getY() - 40, (int)(50*proportion), 10);
                	g2.setColor(Color.BLACK);
                	g2.drawRect((int)drawPoint.getX() + 20, (int)drawPoint.getY() - 40, 50, 10);
                }
            } catch (IOException e) {
                System.err.println("Renderer: Image "+filename+" not found");
            }
        }




        //DRAFT IMAGE

    }
    
    private Point2D getScreenPosition(Drawable d) {
        return applyTransform(d.getDrawPosition().getX()*TILE_WIDTH,d.getDrawPosition().getY()*TILE_HEIGHT);
    }

    private Point2D applyTransform(double x, double y) {
    	if (isoTransform == null) {
    		return new Point2D.Double(0,0);
    	}
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
        	Point2D drawPointA = getScreenPosition(a);
        	Point2D drawPointB = getScreenPosition(b);
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
