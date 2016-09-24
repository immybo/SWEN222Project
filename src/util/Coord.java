package util;
/**
 * Represents a combination of PointD for position and Direction for facing
 * 
 * @author Martin Chau
 *
 */
public class Coord {
	public Coord(Direction facing, PointD point) {
		this.facing = facing;
		this.point = point;
	}
	private Direction facing;
	private PointD point;
	public Direction getFacing() {
		return facing;
	}
	public void setFacing(Direction facing) {
		this.facing = facing;
	}
	public PointD getPoint() {
		return point;
	}
	public void setPoint(PointD point) {
		this.point = point;
	}
	
}
