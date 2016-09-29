package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import model.World;
import model.Character;

public class ServerSpamThread extends Thread {
	private World world;
	private Character character;
	private ObjectOutputStream out;
	private Document doc;
	
	public ServerSpamThread(World world, Character character, ObjectOutputStream out) {
		this.world = world;
		this.character = character;
		this.out = out;
		
	
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	doc = docBuilder.newDocument();
	}
	
	@Override
	public void run() {
		while(true) {			
			try {
				System.err.println("Sending level update");
				out.writeObject(character.getZone());
				sleep(1000);
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
