package network;

/**
 * Wrapper class around Exception to signify an error
 * with the network connection
 * 
 * @author David Phillips
 *
 */
public class NetworkError extends Error {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 7312514606619687578L;
	
	/**
	 * Construct a NetworkError with a message String
	 * @param message
	 */
	public NetworkError(String message) {
		super(message);
	}
	
	/**
	 * Construct a NetworkError with an exception
	 * @param e
	 */
	public NetworkError(Exception e) {
		super(e);
	}
}
