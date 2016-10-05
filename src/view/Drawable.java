package view;

import util.PointD;

import java.io.Serializable;

public interface Drawable {

    /**
     * Get the path to the image that will be used to draw this object.
     * @return Path to the display image
     */
	public String getDrawImagePath();

    /**
     * Get the x and y position of the object. This is where the object will be drawn on the screen.
     * @return a PointD corresponding to the top left corner of the image
     */
    public PointD getDrawPosition();

    /**
     * Get the "depth" of the drawn object. Used to determine draw order, i.e. objects with a lower depth will be drawn
     * before objects with a higher depth.
     *
     * Could perhaps be renamed?
     * @return the object's depth
     */
    public double getDepth();


	
	
}
