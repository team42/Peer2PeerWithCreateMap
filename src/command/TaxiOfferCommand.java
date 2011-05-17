package command;

import java.net.*;

import database.TripsDAO;

/**
 * A taxi has been offered a trip.
 * 
 * @author Nicolai
 *
 */
public class TaxiOfferCommand extends Command {

	private TripsDAO dao = new TripsDAO();
	
	/**
	 * All data is identified and the trip is added the taxi
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
	public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
		String taxiID = receivedMessage.substring(5, 11);
		String tripID = receivedMessage.substring(11, 21);
		String tripCoord = receivedMessage.substring(21);
		String returnIP = receivePacket.getAddress().getHostAddress();
		
		dao.insertTrip(taxiID, tripID, tripCoord, returnIP);
	}

}
