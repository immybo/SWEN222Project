package util;

import java.io.Serializable;

/**
 * Defines a 2D point with coordinates represented 
 * as doubles.
 * 
 * @author Robert Campbell
 */
public class PointD implements Serializable {
	public final double X;
	public final double Y;
	
	public PointD(double X, double Y){
		this.X = X;
		this.Y = Y;
	}
	
	public double getX(){
		return X;
	}
	
	public double getY(){
		return Y;
	}
	
	public PointD add(PointD other){
		return new PointD(this.X + other.X, this.Y + other.Y);
	}
	
	public PointD add(double X, double Y){
		return new PointD(this.X + X, this.Y + Y);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof PointD){
			PointD p = (PointD)other;
			if(p.X == X && p.Y == Y)
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (int)(X * 7 + Y * 19);
	}
}
