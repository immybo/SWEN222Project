package view;

import java.awt.*;

/**
 * Provides the ability to transform between coordinates on
 * the graphics pane and coordinates on the world, and vice-
 * versa.
 * 
 * @author Robert Campbell
 */
public class PositionTransformation {
	private double offset;
	private double factor;
	
	/**
	 * Creates a new transformation.
	 * @param offset The flat offset to apply from a world coordinate to a graphics pane coordinate.
	 * @param factor The factor to apply before the offset is applied.
	 */
	public PositionTransformation(double offset, double factor){
		this.offset = offset;
		this.factor = factor;
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
				(int)(worldX*factor + offset),
				(int)(worldY*factor + offset)
		);
	}
}
