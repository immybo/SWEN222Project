package model;

/**
 * A level represents a collection of zones.
 * 
 * @author Robert Campbell
 */
public class Level {
	private String name;
	private Zone[] zones;
	
	/**
	 * Private constructor for a Level.
	 * Levels can only be contructed through
	 * reading from an XML format.
	 * 
	 * @param name The name of this level.
	 * @param zones All zones in this level.
	 */
	private Level(String name, Zone[] zones){
		this.name = name;
		this.zones = zones;
	}
}
