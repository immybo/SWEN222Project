package datastorage;

import javax.xml.bind.Element;

/**
 * Defines a class which can construct objects from
 * XML elements.
 * 
 * @author Robert Campbell
 *
 * @param <T> The object type which this factory can construct.
 */
public interface StorableFactory<T> {
	/**
	 * Constructs an object from an XML element.
	 * 
	 * @param elem The element to construct the object from.
	 * @return The element that was constructed.
	 */
	public T fromXMLElement(Element elem);
}
