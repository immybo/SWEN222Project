package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

import datastorage.Storable;
import datastorage.StorableFactory;
import model.Zone.ZoneFactory;

/**
 * A level represents a collection of zones.
 * 
 * @author Robert Campbell
 */
public class Level implements Storable {
	private String name;
	private Zone[] zones;
	
	/**
	 * Levels should usually be constructed
	 * through reading from an XML format.
	 * 
	 * @param name The name of this level.
	 * @param zones All zones in this level.
	 */
	public Level(String name, Zone[] zones){
		this.name = name;
		this.zones = zones;
	}
	
	public Zone[] getZones(){
		return zones.clone();
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("level_"+name);
		elem.setAttribute("name", name);
		for(Zone z : zones){
			elem.appendChild(z.toXMLElement(doc));
		}
		return elem;
	}
	
	public static class LevelFactory implements StorableFactory<Level> {
		@Override
		public Level fromXMLElement(Element elem) {
			String name = elem.getAttribute("name");
			List<Zone> zones = new ArrayList<Zone>();
			ZoneFactory factory = new ZoneFactory();
			NodeList nodes = elem.getChildNodes();
			for(int i = 0; i < nodes.getLength(); i++){
				zones.add(factory.fromXMLElement((Element)nodes.item(i)));
			}
			return new Level(name, zones.toArray(new Zone[zones.size()]));
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Level){
			Level level = (Level)other;
			if(!name.equals(level.name)) return false;
			for(int i = 0; i < zones.length; i++)
				if(!zones[i].equals(level.zones[i]))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
}
