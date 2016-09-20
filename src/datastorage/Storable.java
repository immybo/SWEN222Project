package datastorage;

import javax.xml.bind.Element;

/**
 * Defines a class whose objects can be stored in an
 * an XML format.
 * 
 * @author Robert Campbell
 */
public interface Storable {
	/**
	 * Writes this object to an XML element.
	 * 
	 * @return The XML element that was produced.
	 */
	public Element toXMLElement();
}
