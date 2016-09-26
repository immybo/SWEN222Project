package datastorage;

import org.w3c.dom.*;

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
	 * @param doc The parent document.
	 * @return The XML element that was produced.
	 */
	public Element toXMLElement(Document doc);
}
