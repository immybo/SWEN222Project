package model;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import datastorage.Storable;

/**
 * An interaction that can be done on any gate, this interaction will check the player's inventory for a valid key and consume it, therefore opening the gate, if no suitable key exists then show dialog saying no key exists
 *
 * @author Martin Chau
 *
 */
public class UseKey extends Interaction implements Serializable {
	private static final long serialVersionUID = 2006816371515001453L;
	
	public UseKey(KeyGate keyGate){
		setEntity(keyGate);
	}
	
	@Override
	public String getText() {
		return "Use Key";
	}

	@Override
	public String execute(Player p) {
		// get keygate
		KeyGate keyGate = (KeyGate)getEntity();
		
		// get player inventory
		Item[] pInven = p.getInventory().getItems();
		Key matchKey = null;
		// for every item check if the gate can be opened by it
		for(Item i: pInven){
			if(i instanceof Key){
				Key k = (Key) i;
				if(keyGate.openedBy(k)){
					matchKey = k;
					break;
				}
			}
		}
		if(matchKey == null) {
			return "Your key doesnt seem to fit";
		} else {
			//set the gate to open
			keyGate.setPassable(true);
			System.out.println("kg-" + keyGate.isPassable());
			//remove key from play inventory
			p.getInventory().removeItem(matchKey);
			//remove interaction from gate
			keyGate.removeInteraction(this);
			keyGate.addInteraction(new Inspect("It looks to be an open gate."));
			return "Alohomora";
		}
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof UseKey){
			UseKey uk = (UseKey) o;
			if(getEntity().getCoord().equals(uk.getEntity().getCoord()))
				return super.equals(o);
		}
		return false;
	}

	@Override
	public Element toXMLElement(Document doc) {
		return super.toXMLElement(doc, "UseKey");
	}
	
}


