package tests;
import java.awt.Point;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import view.PositionTransformation;

public class PositionTransformationTest
    extends TestCase
{
    @SuppressWarnings("deprecation")
	public void testPositionTransformation()
    {
        PositionTransformation p = new PositionTransformation(2.5, 2.5, 3, 3);
        
        double[] x = new double[]{
        	1, -1, 20
        };
        double[] y = new double[]{
        	2, 0, 20
        };
        Point[] expected = new Point[]{
        	new Point(5, 8),
        	new Point(0, 3),
        	new Point(53, 53)
        };
        
        for(int i = 0; i < x.length; i++){
        	Assert.assertEquals(expected[i], p.transform(x[i], y[i]));
        }
        
        
    }
}
