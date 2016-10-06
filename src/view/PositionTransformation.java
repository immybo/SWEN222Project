package view;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Provides the ability to transform between coordinates on
 * the graphics pane and coordinates on the world, and vice-
 * versa.
 * 
 * @author Robert Campbell
 */
public class PositionTransformation {
	private double xOffset;
	private double yOffset;
	private double xFactor;
	private double yFactor;
	
	/**
	 * Creates a new transformation.
	 * @param offset The flat offset to apply from a world coordinate to a graphics pane coordinate.
	 * @param factor The factor to apply before the offset is applied.
	 */
	public PositionTransformation(double xFactor, double yFactor, double xOffset, double yOffset){
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xFactor = xFactor;
		this.yFactor = yFactor;
	}
	
	/**
	 * Transforms a point from a world coordinate to a point
	 * on the graphics pane according to this transformation.
	 * @param worldX The x position of the world coordinate.
	 * @param worldY The y position of the world coordinate.
	 * @return The equivalent coordinate on the graphics pane.
	 */
	public Point transform(double worldX, double worldY){
		return new Point(
				(int)(worldX*xFactor + xOffset),
				(int)(worldY*yFactor + yOffset)
		);
	}
	
	/**
	 * Transforms a point from a point on the graphics pane
	 * to a world coordinate according to this transformation,
	 * taking the floor of the resulting coordinates.
	 * @param point The point on the graphics pane to transform.
	 * @return The equivalent, rounded world coordinate.
	 */
	public Point reverseTransform(Point point){
		return new Point(
				(int)((point.x - xOffset)/xFactor),
				(int)((point.y - yOffset)/yFactor)
		);
	}
	
	public static PositionTransformation fromAffineTransform(AffineTransform affineTransform){
		double xFactor = affineTransform.getScaleX();
		double yFactor = affineTransform.getScaleY();
		double xOffset = affineTransform.getTranslateX();
		double yOffset = affineTransform.getTranslateY();
		return new PositionTransformation(xFactor, yFactor, xOffset, yOffset);
	}
}
