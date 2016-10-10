package model;

import java.io.Serializable;

import datastorage.Storable;

/**
 * An interaction that can be done on any gate, this interaction will check the player's inventory for a valid key and consume it, therefore opening the gate, if no suitable key exists then show dialog saying no key exists
 *
 * @author Martin Chau
 *
 */
public class UseKey extends Interaction implements Serializable, Storable {
	private static final long serialVersionUID = 2006816371515001453L;
	private KeyGate keyGate;
	
	public UseKey(KeyGate keyGate){
		this.keyGate = keyGate;
	}

	@Override
	public String getText() {
		return "Use Key";
	}

	@Override
	public void execute(Player p) {
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
			//show dialog of "Your keys dont seem to fit"
		} else {
			//set the gate to open
			keyGate.unlock();
			keyGate.open();

			//keyGate.setPassable(true);

			keyGate.setDrawImagePath(matchKey.getKeyID() + "-open");
			//remove key from play inventory
			p.getInventory().removeItem(matchKey);
			//remove interaction from gate
			keyGate.removeInteraction(this);
			keyGate.addInteraction(new Inspect("It looks to be an open gate."));
		}
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof UseKey){
			UseKey uk = (UseKey) o;
			if(this.keyGate.equals(uk.keyGate))
				return super.equals(o);
		}
		return false;
	}

	/*@Override
	public Element toXMLElement(Document doc){
		Element elem = super.toXMLElement(doc, "UseKey");
		elem.appendChild(keyGate.toXMLElement(doc));
		return elem;
	}

	public static class Factory implements StorableFactory<UseKey> {
		
		private Zone[] zones;
		
		public Factory (Zone[] zones){
			this.zones = zones;
		}
		@Override
		public UseKey fromXMLElement(Element elem) {
			KeyGate keyGate = new KeyGate.Factory(zones).fromXMLElement((Element) elem.getChildNodes().item(0));
			return new UseKey(keyGate);
		}
	}*/

}


