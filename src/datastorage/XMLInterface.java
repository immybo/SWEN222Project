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
import org.xml.sax.SAXException;

/**
 * Allows for writing and reading a game's state
 * to and from an XML document.
 * 
 * @author Robert Campbell
 */
public class XMLInterface {
	private XMLInterface(){}
	
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
	 * Writes the current game's state to a file in XML
	 * format and saves it on disk.
	 * @param filename The name of the file to create.
	 * @throws IOException If the filename is invalid.
	 */
	public static void saveGame(String filename) throws IOException{
		// TODO take a World/etc
		
		File file = new File(filename);
		
		// http://stackoverflow.com/questions/4561734/how-to-save-parsed-and-changed-dom-document-in-xml-file
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(file);
			Source input = new DOMSource(doc);

			transformer.transform(input, output);
		} catch (TransformerFactoryConfigurationError | ParserConfigurationException |
				 TransformerException | SAXException e) {
			// We really shouldn't have any exceptions here; they are exceptions with the code
			// in here, so it's best to just crash.
			throw new RuntimeException(e);
		}
	}
}
