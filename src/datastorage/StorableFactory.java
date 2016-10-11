package datastorage;

import org.w3c.dom.*;

/**
 * Defines a class which can construct objects from
 * XML elements.
 * 
 * Implements the AbstractFactory pattern (kind of!).
 * Certainly implements the Factory pattern for a lot
 * of classes.
 * 
 * @author Robert Campbell
 * @author Joshua Hurst
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
