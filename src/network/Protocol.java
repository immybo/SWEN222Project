package network;

public class Protocol {
	
	/* default TCP port */ 
	public static final int DEFAULT_PORT = 11684;
	
	/* handshake values; these are a method to ensure two things before
	 * regular communication proceeds:
	 *  o The client/server is connected to a Pupo+Yelo server/client
	 *  o The client+server are speaking the same protocol, or version of protocol
	 * 
	 * The magic values should be changed when a non-backwards-compatible
	 * change is made to the protocol, however during our current phase, let's
	 * not bother with that; more of a thing once a stable release is made.
	 * 
	 * Upon connection, server sends SERVER_MAGIC and client checks this happens.
	 * The client then replies CLIENT_MAGIC and the server checks this is valid
	 * If the messages received are not what was expected, the client or server
	 * terminates the connection.
	 */
	public static final String SERVER_MAGIC = "Yes helo does the mcborger contain vegetal\n";
	public static final String CLIENT_MAGIC = "no\n";

	/* delay between game state updates (milliseconds) */
	public static final long UPDATE_DELAY = 50;
	
	/* Events that can be communicated between client and server */
	public static enum Event {
		/* movement */
		FORWARD,
		BACKWARD,
		MOVE_TO_POINT,
		
		/* rotation */
		ROTATE_CLOCKWISE,
		ROTATE_ANTICLOCKWISE,
		
		/* interaction */
		INTERACT,
		ATTACK,
		
		/* downlink messages */
		POPUP_MESSAGE,
		YOUR_CHARACTER_ID,
		
		/* game state actions */
		GAME_SAVE,
		GAME_LOAD,
	};
}
