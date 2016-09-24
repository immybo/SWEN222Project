package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

	private final static int DEFAULT_PORT = 11684;

	private Socket socket;

	private DataOutputStream out;
	private DataInputStream in;


	/**
	 * Basic client constructor connecting to addr
	 * @param addr
	 */
	public Client(String addr){

		try{
			InetAddress ip = InetAddress.getByName(addr);
			this.socket = new Socket(ip, DEFAULT_PORT);
			this.out = new DataOutputStream(socket.getOutputStream());
			this.in = new DataInputStream(socket.getInputStream());
			this.doHandshake();
		} catch(IOException e){
			e.printStackTrace();
		}
		

	}

	/**
	 * Basic client constructor connecting to addr, and given port number
	 * @param addr
	 */
	public Client(String addr, int port){

		try{
			InetAddress ip = InetAddress.getByName(addr);
			this.socket = new Socket(ip, port);
			this.out = new DataOutputStream(socket.getOutputStream());
			this.in = new DataInputStream(socket.getInputStream());
			this.doHandshake();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Perform a simple sanity-check handshake with the server
	 * based on the protocol set out in the network.Protocol
	 * class
	 * @return true if handshake succeeds, false otherwise
	 * @throws IOException
	 */
	private boolean doHandshake()throws IOException{
		try {
			/* send the server's magic sequence and wait for a reply */
			String response = in.readUTF();
			this.out.writeUTF(Protocol.clientMagic);

			/* did the client's response match the expected value? */
			return (response != null && response.equals(Protocol.serverMagic));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
