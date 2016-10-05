package model;
	/**
	 * An interaction that can be done on any gate, this interaction will check the player's inventory for a valid key and consume it, therefore opening the gate, if no suitable key exists then show dialog saying no key exists
	 * 
	 * @author Martin Chau
	 *
	 */
	public class UseKey implements Interaction{
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
					}
				}
			}
			if(matchKey == null) {
				//show dialog of "Your keys dont seem to fit"
			} else {
				//set the gate to open
				keyGate.unlock();
				keyGate.open();
				keyGate.setDrawID(matchKey.getKeyID() + "-open");
				//remove key from play inventory
				p.getInventory().removeItem(matchKey);
				//remove interaction from gate
				keyGate.removeInteraction(this);
				keyGate.addInteraction(new Inspect("It looks to be an open gate."));
			}
		}
		

	}


