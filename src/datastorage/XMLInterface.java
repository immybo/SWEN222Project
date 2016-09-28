package datastorage;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import model.Zone;

/**
 * Allows for writing and reading a game's state
 * to and from an XML document.
 * 
 * @author Robert Campbell
 */
public class XMLInterface {
	private XMLInterface(){}
	
	/**
	 * Saves the given element to the given file as XML through
	 * its toXMLElement(Document) method.
	 * 
	 * @param rootElement The root element of the XML file.
	 * @param file The file to save it to. This file will be overriden if it exists.
	 */
	public static void saveToFile(Storable rootElement, File file){
		try {
			// Build a document
			
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		
    		Document doc = docBuilder.newDocument();
    		
    		// Put the XML in the document
    		
    		doc.appendChild(rootElement.toXMLElement(doc));

    		// Push the document into a file
    		
    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer();
    		DOMSource source = new DOMSource(doc);
    		
    		StreamResult result =  new StreamResult(file);
    		transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
    		throw new IllegalStateException("Error with saving XML to file: \n" + e);
		}
	}
	
	/**
	 * Loads XML from a file, assuming that an instance of type E
	 * is the root element. For example, a Level could be the root
	 * element of a file, in which case a LevelFactory would be
	 * passed.
	 * 
	 * @param rootFactory A factory corresponding to the root element of the file.
	 * @param file The file to read from.
	 * @return The root element that was read as a Java object.
	 */
	public static <E> E loadFromFile(StorableFactory<E> rootFactory, File file){
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse(file);
			return rootFactory.fromXMLElement(doc.getDocumentElement());
		}
		catch(IOException e){
			throw new IllegalArgumentException("Can't load XML from file: \n" + e);
		}
		catch(SAXException e){
			throw new IllegalStateException("Invalid XML in file " + file.getName() + ": \n" + e);
		}
		catch(ParserConfigurationException e){
			throw new IllegalStateException("Error with loading XML from file: \n" + e);
		}
	}
	
	/**
	 * Writes the current game's state to a file in XML
	 * format and saves it on disk.
	 * Uses a default name.
	 */
	public static void saveGame(){
		// Find an appropriate filename for the save
		Date currentTime = new Date();
		String fname = currentTime.toString();
		
		try{
			saveGame(fname);
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * Writes the current game's state to a file in XML
	 * format and saves it on disk.
	 * @param filename The name of the file to create.
	 * @throws IOException If the filename is invalid.
	 */
	public static void saveGame(String filename) throws IOException{
		// TODO take a World/etc
		
		File file = new File(filename);
	}
}
