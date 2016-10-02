package model;

import java.awt.Point;
import java.util.HashMap;

import util.Coord;
import util.PointD;

public class ZoneDrawInfo {
	
	public ZoneDrawInfo(String[][] tileInfo, HashMap<Coord, String> entityInfo, HashMap<Point, String> itemInfo) {
		this.tileInfo = tileInfo;
		this.entityInfo = entityInfo;
		this.itemInfo = itemInfo;
	}
	
	private String[][] tileInfo;
	private HashMap<Coord, String> entityInfo;
	private HashMap<Point, String> itemInfo;
	
	public String[][] getTileInfo() {
		return tileInfo;
	}
	public void setTileInfo(String[][] tileInfo) {
		this.tileInfo = tileInfo;
	}
	public HashMap<Coord, String> getEntityInfo() {
		return entityInfo;
	}
	public void setEntityInfo(HashMap<Coord, String> entityInfo) {
		this.entityInfo = entityInfo;
	}
	public HashMap<Point, String> getItemInfo() {
		return itemInfo;
	}
	public void setItemInfo(HashMap<Point, String> itemInfo) {
		this.itemInfo = itemInfo;
	}
}
