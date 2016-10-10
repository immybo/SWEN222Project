package view;

import util.Direction;

public enum DrawDirection {
	NW,NE,SE,SW;
	
	
	/**
	 * Returns a direction NW for TL, NE for TR etc, depending on the combination
	 * of a facing direction of a entity/player and the facing direction of the map.
	 * e.g if map is NW and player is facing N then then it should draw TR(NE).
	 * 
	 * @param mapDir direction of map
	 * @param objDir direction of entity/player 
	 */
	public static DrawDirection getCompositeDirection(DrawDirection mapDir, Direction objDir){
		if(mapDir == DrawDirection.NW){
			if(objDir.getDirection() == Direction.NORTH) return DrawDirection.NE;
			if(objDir.getDirection() == Direction.EAST) return DrawDirection.SE;
			if(objDir.getDirection() == Direction.SOUTH) return DrawDirection.SW;
			if(objDir.getDirection() == Direction.WEST) return DrawDirection.NW;
		}
		if(mapDir == DrawDirection.NE){
			if(objDir.getDirection() == Direction.NORTH) return DrawDirection.NW;
			if(objDir.getDirection() == Direction.EAST) return DrawDirection.NE;
			if(objDir.getDirection() == Direction.SOUTH) return DrawDirection.SE;
			if(objDir.getDirection() == Direction.WEST) return DrawDirection.SW;
		}
		if(mapDir == DrawDirection.SE){
			if(objDir.getDirection() == Direction.NORTH) return DrawDirection.SW;
			if(objDir.getDirection() == Direction.EAST) return DrawDirection.NW;
			if(objDir.getDirection() == Direction.SOUTH) return DrawDirection.NE;
			if(objDir.getDirection() == Direction.WEST) return DrawDirection.SE;
		}
		if(mapDir == DrawDirection.SW){
			if(objDir.getDirection() == Direction.NORTH) return DrawDirection.SE;
			if(objDir.getDirection() == Direction.EAST) return DrawDirection.SW;
			if(objDir.getDirection() == Direction.SOUTH) return DrawDirection.NW;
			if(objDir.getDirection() == Direction.WEST) return DrawDirection.NE;
		}
		return null; //shouldnt reach
	}
	
	public static DrawDirection getAnticlockwiseDirection(DrawDirection original){
		if(original == DrawDirection.NW) return DrawDirection.NE;
		else if(original == DrawDirection.NE) return DrawDirection.SE;
		else if(original == DrawDirection.SE) return DrawDirection.SW;
		else return DrawDirection.NW;
	}
	
	public static DrawDirection getClockwiseDirection(DrawDirection original){
		return getAnticlockwiseDirection(getAnticlockwiseDirection(getAnticlockwiseDirection(original)));
	}
}
