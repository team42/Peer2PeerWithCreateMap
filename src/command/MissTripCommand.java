package command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import database.*;

/**
 * If another company got the trip first this command will be received to tell
 * the Peer that it didn't get the trip.
 * 
 * @author Nicolai
 *
 */
public class MissTripCommand extends Command {
	
	private TripsDAO dao = new TripsDAO();
	
	/**
	 * The trip ID is identified and the trip is deleted for all taxis.
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
	public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
		String tripID = receivedMessage.substring(5);
		
		dao.deleteTrip(tripID);
	}

}
