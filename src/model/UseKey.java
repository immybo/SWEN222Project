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
		}
		

	}


